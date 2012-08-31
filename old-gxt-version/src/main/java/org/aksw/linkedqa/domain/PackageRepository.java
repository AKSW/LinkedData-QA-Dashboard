package org.aksw.linkedqa.domain;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.aksw.commons.collections.Descender;
import org.aksw.commons.collections.DescenderIterator;
import org.aksw.commons.util.Files;
import org.aksw.linkedqa.shared.Package;
import org.aksw.linkedqa.shared.ParserUtils;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extjs.gxt.ui.client.data.BaseModel;


interface NumberParser
	//extends Transformer
{
	Object parse(String str);
}

/*
class PrimitiveTypeParser<T>
{
	public PrimitiveTypeParser(Class<T> clazz) {
		Integer.get
	}
	
	public Object 
}*/


// TODO: Maybe this classes can be replaced by NumberFormat
class DoubleParser
	implements NumberParser
{
	public Object parse(String str) {
		return ParserUtils.tryParseDouble(str);
	}
	
}

class LongParser
	implements NumberParser
{
	private Integer radix = null;
	
	public LongParser() {
	}

	public LongParser(int radix) {
		this.radix = radix;
	}

	
	public Object parse(String str) {
		return ParserUtils.tryParseLong(str, this.radix);
	}
}

class DateFormatParser
	implements NumberParser
{
	public static final DateFormat DefaultUnixDateFormat = new SimpleDateFormat("E MMM d H:m:s z yyyy");
	
	private DateFormat formatter;
	
	public DateFormatParser(DateFormat formatter) {
		this.formatter = formatter;
	}

	public Date parse(String str) {
		//System.out.println(formatter.format(new Date()));
		
		try {
			return formatter.parse(str);
		} catch(Exception e) {
			return null;
		}
	}
}



interface PackageFactory
{
	Package create(String packageId);	
}


class TestFileDescender
implements Descender<File>
{
private FileFilter filter;

public TestFileDescender() {
	this.filter = new FileFilter() {
		public boolean accept(File pathname) {
			return true;
		}};
}

public TestFileDescender(FileFilter filter) {
	this.filter = filter;
}

@SuppressWarnings("unchecked")
public Collection<File> getDescendCollection(File item) {
	System.out.println("Want descend collection for " + item.getAbsolutePath());
    if(!item.isDirectory()) {
    	System.out.println("Not a directory");
        return new ArrayList<File>();
    }

	File[] files = filter != null ? item.listFiles(filter) : item.listFiles();
	Collection<File> tmp = files == null ? Collections.<File>emptyList() : Arrays.asList(files);

	System.out.println("Got " + tmp.size() + " files");
    //System.out.println(tmp);
    return new ArrayList<File>(tmp);
}
}

class FolderPackageFactory
	implements PackageFactory
{
	private static final Logger logger = LoggerFactory.getLogger(FolderPackageFactory.class);
	
	private String repoRoot;
		
	public FolderPackageFactory(String repoRoot) {
		this.repoRoot = repoRoot;		
	}
	
	/**
	 * Attempts to load a package description for the given packageId
	 * 
	 * @param folder
	 * @return
	 */
	public Package create(String packageId) {
		File folder = new File(repoRoot + packageId);
		
		logger.info("Creating a package from " + folder);
		
		Package result = new Package(packageId);
		
		if(!folder.isDirectory()) {
			return null;
		}

		// Load metrics
		for(File file : folder.listFiles()) {
		
			FileName name = FileName.create(file.getAbsolutePath());
			
			if(name.getExtension().equalsIgnoreCase("dat")) {
				result.set(name.getName(), name);
			}

			if(name.getFullName().equalsIgnoreCase("eval-positive.ini")) {
				result.set("eval-positive", name.getName());
			}
		}
		
		return result;
	}
}

 
/**
 * Access to evaluation 
 * 
 */
public class PackageRepository {

	private static final Logger logger = LoggerFactory.getLogger(TaskDao.class);

	private String repoRoot;
	private PackageFactory packageFactory;

	private Map<String, Package> nameToPackage = new HashMap<String, Package>();
	
	private GregorianCalendar lastModificationDate;
	
	public GregorianCalendar getLastModificationDate()
	{
		return lastModificationDate;
	}

	
	public PackageRepository(String repoRoot, GregorianCalendar lastModificationDate) {
		this.repoRoot = repoRoot;
		this.packageFactory = new FolderPackageFactory(repoRoot);
		
		
		this.lastModificationDate = lastModificationDate;
		
		File file = new File(repoRoot);
		logger.info("Loading packages from: " + file.getAbsolutePath());
		
		for(Package item : loadPackages(file, this.packageFactory)) {
			nameToPackage.put(item.getName(), item);
		}
	}
	
	public static void main(String[] args)
	{
		DateFormatParser x = new DateFormatParser(DateFormatParser.DefaultUnixDateFormat);
		System.out.println(x.parse("Thu Aug 18 18:30:42 CEST 2011"));
	}
	
	public Map<String, Package> getMap() {
		return nameToPackage;
	}


	public static boolean isAccepted(List<File> path) {
		/*
		if(!path.isEmpty()) {
			File file = path.get(path.size() - 1);
		}*/
		
		return true;
	}
	
	public static Properties load(File file, Properties out) throws IOException {
		if(out == null) {
			out = new Properties();
		}
		
		InputStream in = new FileInputStream(file);
		try {
			out.load(in);
		}
		finally {
			if(in != null) {
				try {
					in.close();
				} catch (Exception e){
				}
			}
		}
		
		return out;
	}

	
	public static void mergeModel(BaseModel target, BaseModel source)
	{
		if(source != null) {
			target.setProperties(source.getProperties());
		}
	}
	
	public BaseModel getData(String packageId)
			throws InvalidFileFormatException, IOException
	{
		BaseModel result = new BaseModel();

		mergeModel(result, getPositiveEvaluation(packageId));
		mergeModel(result, getMetricsReport(packageId));

		if(result.getProperties().isEmpty()) {
			return null;
		}
		
		return result;
	}
	
	
	public BaseModel getMetricsReport(String packageId)
			throws IOException {

		BaseModel result = new BaseModel();
		
		File file = new File(repoRoot + "/" + packageId + "/report.json" );

		if(!file.exists()) {
			return null;
		}
		
		String str = Files.readContent(file);
		
		result.set("metricsReport", str);
		//String packageId = removePrefix(repoRoot, file.getAbsolutePath());
		result.set("name", packageId);
		result.set("date", new Date(file.lastModified()));
		
		return result;
	}
	
	
	public BaseModel getPositiveEvaluation(String packageId)
			throws InvalidFileFormatException, IOException
	{
		
		// FIXME Move to an appropriate location
		Map<String, NumberParser> propertyToParser = new HashMap<String, NumberParser>();
		propertyToParser.put("precision", new DoubleParser());
		propertyToParser.put("recall", new DoubleParser());

		propertyToParser.put("estimatedPrecisionLowerBound", new DoubleParser());
		propertyToParser.put("estimatedPrecisionUpperBound", new DoubleParser());

		
		propertyToParser.put("duplicates", new LongParser());
		propertyToParser.put("linksetSize", new LongParser());
		propertyToParser.put("startDate", new DateFormatParser(DateFormatParser.DefaultUnixDateFormat));
		propertyToParser.put("endDate", new DateFormatParser(DateFormatParser.DefaultUnixDateFormat));
		propertyToParser.put("linksetErrorCount", new LongParser());
		propertyToParser.put("refsetErrorCount", new LongParser());
		
		
		
		File file = new File(repoRoot + "/" + packageId + "/eval-positive.ini" );
		
		logger.info("Looking for evaluation for " + packageId + " at " + file.getAbsolutePath());
		if(!file.exists()) {
			logger.warn("No evaluation found for " + packageId + " at " + file.getAbsolutePath());
			return null;
		}
		
		Properties p = load(file, null);

		//Ini ini = new Ini(file);
		
		BaseModel result = new BaseModel();
		propertiesToModel(p, "", result);
		
		// TODO Convert datatypes
		for(String propertyName : result.getPropertyNames()) {
			NumberParser parser = propertyToParser.get(propertyName);
			if(parser != null) {
				Object value = parser.parse(result.get(propertyName).toString());
				if(value != null) {
					result.set(propertyName, value);
				}
			}
		}
		
		//iniToModel(ini, "", result);
		
		return result;
	}
	
	
	public static BaseModel propertiesToModel(Properties props, String prefix, BaseModel out) {
		String tmp = (prefix == null || prefix.isEmpty()) ? "" : prefix + ".";
		
		for(Entry<Object, Object> entry : props.entrySet()) {
			out.set(tmp + entry.getKey(), entry.getValue());
		}
		
		return out;
	}
	
	public static BaseModel iniToModel(Ini ini, String prefix, BaseModel out) {
		String tmp = (prefix == null || prefix.isEmpty()) ? "" : prefix + ".";
		
		for(Entry<String, Section> section : ini.entrySet()) {
			for(Entry<String, String> entry : section.getValue().entrySet()) {
			
				out.set(tmp + section.getKey() + "." + entry.getKey(), entry.getValue());
			}
		}
		
		return out;
	}
	

	
	public static Set<Package> loadPackages(File base, PackageFactory packageFactory) {
		Set<Package> result = new HashSet<Package>();
		
		// Scan the file-system recursively for reports.
		DescenderIterator<File> it = new DescenderIterator<File>(base, new TestFileDescender());
		
		logger.info("Scanning for packages with base: " + base.getAbsolutePath());
		
		//Set<File> packages = new HashSet<File>();
		
		while (it.hasNext()) {
			List<File> current = it.next();			
			File file = current.isEmpty() ? null : current.get(current.size() - 1);
			//System.out.println("Path: " + current);

			if(!isAccepted(current)) {
				continue;
			}
   	
			if(!it.canDescend()) {
				// We got a file - if it is a .dat file, add the folder to the output
				/*
				File file = current.get(current.size() - 1);
				
				
				FileName name = FileName.create(file.getName());
				if(name.getExtension().equalsIgnoreCase("dat")) {
					packages.add(file.getParentFile());
				}*/
				
			}
			else {
				it.descend();

				
				// We have not yet reached a leaf, therefore descend
				if(base.toString().equals(file.getAbsolutePath())) {
					continue;
				}
				
				String packageId = TaskDao.removePrefix(base.toString() + "/", file.getAbsolutePath()); //file.getAbsolutePath().substring(base.getAbsolutePath().length() + 1);
				
				/*
				if(packageId == null || packageId.isEmpty()) {
					continue;
				}*/
				
				//System.out.println("pak: " + packageId);
				
				
				
				Package p = packageFactory.create(packageId);
				result.add(p);				
			}
		}
		return result;

		
		/*
		List<TaskDescription> result = new ArrayList<TaskDescription>();
		for(File file : packages) {
			TaskDescription taskDesc = new TaskDescription();
			String packageId = TaskDao.removePrefix(base.toString(), file.getAbsolutePath());
			
			taskDesc.setName(packageId);
			taskDesc.setDate(new Date(file.lastModified()));
			
			
			result.add(taskDesc);			
		}
		
		
		return null;//result;
		*/
	}
	
}

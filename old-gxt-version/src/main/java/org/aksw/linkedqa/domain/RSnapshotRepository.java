package org.aksw.linkedqa.domain;

import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.ui.client.data.BaseModel;

class ItemId {
	private String name;
	private String version;
	
	public ItemId(String name, String version) {
		super();
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}
	
	public String getVersion() {
		return version;
	}
}


/*
class PackageDescription
{
	// name + contents
}*/


/**
 * A collection of packages
 * 
 * 
 * 
 * @author raven
 *
 */
class ItemDescription
	extends BaseModel
{
	public ItemDescription(ItemId id) {
		this.set("id", id);
	}

	public ItemId getId() {
		return this.get("id");
	}
}


class TimeLineCollectionDescription
	extends BaseModel
{
	
	
}



public class RSnapshotRepository<T> {
	private static final Logger logger = LoggerFactory.getLogger(RSnapshotRepository.class);
	
	// Path to the RSnapshot Repository
	private File snapshotRoot;
	
	// Path to the link-specification data within a specific snapshot
	private String repoPath;

	private Factory<File, T> factory;
	
	private Map<String, NavigableMap<Integer, T>> availableRepos = new TreeMap<String, NavigableMap<Integer, T>>();
	
	
	public File getSnapshotRoot() {
		return snapshotRoot;
	}
	
	public String getRepoPath() {
		return repoPath;
	}
	
	public File getFile(String period, int index, String packageId) {
		return new File(snapshotRoot.getAbsoluteFile() + "/" + period + "." + index + "/" + repoPath + "/" + packageId);
	}
	
	
	public RSnapshotRepository(File snapshotRoot, Factory<File, T> factory, String repoPath)
	{
		this.repoPath = repoPath;
		logger.info("Snapshot root is: " + snapshotRoot.getAbsolutePath());
		this.snapshotRoot = snapshotRoot;
		this.factory = factory;
		init();
	}
	
	
	public static void main(String[] args)
		throws Exception
	{
		TaskDao taskDao = new TaskDao();

		List<ChartModel> result = taskDao.getCharts("linkedgeodata-fao.nt");
		taskDao.getCharts("sampled/onlyout/gho-linkedct-country/intervals/0.1-0.15000000000000002");
		
		
		Factory<File, PackageRepository> factory = new Factory<File, PackageRepository>() {
			public PackageRepository create(File file) {

				// TODO URGENT The date should be read from some metadata file
				GregorianCalendar lastModificationDate = new GregorianCalendar();
				lastModificationDate.setTime(new Date(file.lastModified()));

				return new PackageRepository(file.getAbsolutePath() + "/localhost/home/raven/Projects/Current/IntelliJ/24-7-platform/link-specifications/", lastModificationDate);
			}
		};
		
		RSnapshotRepository<PackageRepository> repo = new RSnapshotRepository<PackageRepository>(new File("/home/raven/data/linkspec-repo"), factory, "test---test");
		
		TimeLineCollectionRepository x = new TimeLineCollectionRepository(repo);
		//x.getAllPackages();
		//System.out.println(repo.getLatest("hourly").getMap());
		
		x.getLatestEvaluations();
		
	}
	
	public static <T> Map<String, NavigableMap<Integer, T>> getAvailableRepos(File snapshotRoot, Factory<File, T> factory) {
		Map<String, NavigableMap<Integer, T>> availableRepos = new TreeMap<String, NavigableMap<Integer, T>>();
		
		logger.info("Trying to read repos from path: " + snapshotRoot.getAbsolutePath());
		//
		File[] files = snapshotRoot.listFiles();
		if(files == null) {
			logger.error("Error retrieving files from: " + snapshotRoot.getAbsolutePath() + ". If the directory exists, check the permissions");
			return availableRepos;
		}
		
		for(File file : files) {
			
			FileName name = FileName.create(file.getAbsolutePath());

			Integer index;
			try {
				index = Integer.parseInt(name.getExtension());
			} catch(Exception e) {
				continue;
			}
			
			NavigableMap<Integer, T> map = availableRepos.get(name.getName());
			if(map == null) {
				map = new TreeMap<Integer, T>();
				availableRepos.put(name.getName(), map);
			}

			T o = factory.create(file);
			
			map.put(index, o);
		}

		return availableRepos;
	}
	

	private void init() {
		this.availableRepos = getAvailableRepos(snapshotRoot, factory);
		//System.out.println(availableRepos);
	}
	
	public Set<String> getIntervalTypes() {
		return availableRepos.keySet();
	}
	
	public NavigableMap<Integer, T> getRepos(String name) {
		return availableRepos.get(name);
	}
	
	
	public T get(int index, String name) {
		NavigableMap<Integer, T> map = availableRepos.get(name);
		if(map == null || map.isEmpty()) {
			return null;
		}
		
		return map.get(index);		
	}
	
	
	public T getLatest(String name) {
		return get(availableRepos.get(name).firstKey(), name);
				
		/*
		NavigableMap<Integer, T> map = availableRepos.get(name);
		if(map == null || map.isEmpty()) {
			return null;
		}
		
		return map.firstEntry().getValue();
		*/
	}
	
	/*
	public File getAllPackageDescriptions(String relativePath)
	{
		//return
	}*/
	
	
	
	/*
	private getPackage(String packageId)
	{
		
	}*/
}


package org.aksw.linkedqa.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.commons.collections.DescenderIterator;
import org.aksw.commons.collections.FileDescender;
import org.aksw.linkedqa.shared.MyChart;
import org.aksw.linkedqa.shared.TaskDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.CylinderBarChart;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.common.base.Joiner;


public class TaskDao {

	private static final Logger logger = LoggerFactory.getLogger(TaskDao.class);
	
	private String repoRoot = "/home/raven/Projects/Current/Eclipse/LinkedData-QA/reports/";
	
	
	public static void main(String[] args) {
		TaskDao dao = new TaskDao();
		
		dao.getAllTasks();
	}
	
	
	public static String removePrefix(String prefix, String str) {
		return str.startsWith(prefix) ? str.substring(prefix.length()) : str;
	}
	
	public ChartModel loadChartModel(String filename)
	{
		Reader reader;
		try {
			File file = new File(filename);
			reader = new InputStreamReader(new FileInputStream(file));
			
			String type = file.getName();
			int dotIndex = type.lastIndexOf('.');
			if(dotIndex >= 0) {
				type = type.substring(0, dotIndex);
			}
			
			return loadData(type, reader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	
	public static String getFilename(String filename) {
		int dotIndex = filename.lastIndexOf('.');
		return (dotIndex >= 0) ? filename.substring(0, dotIndex) : filename;
	}
		
	public List<ChartModel> getCharts(String packageId) throws IOException {
		String filename = repoRoot + packageId;
		
		logger.info("Loading charts from location: " + packageId);
		
		List<ChartModel> result = new ArrayList<ChartModel>();
		
		File folder = new File(filename);
		for(File file : folder.listFiles()) {
			FileName name = FileName.create(file.getAbsolutePath());
			
			if(!name.getExtension().equalsIgnoreCase("dat")) {
				continue;
			}
			
			logger.info("Loading dat file: " + file.getAbsolutePath());
			
			Reader reader = new InputStreamReader(new FileInputStream(file));
			ChartModel model = loadData(name.getName(), reader);
			
			result.add(model);
		}
		
		return result;
	}
	
	
	public List<MyChart> getMyCharts(String packageId) throws IOException {
		String path = repoRoot + "" + packageId;
		logger.info("Loading charts from location: " + path);
				
		File folder = new File(path);
		
		return getMyCharts(folder);
	}
	
	
	public static List<MyChart> getMyCharts(File folder)
		throws IOException
	{
		List<MyChart> result = new ArrayList<MyChart>();

		
		for(File file : folder.listFiles()) {
			FileName name = FileName.create(file.getAbsolutePath());
			
			if(!name.getExtension().equalsIgnoreCase("dat")) {
				continue;
			}
			
			logger.info("Loading dat file: " + file.getAbsolutePath());
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			//ChartModel model = loadData(name.getName(), reader);
			String data = "";
			String line;
			while((line = reader.readLine()) != null) {
				if(line.startsWith("#")) {
					continue;
				}
				
				String[] parts = line.split("\\s+");
				String rebuild = Joiner.on("\t").join(parts);

				data += rebuild + "\n";
			}
			
			MyChart item = new MyChart(name.getName(), data);
			System.out.println(item.getData());
			result.add(item);
		}
		
		return result;
	}
	
	
	private ChartModel loadData(String type, Reader reader)
			throws IOException
	{
		ChartModel cm = new ChartModel(type, "font-size: 14px; font-family: Verdana; text-align: center;");
		cm.setBackgroundColour("#fffff5");
		//Legend lg = new Legend(Position.RIGHT, true);
		//lg.setPadding(10);
		//cm.setLegend(lg);
		
		
		
		BarChart bc = new CylinderBarChart();
		//bc.setTooltip("#label# $#val#M<br>#percent#");
		//bc.setsetColours("#ff0000", "#00aa00", "#0000ff", "#ff9900", "#ff00ff");
		
		List<BarChart.Bar> bars = new ArrayList<BarChart.Bar>();
		bars.add(new CylinderBarChart.Bar(1, 3));
		bars.add(new CylinderBarChart.Bar(1, 5));
		bars.add(new CylinderBarChart.Bar(2, 4));

		bc.addBars(bars);
		
				
		cm.addChartConfig(bc);
		
		
		//cm.addCh(listener);
		
		/*
		CSVReader csv = new CSVReader(reader, '\t');
		String[] line;
		while((line = csv.readNext()) != null) {
			System.out.println(line.length);
		}
		*/
		
		
		
		
		return cm;
	}
	
	
	public static boolean isAccepted(List<File> path) {
		/*
		if(!path.isEmpty()) {
			File file = path.get(path.size() - 1);
		}*/
		
		return true;
	}
	
	
	

	
	
	public List<TaskDescription> loadAllTasks() {		
		File base = new File(repoRoot);
		
		// Scan the file-system recursively for reports.
		DescenderIterator<File> it = new DescenderIterator<File>(base, new FileDescender());
		
		Set<File> packages = new HashSet<File>();
		
		while (it.hasNext()) {
			List<File> current = it.next();
			//System.out.println("Path: " + current);

			if(!isAccepted(current)) {
				continue;
			}
   	
			if(!it.canDescend()) {
				// We got a file - if it is a .dat file, add the folder to the output
				File file = current.get(current.size() - 1);
				
				
				FileName name = FileName.create(file.getAbsolutePath());
				if(name.getExtension().equalsIgnoreCase("dat")) {
					packages.add(file.getParentFile());
				}
				
			}
			else {
				// We have not yet reached a leaf, therefore descend
				it.descend();
			}
		}

		
		List<TaskDescription> result = new ArrayList<TaskDescription>();
		for(File file : packages) {
			TaskDescription taskDesc = new TaskDescription();
			String packageId = removePrefix(repoRoot, file.getAbsolutePath());
			
			taskDesc.setName(packageId);
			taskDesc.setDate(new Date(file.lastModified()));
			
			
			result.add(taskDesc);			
		}
		
		
		return result;
	}
	
	public List<TaskDescription> getAllTasks() {		
		return loadAllTasks();
		
		/*
		List<TaskDescription> result = new ArrayList<TaskDescription>();

		result.add(new TaskDescription());

		return result;
		*/
	}
}
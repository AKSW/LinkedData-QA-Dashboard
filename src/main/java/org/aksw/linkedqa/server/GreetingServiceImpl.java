package org.aksw.linkedqa.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.commons.collections.DescenderIterator;
import org.aksw.commons.collections.FileDescender;
import org.aksw.linkedqa.client.GreetingService;
import org.aksw.linkedqa.domain.Factory;
import org.aksw.linkedqa.domain.PackageRepository;
import org.aksw.linkedqa.domain.RSnapshotRepository;
import org.aksw.linkedqa.domain.TaskDao;
import org.aksw.linkedqa.domain.TimeLineCollectionRepository;
import org.aksw.linkedqa.shared.FieldVerifier;
import org.aksw.linkedqa.shared.MyChart;
import org.aksw.linkedqa.shared.TaskDescription;
import org.aksw.linkedqa.shared.TimeLinePackage;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.CylinderBarChart;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.ibm.icu.util.GregorianCalendar;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	private static final Logger logger = LoggerFactory.getLogger(GreetingServiceImpl.class);
	
	private TaskDao taskDao;
	
	
	private TimeLineCollectionRepository timeLineRepo;
	
	//private TimeLinePa
	
	public GreetingServiceImpl()
		throws Exception
	{
		this.taskDao = new TaskDao();
		
		
		/*
		// I used this code for figuring out why files wouldn't be accessible
 
		File base = new File("/home/raven/data/linkspec-repo");
		base = new File("/home/raven/data/linkspec-repo/hourly.0/localhost/home/raven/Projects/Current/IntelliJ/24-7-platform/link-specifications");
		File[] files = base.listFiles();
		if(files == null) {
			logger.info("NO FILES FOUND");
		} else {
			logger.info(files.length + " FILES FOUND");
		}
		
		
		DescenderIterator<File> it = new DescenderIterator<File>(base, new FileDescender());
		
		while (it.hasNext()) {
			List<File> current = it.next();			
			File file = current.isEmpty() ? null : current.get(current.size() - 1);

			System.out.println("Scan: " + file.getAbsolutePath());
   	
			if(it.canDescend()) {
				it.descend();				
			}
		}
		*/
		
		final String snapshotRepoPath = MyApplicationContext.get().getSnapshotRepoPath();
		final String packageRepoPath = MyApplicationContext.get().getPackageRepoPath();
		
		
		
		Factory<File, PackageRepository> factory = new Factory<File, PackageRepository>() {
			public PackageRepository create(File file) {

				// TODO URGENT The date should be read from some metadata file
				GregorianCalendar lastModificationDate = new GregorianCalendar();
				lastModificationDate.setTime(new Date(file.lastModified()));

				return new PackageRepository(file.getAbsolutePath() + packageRepoPath, lastModificationDate);
			}
		};
		
		RSnapshotRepository<PackageRepository> repo = new RSnapshotRepository<PackageRepository>(new File(snapshotRepoPath), factory);
		
		timeLineRepo = new TimeLineCollectionRepository(repo);
		//x.getAllPackages();
	}
	
	
	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid.
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back
			// to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script
		// vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
	
	
	/**
	 * TODO: Think about what the id should be. For now its a filename
	 * 
	 */
	public ChartModel getChartModel(String id)
	{
		return null;
	}


	public List<TaskDescription> getTaskDescriptions() {
		List<TaskDescription> result = taskDao.getAllTasks();
		return result;
	}


	public String test() {
		return "success";
	}

	
	private ChartModel getColumnChartData()
	{
		ChartModel cm = new ChartModel("Degree", "font-size: 14px; font-family: Verdana; text-align: center;");
		cm.setBackgroundColour("#fffff5");
		Legend lg = new Legend(Position.RIGHT, true);
		lg.setPadding(10);
		cm.setLegend(lg);
		
		
		
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
	

	// TODO Maybe replace with some more general class
	public List<MyChart> getCharts2(String packageId) {
		try {
			return taskDao.getMyCharts(packageId);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<ChartModel> getCharts(String packageId) {
		try {
			List<ChartModel> result = taskDao.getCharts(packageId);
			
			logger.info("Got " + result.size() + " charts");
			
			
			return result;
		} catch (IOException e) {
			String str = ExceptionUtils.getFullStackTrace(e);
			logger.error(str);
			//throw new RuntimeException(e);
			//throw new MyException(str);
		}
		
		return new ArrayList<ChartModel>();
	}

	@Override
	protected void doUnexpectedFailure(Throwable e)
	{
		logger.error("Got an unexpected exception", e);
	}
	
	public ChartModel testChart() {
		return getColumnChartData();
	}
	
	
	/**
	 * Return the timeline for a specific package
	 * 
	 * @param packageId
	 * @return
	 */
	public TimeLinePackage getTimeLineEvaluations(String packageId)
	{
		return timeLineRepo.getTimeLinePackage(packageId);
	}
	
	/**
	 * Returns metada about the snapshots
	 * 
	 * @return
	 */
	public Set<BaseModel> getSnapshots() {
		return null;
	}
	
	public Map<String, BaseModel> getLatestEvaluations()
			throws Exception
	{
		return timeLineRepo.getLatestEvaluations();
	}
	
}

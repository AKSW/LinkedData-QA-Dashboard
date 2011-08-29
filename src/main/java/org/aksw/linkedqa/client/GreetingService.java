package org.aksw.linkedqa.client;

import java.util.List;
import java.util.Map;

import org.aksw.linkedqa.shared.MyChart;
import org.aksw.linkedqa.shared.TaskDescription;
import org.aksw.linkedqa.shared.TimeLinePackage;

import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException;

	List<TaskDescription> getTaskDescriptions();
	
	String test();
	//ChartModel testChart();


	List<MyChart> getCharts2(String packageId);
	List<ChartModel> getCharts(String packageId);
	
	public Map<String, BaseModel> getLatestEvaluations() throws Exception;
	
	
	TimeLinePackage getTimeLineEvaluations(String packageId);
}

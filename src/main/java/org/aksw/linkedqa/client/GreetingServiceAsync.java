package org.aksw.linkedqa.client;

import java.util.List;
import java.util.Map;

import org.aksw.linkedqa.shared.MyChart;
import org.aksw.linkedqa.shared.TaskDescription;
import org.aksw.linkedqa.shared.TimeLinePackage;

import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GreetingServiceAsync {

	void greetServer(String name, AsyncCallback<String> callback);

	void getTaskDescriptions(AsyncCallback<List<TaskDescription>> callback);

	void test(AsyncCallback<String> callback);

	void getCharts2(String packageId, AsyncCallback<List<MyChart>> callback);
	void getCharts(String packageId, AsyncCallback<List<ChartModel>> callback);

	void getLatestEvaluations(AsyncCallback<Map<String, BaseModel>> callback);

	void getTimeLineEvaluations(String packageId,
			AsyncCallback<TimeLinePackage> callback);

	//void testChart(AsyncCallback<ChartModel> callback);
}

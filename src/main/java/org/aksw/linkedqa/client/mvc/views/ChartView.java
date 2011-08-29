package org.aksw.linkedqa.client.mvc.views;

import org.aksw.linkedqa.client.ChartWidget;
import org.aksw.linkedqa.client.mvc.events.AppEvents;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.google.gwt.user.client.ui.RootPanel;

public class ChartView
	extends View
{
	private ChartWidget chart;
	
	
	//private List<TaskDescription> taskDescs;
	
	public ChartView(Controller controller) {
		super(controller);
	}
	
	public ChartWidget getWidget() {
		return chart;
	}
	
	private void onInit(AppEvent event) {
		chart = new ChartWidget();
	}
	
	
	private void onUiReady(AppEvent event) {
	}
	

	private void onTaskSelectionChanged(AppEvent event) {
		
	}
	
	@Override
	protected void handleEvent(AppEvent event) {
		if(event.getType().equals(AppEvents.Init)) {
			onInit(event);
		} else if(event.getType().equals(AppEvents.UiReady)) {
			onUiReady(event);
		} else if(event.getType().equals(AppEvents.TaskSelectionChanged)) {
			onTaskSelectionChanged(event);
		}
	}

}

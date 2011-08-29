package org.aksw.linkedqa.client.mvc.views;

import org.aksw.linkedqa.client.TaskGrid;
import org.aksw.linkedqa.client.mvc.events.AppEvents;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;

public class OverviewView
	extends View
{
	private TaskGrid taskGrid;

	public OverviewView(Controller controller) {
		super(controller);
	}
	
	public TaskGrid getTaskGrid() {
		return taskGrid;
	}

	@Override
	protected void initialize() {
		taskGrid = new TaskGrid();		
	}
	
	private void onInit(AppEvent event) {
		//private Chart overviewChartView;
		//private LinksetGrid linksetView; 

	}
	
	private void onUiReady(AppEvent event) {
	}

	private void onTaskSelectionChanged(AppEvent event) {

	}

	@Override
	protected void handleEvent(AppEvent event) {
		if (event.getType().equals(AppEvents.Init)) {
			onInit(event);
		} else if(event.getType().equals(AppEvents.UiReady)) {
			onUiReady(event);
		} else if (event.getType().equals(AppEvents.TaskSelectionChanged)) {
			onTaskSelectionChanged(event);
		}
		/*
		else if (event.getType().equals(AppEvents.TasksRetrieved)) {
			onTasksRetrieved(event.get)
		}*/
	}

}

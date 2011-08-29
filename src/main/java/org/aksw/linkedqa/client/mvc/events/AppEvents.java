package org.aksw.linkedqa.client.mvc.events;

import com.extjs.gxt.ui.client.event.EventType;

public class AppEvents {
	public static final EventType Init = new EventType();
	public static final EventType Error = new EventType();
	
	public static final EventType UiReady = new EventType();
	
	public static final EventType TasksRetrieved = new EventType();

	public static final EventType TaskSelectionChanged = new EventType();

	public static final EventType LinksetSelectionChanged = new EventType();
}


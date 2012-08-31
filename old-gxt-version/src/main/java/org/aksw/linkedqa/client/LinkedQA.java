package org.aksw.linkedqa.client;

import org.aksw.linkedqa.client.mvc.controllers.AppController;
import org.aksw.linkedqa.client.mvc.events.AppEvents;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.Model;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Theme;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LinkedQA implements EntryPoint {

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
	  Registry.register(Constants.MAIN_SERVICE, GWT.create(GreetingService.class));
	  Registry.register(Constants.TASK_STORE, new ListStore<ModelData>());

	  Registry.register(Constants.EVALUATION_STORE, new GroupingStore<Model>());
	  
	  
	  GXT.setDefaultTheme(Theme.BLUE, true);

	  
	  Dispatcher dispatcher = Dispatcher.get();
	  dispatcher.addController(new AppController());
	  dispatcher.dispatch(AppEvents.Init);
	  GXT.hideLoadingPanel("loading");	  
	  dispatcher.dispatch(AppEvents.UiReady);
  }
}

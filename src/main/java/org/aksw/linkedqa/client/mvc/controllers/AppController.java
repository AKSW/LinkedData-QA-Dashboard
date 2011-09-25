package org.aksw.linkedqa.client.mvc.controllers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aksw.linkedqa.client.ChartWidget;
import org.aksw.linkedqa.client.Constants;
import org.aksw.linkedqa.client.GreetingServiceAsync;
import org.aksw.linkedqa.client.mvc.events.AppEvents;
import org.aksw.linkedqa.client.mvc.views.ChartView;
import org.aksw.linkedqa.client.mvc.views.LinksetGrid;
import org.aksw.linkedqa.client.mvc.views.TaskGridView;
import org.aksw.linkedqa.shared.MyChart;
import org.aksw.linkedqa.shared.TimeLinePackage;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.event.ChartEvent;
import com.extjs.gxt.charts.client.event.ChartListener;
import com.extjs.gxt.charts.client.model.BarDataProvider;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.ScaleProvider;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.XAxis.XLabels;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.BarChart.BarStyle;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.Model;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;


interface Transformer<I, O> {
	O transform(I input);
}

// TODO We need some class that tells us how to use (load & display) the measures


public class AppController
	extends Controller
{
	private TaskGridView taskView;
	private GreetingServiceAsync service;
	private ChartView chartView;

	
	private Chart overviewChartView;
	private LinksetGrid linksetGrid;
	
	
	private Chart timeLineChart;
	
	private Label noChartLabel;
	
	private LayoutContainer timeLineChartContainer;
	//private Button linksetGrid;
	//private Chart ove

	// Here we need to be aware of our views
	
	// TODO Replace with a proper map
	String[] measureNames = new String[] {"precision", "recall", "duplicates", "linkSetErrorCount"};
	private int activeMeasureIndex = 0;
	
	private String activePackageName = null;
	
	public AppController() {
		registerEventTypes(AppEvents.Init);
		registerEventTypes(AppEvents.Error);
		registerEventTypes(AppEvents.UiReady);
		registerEventTypes(AppEvents.TaskSelectionChanged);
		registerEventTypes(AppEvents.LinksetSelectionChanged);
	}

	@Override
	public void initialize() {
		super.initialize();
		
		//PropertyManager.get();
		//pm.add("precision", new Range<Double>(0, 1));
		
		chartView = new ChartView(this);
		taskView = new TaskGridView(this);

		chartView = new ChartView(this);
		linksetGrid = new LinksetGrid();
		
		overviewChartView = ChartWidget.createChart();
	}

	
	int getValueClass(double value, double scale, int n) {
		return Math.min(n - 1, (int)(value * scale * n));		
	}
	
	
	/**
	 * Returns an object that creates keys for grouping.
	 * 
	 * @param min
	 * @param max
	 * @param n
	 * @return
	 */
	public static Transformer<Number, Integer> createGrouper(final int n, final Number scale) {
		//final double _min = min.doubleValue();
		//final double _max = max.doubleValue();
		
		return new Transformer<Number, Integer>() {
			public Integer transform(Number input) {				
				//double val = Math.max(_input, _min);
				//val = Math.min(val, _max);
				
				//double scale = (_max - _min) / (double)(n - 1);
				
				return (int)(input.doubleValue() * scale.doubleValue() * n);		
			}
		};
	}
	
	ListStore<Model> deriveHistogram(ListStore<? extends Model> store, String attribute, int n) {
		int counts[] = new int[n];
		Arrays.fill(counts, 0);

		
		for(ModelData raw : store.getModels()) {
			Double prec = tryParseDouble(raw.get(attribute));
			if(prec == null) {
				continue;
			}
			
			int x = getValueClass(prec, 1, n);
		
			//GWT.log("xxx = " + prec + "    " + x);
			counts[x]++;
		}

		ListStore<Model> result = new ListStore<Model>();
		for(int i = 0; i < counts.length; ++i) {
			String label = i * counts.length + "%";
			
			Model data = new BaseModel();
			data.set("label", label);
			data.set("count", counts[i]);
			
			result.add(data);
		}

		return result;
	}

	public void addTimeLineChart(ChartModel model, String measureName, ListStore<Model> listStore) {

		BarChart bar = new BarChart(BarStyle.GLASS);
	    bar.setColour("#00aa00");
	    BarDataProvider barProvider = new BarDataProvider(measureName, "snapshotDate");//new BarDataProvider("alphasales", "month");  
	    barProvider.bind(listStore);  
	    bar.setDataProvider(barProvider); 
	    //bar.addChartListener(listener);  
	    model.addChartConfig(bar);		
	    
	    
	    /*
	    ChartModel chartModel = new ChartModel("My Title", "font-size: 23px;font-weight:bold; font-family: Verdana; text-align: center;");

	    //XAxis with label at 45°
	    XAxis xaxis = new XAxis();
	    List<String> axisList = new ArrayList<String>();
	    axisList.add("Axis A");
	    axisList.add("Axis B");
	    axisList.add("Axis C");

	    XLabels XLabelSerie = xaxis.new XLabels(axisList);
	     XLabelSerie.setRotationAngle(-45);
	     xaxis.setLabels(XLabelSerie);

	    chartModel.setXAxis(xaxis);
	    */

	}
	
	
	public ChartModel createTimeLineChartModel(String title)
	{
		ChartModel model = new ChartModel(title,  
		        "font-size: 14px; font-family: Verdana; text-align: center;");  
	    model.setBackgroundColour("#fefefe");  
	    model.setLegend(new Legend(Position.TOP, true));  
	    //model.setScaleProvider(ScaleProvider.ROUNDED_NEAREST_SCALE_PROVIDER);  


	    XAxis xAxis = new XAxis();
	    XLabels xLabels = xAxis.new XLabels();
	    xLabels.setRotationAngle(-45);
	    xAxis.setLabels(xLabels);

	    model.setXAxis(xAxis);

	    YAxis yAxis = new YAxis();
	    yAxis.setMax(1.0);
	    yAxis.setSteps(0.1);
	    model.setYAxis(yAxis);
	    
	    //model.getXAxis().getLabels().setRotationAngle(-45);
	    return model;
	}
	
	
	public void resetOverviewChart(int measureIndex) {
		String measureName = this.measureNames[measureIndex];
		
		ListStore<Model> store = Registry.get(Constants.EVALUATION_STORE);

		
		int n = 10;
		ListStore<Model> histogram = deriveHistogram(store, measureName, n);
		//ListStore<Model> h = deriveHistogram(store, "recall", n);
		
		/*
		histogram.addFilter(new StoreFilter<Model>() {

			public boolean select(Store<Model> store, Model parent, Model item,
					String property) {
				
				Number x = item.get(property);
				if(x == null) {
					return true;
				}
				//GWT.log("Prop = " + property + " Value: " + x);
				return x.doubleValue() >= 1.0;
			}});
		

		histogram.applyFilters("precision");
		*/
		
		
		//histogram.f
		
		String snapshotTitle = "latest snapshot";
		//snapshot at 19-3-2011
		
		String title = measureName + " distribution for " + snapshotTitle;
		
		ChartModel model = new ChartModel(title,  
		        "font-size: 14px; font-family: Verdana; text-align: center;");  
		    model.setBackgroundColour("#fefefe");  
		    model.setLegend(new Legend(Position.TOP, true));  
		    model.setScaleProvider(ScaleProvider.ROUNDED_NEAREST_SCALE_PROVIDER);  
		  
		    
		    ChartListener listener = new ChartListener() {  
		        public void chartClick(ChartEvent ce) {  
		          
		        	//ce.get
		        	
		        	int row = ce.getChartConfig().getValues().indexOf(ce.getDataType());  
		        	int col = ce.getChartModel().getChartConfigs().indexOf(ce.getChartConfig()) + 1;
		          
		        	//MessageBox.info("Magic not implemented yet.", "" + row + ", "  + col + " --- " + ce.getChartConfig().getDataProvider().toString(), null);

		        	GridView gridView = linksetGrid.getGrid().getView();
		        	if(gridView instanceof GroupingView) {
		        		GroupingView groupingView = (GroupingView)gridView;
		        		NodeList<Element> groups = groupingView.getGroups();
		        		
		        		groups.getItem(row).scrollIntoView();
		        	}

		        	
		        	//.getGroups().getItem(0).sc
		        	
		        	//ce.getChartModel().g
		        	
		        	
		          //CellSelectionModel<TeamSales> csm = (CellSelectionModel<TeamSales>) teamSalesGrid.getSelectionModel();  
		         /*
		          if (selRadio.getValue()) {  
		            csm.selectCell(row, col);  
		          } else {  
		            teamSalesGrid.startEditing(row, col);  
		          }*/  
		        }  
		      };  
		    
		    {
		    BarChart bar = new BarChart(BarStyle.GLASS);  
		    bar.setColour("#00aa00");  
		    BarDataProvider barProvider = new BarDataProvider("count", "label");//new BarDataProvider("alphasales", "month");  
		    barProvider.bind(histogram);  
		    bar.setDataProvider(barProvider);  
		    bar.addChartListener(listener);  
		    model.addChartConfig(bar);
		    }

		    /*
		    {
		    BarChart bar = new BarChart(BarStyle.GLASS);  
		    bar.setColour("#aa0000");  
		    BarDataProvider barProvider = new BarDataProvider("count", "label");//new BarDataProvider("alphasales", "month");  
		    barProvider.bind(h);  
		    bar.setDataProvider(barProvider);  
		    //bar.addChartListener(listener);  
		    model.addChartConfig(bar);  
		    }
		    */
		    
		    
		    //overviewChartView.getWidget().addChartModel(model); 
		    overviewChartView.setChartModel(model);
		    /*
		    bar = new BarChart(BarStyle.GLASS);  
		    bar.setColour("#0000cc");  
		    barProvider = new BarDataProvider("betasales");  
		    barProvider.bind(store);  
		    bar.setDataProvider(barProvider);  
		    //bar.addChartListener(listener);  
		    model.addChartConfig(bar);
		    */  
		  
	}
	
	public static Double tryParseDouble(Object o) {
		if(o == null) {
			return null;
		} else if(o instanceof Double) {
			return (Double)o;
		}
		
		
		try {
			return Double.parseDouble(o.toString());
		} catch(Exception e) {
			return null;
		}
	}
	
	
	public void resetView(int measureIndex) {
		activeMeasureIndex = measureIndex;
		resetOverviewChart(measureIndex);
		resetTimeLineChart();
	}
	
	public void onInit(AppEvent event) {		

		// Make sure the other widgets initialize
		forwardToView(taskView, event);
		forwardToView(chartView, event);
		//forwardToView(overviewChartView, event);
		
		
		Viewport viewport = new Viewport();
		viewport.setLayout(new FitLayout());

		
		TabPanel panel = new TabPanel();
		panel.setLayoutData(new FitLayout());
		panel.setResizeTabs(true);
		//panel.setEnableTabScroll(true);
		//panel.setAnimScroll(true);
		//panel.setAutoHeight(true);

		TabItem overviewTab = new TabItem();
		overviewTab.setLayout(new FitLayout());
		overviewTab.setLayoutOnChange(true);
		overviewTab.setClosable(false);
		overviewTab.setText("Overview");
		//overviewTab.setAutoHeight(true);
		//overviewTab.setAutoWidth(true);
		//overviewTab.setSize(500, 500);

		//overviewTab.add(overviewChartView.getWidget());
		
		LayoutContainer overviewPanel = new LayoutContainer();
		overviewPanel.setLayout(new RowLayout()); //Orientation.HORIZONTAL
		overviewPanel.setScrollMode(Scroll.AUTO);
		//overviewPanel.setLayout(new TableLayout());
		//overviewPanel.setSize(500, 500);
		//overviewPanel.setAutoWidth(true);
		//overviewPanel.
		overviewPanel.setLayoutOnChange(true);
		//overviewPanel.si
		
		
		ContentPanel infoPanel = new ContentPanel();
		infoPanel.setFrame(true);
		infoPanel.setHeaderVisible(false);
		infoPanel.setTitle("Test");
		Label label = new Label("This tab provides an overview of the latest statistics of the LATC datasets. Click a bar in the diagram to see the corresponding datasets."); // Select a linkset for seeing its timeline.");
		
		/*
		SimpleComboBox<String> combo = new SimpleComboBox<String>();
		combo.add("Precision");
		combo.add("Recall");
		combo.add("Duplicates");
		
		combo.setSimpleValue("Precision");

		infoPanel.add(combo);
		*/

		final ListBox lb = new ListBox();
		lb.addItem("Precision");
		lb.addItem("Recall");
		lb.addItem("Duplicates");
		lb.addItem("Link set errors");
		
		lb.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				
				
				int index = lb.getSelectedIndex();
				if(index < 0) {
					return;
				}
				resetView(index);
			}
		});
		
		infoPanel.add(lb);
		

		/* Slider for the snapshot date */
		int margins = 30;

		Slider slider = new Slider();
	    slider.setWidth(300);
	    //slider.setIncrement(10);
	    //slider.setMaxValue(200);
	    slider.setClickToChange(true);

	    //infoPanel.add(slider, new FillData(margins));
	    overviewPanel.add(slider);
		
	    slider.addListener(Events.Change, new Listener<SliderEvent>() {
			public void handleEvent(SliderEvent se) {
				
				MessageBox.info("info", "new value is: " + se.getNewValue(), null);
			}
	    });
		

		
		//label.setStyleAttribute("font", "normal 20px courier");
		infoPanel.add(label);
		
		overviewPanel.add(infoPanel);
		
		LayoutContainer tmpContainer = new LayoutContainer();
		tmpContainer.setLayout(new ColumnLayout());

		LayoutContainer chartContainer = new LayoutContainer();
		chartContainer.setLayout(new FitLayout());
		chartContainer.setSize(300, 300);
		
		
		chartContainer.add(overviewChartView);
		tmpContainer.add(chartContainer);
		linksetGrid.setSize(550,300);
		//linksetGrid.setAutoHeight(true);
		tmpContainer.add(linksetGrid);
		
		
		overviewPanel.add(tmpContainer);
		
		
		timeLineChartContainer = new LayoutContainer(new FitLayout());
		timeLineChartContainer.setLayoutOnChange(true);
		//timeLineChart.setChartModel(new ChartModel("oaeuaeoueo"));
		//timeLineChart.setChartModel(model)
		timeLineChartContainer.setSize(850, 300);
		
		timeLineChartContainer.setBorders(true);
		//timeLineChartContainer.set
		
		noChartLabel = new Label("Select a link set for viewing its timeline");
		noChartLabel.setStyleAttribute("font-family", "Verdana");
		noChartLabel.setStyleAttribute("text-align", "center");
		noChartLabel.setStyleAttribute("color", "#aaaaaa");
		//noChartLabel.
		//ttimeLineChartContainer.setV
		
		//font-size: 23px;font-weight:bold; f
		
		//noChartLabel.set
		timeLineChartContainer.add(noChartLabel);
		//timeLineChart = ChartWidget.createChart();
		//timeLineChart.setChartModel(new ChartModel());
		//timeLineChartContainer.add(timeLineChart);
		
		//timeLineChart.setSize(800, 300);
		// Trying to get the chart to auto size... but failed so far
		//timeLineChart.setHeight(300);
		//chartContainer2.add(timeLineChart, new RowData(-1, 1));
		//chartContainer2.setSize(800, 300);
		//chartContainer2.setStyleAttribute("backgrou, value)
		//overviewPanel.add(chartContainer2);
		overviewPanel.add(timeLineChartContainer);
		//overviewPanel.add(timeLineChart);
		
		overviewTab.add(overviewPanel);
		
		
		TabItem metricsTab = new TabItem();
		metricsTab.setLayout(new FitLayout());
		metricsTab.setLayoutOnChange(true);
		metricsTab.setClosable(false);
		metricsTab.setText("Metrics");

		LayoutContainer metricsPanel = new LayoutContainer();
		metricsPanel.setLayout(new RowLayout()); //Orientation.HORIZONTAL
		metricsPanel.setScrollMode(Scroll.AUTO);
		metricsPanel.setLayoutOnChange(true);


		metricsPanel.add(taskView.getTaskGrid());
		metricsPanel.add(chartView.getWidget());
		
		//item.add(new Label("Test Content"));

		
		// TODO: Outliers and statistics 
		
		
		
		metricsTab.add(metricsPanel);
		
		panel.add(overviewTab);
		panel.add(metricsTab);
	

		
		
		//((Viewport)RootPanel.get().getWidget(0)).add(panel);
		
		viewport.add(panel);
		RootPanel.get().add(viewport);
		
		//System.out.println("Initializing application...");
		//super.initialize();

		
		service = (GreetingServiceAsync)Registry.get(Constants.MAIN_SERVICE);
		
		resetEvaluationGrid();
		
		/*
		service.test(new AsyncCallback<String>() {			
			public void onSuccess(String result) {
				
				GWT.log("I got here with result " + result);
				
				Dispatcher.forwardEvent(AppEvents.Error, result);
				//ListStore<ModelData> taskStore = Registry.get(Constants.TASK_STORE);
				//taskStore.add(result);
				
				//GWT.log("Loaded " + taskStore.getCount() + " task descriptions");
				
				//Dispatcher.forwardEvent(AppEvents.TasksRetrieved, result);
			}
			
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent(AppEvents.Error, caught);
			}
		});*/
		


		service.getLatestMetricsEvaluations(new AsyncCallback<Map<String, Model>>() {
			public void onSuccess(Map<String, Model> result) {
				ListStore<Model> taskStore = Registry.get(Constants.TASK_STORE);

				for(Entry<String, Model> entry : result.entrySet()) {
					
					Model model = entry.getValue();
					
					String str = model.get("metricsReport");
					
					GWT.log(str);
					
					JSONValue json = JSONParser.parseStrict(str);
					JSONObject map = json.isObject();

					//Map<String, Object> map = JsonConverter.decode(str);
					
					//GWT.log(map.keySet().toString());
					//GWT.log("ss = " + map.get("sampleSize"));
					model.set("sampled", map.get("sampled").isBoolean());
					model.set("sampleSize", map.get("sampleSize").isNumber());
					model.set("direction", map.get("direction").isNumber());
					
					
					//String o = entry.getValue().get("metricsReport");
					taskStore.add(model);
				}
				//GWT.log("Loaded " + taskStore.getCount() + " task descriptions");
				
				Dispatcher.forwardEvent(AppEvents.TasksRetrieved, result);

				/*
				GWT.log(result.toString());
*/				
				
				
			}

			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent(AppEvents.Error, caught);
			}
		});
		
		
		/*
		service.getTaskDescriptions(new AsyncCallback<List<TaskDescription>>() {			
			public void onSuccess(List<TaskDescription> result) {
				
				GWT.log("Result is " + result);
				ListStore<ModelData> taskStore = Registry.get(Constants.TASK_STORE);
				taskStore.add(result);
				
				//GWT.log("Loaded " + taskStore.getCount() + " task descriptions");
				
				Dispatcher.forwardEvent(AppEvents.TasksRetrieved, result);
			}
			
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent(AppEvents.Error, caught);
			}
		});
		*/

		
		
		taskView.getTaskGrid().getGrid().getSelectionModel().addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<Model>>() {
			public void handleEvent(SelectionChangedEvent<Model> event) {
				// We need to notify some component that updates the diagrams
				//MessageBox.alert("Info", "" + event.getSelectedItem(), null);
				Dispatcher.forwardEvent(AppEvents.TaskSelectionChanged, event.getSelectedItem());
			}
		});
		
		
		
		linksetGrid.getGrid().getSelectionModel().addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<Model>>() {
			public void handleEvent(SelectionChangedEvent<Model> event) {
				// We need to notify some component that updates the diagrams
				//MessageBox.alert("Info", "" + event.getSelectedItem(), null);
				Dispatcher.forwardEvent(AppEvents.LinksetSelectionChanged, event.getSelectedItem());
			}
		});

	}
	
	public void resetEvaluationGrid() {

		//service.get
		
		service.getLatestEvaluations(new AsyncCallback<Map<String, Model>>() {			
			public void onSuccess(Map<String, Model> result) {
			
				//ListStore<Model> evalStore = Registry.get(Constants.EVALUATION_STORE);
				GroupingStore<Model> evalStore = Registry.get(Constants.EVALUATION_STORE);
				
				evalStore.groupBy("precision_group");
				
				Transformer<Number, Integer> grouper = createGrouper(10, 1.0);
				
				for(Entry<String, Model> entry : result.entrySet()) {
					entry.getValue().set("name", entry.getKey());
					
					Model model = entry.getValue();
					double value = model.get("precision");
					model.set("precision_group", grouper.transform(value));

					value = model.get("recall");
					model.set("recall_group", grouper.transform(value));

					
					Double precision = tryParseDouble(entry.getValue().get("precision"));
					if(precision == null) {
						continue;
					}
					
					GWT.log("name :" + entry.getValue().get("name") + " prec: " + entry.getValue().get("precision"));
					
					evalStore.add(entry.getValue());
				}
			
				resetOverviewChart(activeMeasureIndex);
				
				//MessageBox.info("yay", "eee", null);
				//ListStore<ModelData> taskStore = Registry.get(Constants.EVALUATION_STORE);
				//taskStore.add(result);
				
				//GWT.log("Loaded " + taskStore.getCount() + " task descriptions");
				
				//Dispatcher.forwardEvent(AppEvents.TasksRetrieved, result);
			}
			
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent(AppEvents.Error, caught);
			}
		});
	}
	
	
	public void onError(AppEvent event) {
		MessageBox.alert("Error", "" + event.getData(), null);
	}

	public static final DateTimeFormat dateFormatter = DateTimeFormat.getFormat("MMM d y");
	
	public void onLinksetSelectionChanged(AppEvent event) {
		//MessageBox.info("blah", "aeu", null);
		
		Model model = event.getData();
		
		activePackageName = (String)model.get("name");
		//final Format dateFormatter = new SimpleDateFormat("%E %M %y");
		
		resetTimeLineChart();
	}

	public void resetTimeLineChart()
	{
		resetTimeLineChart(activePackageName, this.measureNames[this.activeMeasureIndex]);
	}
	
	public void resetTimeLineChart(String packageName, final String measureName) {
		service.getTimeLineEvaluations(packageName, new AsyncCallback<TimeLinePackage>() {			
			public void onSuccess(TimeLinePackage result) {
				
				ListStore<Model> store = new ListStore<Model>();
				
				for(Entry<Date, Model> entry : result.getMap().entrySet()) {
					//Model tmp = new BaseModel();
					Model tmp = entry.getValue();
					
					String displayDate = dateFormatter.format(entry.getKey());
					
					tmp.set("snapshotDate", displayDate);
					store.add(tmp);
				}
									
				resetTimeLineChart(result.getName(), measureName, store);
			}
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent(AppEvents.Error, caught);
			}
		});
	}

	public void resetTimeLineChart(String name, String measureName, ListStore<Model> store) {

		GWT.log("TimeLine: " + name + ", " + measureName);

		if(timeLineChart != null) {
			timeLineChart.removeFromParent();
		}

		timeLineChart = ChartWidget.createChart();

		ChartModel chartModel = createTimeLineChartModel("Time line for " + name);
		noChartLabel.removeFromParent();
		addTimeLineChart(chartModel, measureName, store);
		timeLineChart.setChartModel(chartModel);


		timeLineChartContainer.add(timeLineChart);

		
		//store.addFilter(filter)
		//MessageBox.info("AOEU", "" + result.toString(), null);
		
		//ListStore<ModelData> taskStore = Registry.get(Constants.TASK_STORE);
		//taskStore.add(result);
		
		//GWT.log("Loaded " + taskStore.getCount() + " task descriptions");
		
		//Dispatcher.forwardEvent(AppEvents.TasksRetrieved, result);
	}

	
	public void onTaskSelectionChanged(AppEvent event) {
		Model model = event.getData();
		String packageId = model.get("name");
		


		service.getCharts2(packageId, new AsyncCallback<List<MyChart>>() {			
			public void onSuccess(List<MyChart> result) {
				
				chartView.getWidget().setModels(result);
				
				//Dispatcher.forwardEvent(AppEvents.Error, );
			}
			
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent(AppEvents.Error, caught);
			}
		});
			
		/*
		service.getCharts(packageId, new AsyncCallback<List<ChartModel>>() {			
			public void onSuccess(List<ChartModel> result) {
				
				//ListStore<ModelData> chartStore = Registry.get(Constants.CHART_STORE);
				//chartStore.add(result);
				
				//chartView.getWidget().setChartModels(result);
				
				//GWT.log("Loaded " + taskStore.getCount() + " task descriptions");
				
				//Dispatcher.forwardEvent(AppEvents.TasksRetrieved, result);
			}
			
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent(AppEvents.Error, caught);
			}
		});*/


	}
	
	@Override
	public void handleEvent(AppEvent event) {
		if(event.getType().equals(AppEvents.Init)) {
			onInit(event);
		} else if(event.getType().equals(AppEvents.Error)) {
			onError(event);
		} else if(event.getType().equals(AppEvents.TaskSelectionChanged)) {
			onTaskSelectionChanged(event);
		} else if(event.getType().equals(AppEvents.LinksetSelectionChanged)) {
			onLinksetSelectionChanged(event);
		}else {
			forwardToView(taskView, event);
			forwardToView(chartView, event);
		}
	}

}

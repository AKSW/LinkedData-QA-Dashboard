package org.aksw.linkedqa.client;

/* 
 * Ext GWT 2.2.4 - Ext for GWT 
 * Copyright(c) 2007-2010, Ext JS, LLC. 
 * licensing@extjs.com 
 *  
 * http://extjs.com/license 
 */

import java.util.ArrayList;
import java.util.List;

import org.aksw.linkedqa.shared.MyChart;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.event.ChartEvent;
import com.extjs.gxt.charts.client.event.ChartListener;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.ScaleProvider;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.CylinderBarChart;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;


public class ChartWidget extends LayoutContainer {
	//LayoutContainer cp;

	//private List<ChartModel> models = new ArrayList<ChartModel>();
	
	
	public ChartWidget() {
		this.setLayout(new FlowLayout());
		this.setLayoutOnChange(true);
		//cp = new LayoutContainer();
	}
	
	/*
	@Override
	protected void initialze() {
		
	}*/
	
	
	public void setModels(List<MyChart> data) {
		List<ChartModel> models = new ArrayList<ChartModel>();
		for(MyChart d : data) {
			models.add(createModel(d));
		}
		
		setChartModels(models);
	}
	
	public ChartModel createModel(MyChart data) {
		ChartModel model = new ChartModel(data.getType(), "font-size: 14px; font-family: Verdana; text-align: center;");
		model.setBackgroundColour("#fffff5");
		model.setScaleProvider(ScaleProvider.ROUNDED_NEAREST_SCALE_PROVIDER);
		//Legend lg = new Legend(Position.RIGHT, true);
		//lg.setPadding(10);
		//cm.setLegend(lg);
		//MessageBox.info("info", "moo", null);
		
		
		
		BarChart bc = new CylinderBarChart();
		//bc.setTooltip("#label# $#val#M<br>#percent#");
		//bc.setsetColours("#ff0000", "#00aa00", "#0000ff", "#ff9900", "#ff00ff");

		String[] lines = data.getData().split("\n");
		
		
		List<BarChart> charts = new ArrayList<BarChart>();

		for(String line : lines) {
			if(line.startsWith("#")) {
				continue;
			}
						
			//List<BarChart.Bar> bars = new ArrayList<BarChart.Bar>();
			
			String[] fields = line.split("\t");
			if(fields.length < 2) {
				continue;
			}
			
			String first = fields[0];
			Number x = Double.parseDouble(first);
			
			for(int i = 0; i < fields.length - 1; ++i) {
				while(charts.size() < (fields.length - 1)) {
					charts.add(new BarChart());
				}
				
				String str = fields[i + 1];
				Number value = Double.parseDouble(str);

				charts.get(i).addBars(new CylinderBarChart.Bar(value));
				//bars.add(new CylinderBarChart.Bar(value));
				
			}
		}		
				
		
		
		//MessageBox.info("info", "" + charts.size(), null);
		for(BarChart chart : charts) {
			model.addChartConfig(chart);
		}
		
		
		//cm.addCh(listener);
		
		/*
		CSVReader csv = new CSVReader(reader, '\t');
		String[] line;
		while((line = csv.readNext()) != null) {
			System.out.println(line.length);
		}
		*/
		
		
		
		
		return model;		
	}
	
	
	public void setChartModels(List<ChartModel> models) {
		removeAll();

		for(ChartModel model : models) {
			addChartModel(model);
		}
	}

	public void addChartModel(ChartModel model) {
		Widget w = createChart(model);
		add(w);
	}
	
	
	public static Chart createChart()
	{
		String url = GWT.isProdMode() ? "" : "../../";
		url += "gxt/chart/open-flash-chart.swf";

		Chart chart = new Chart(url);
		//chart.setBorders(false);		
		return chart;
	}
	
	protected Widget createChart(ChartModel model) {
		
		/*
		if(true) {
			return new Button("hi");
		}*/
		
		//LayoutContainer container = new ContentPanel();
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new FitLayout());
		container.setSize(200, 200);

		//Resizable r = new Resizable(cp);
		//cp.setCollapsible(false);
		//cp.setHeading("Pie chart");
		//cp.setFrame(false);

		String url = "../../";
		url += "gxt/chart/open-flash-chart.swf";

		Chart chart = new Chart(url);
		chart.setBorders(false);
		//chart.setChartModel(getPieChartData());
		chart.setChartModel(model);

		container.add(chart);
		container.setStyleAttribute("float", "left");
		
		//MessageBox.info("here", "here", null);
		return container;
	}
	
	private ChartListener listener = new ChartListener() {

		public void chartClick(ChartEvent ce) {
			Info.display("Chart Clicked", "You selected {0}.",
					"" + ce.getValue());
		}
	};


	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		/*
		add(createChart(createModel(new MyChart("test", "1\t5\n2\t3\n"))));
		add(createChart(createModel(new MyChart("test", "1\t5\n2\t3\n"))));
		add(createChart(createModel(new MyChart("test", "1\t5\n2\t3\n"))));
		*/
	}
	
	
/*
		this.setLayout(new FlowLayout());
		{
			LayoutContainer cp = new LayoutContainer();
			//Resizable r = new Resizable(cp);
			//cp.setCollapsible(false);
			//cp.setHeading("Pie chart");
			//cp.setFrame(false);
			cp.setSize(200, 200);
			cp.setLayout(new FitLayout());
	
			String url = "../../";
			url += "gxt/chart/open-flash-chart.swf";
	
			Chart chart = new Chart(url);
			chart.setBorders(false);
			//chart.setChartModel(getPieChartData());
			chart.setChartModel(getColumnChartData());
	
			cp.add(chart);
			cp.setStyleAttribute("float", "left");
			add(cp, new MarginData(0));
		}
		
		{
			ContentPanel cp = new ContentPanel();
			//Resizable r = new Resizable(cp);
			cp.setCollapsible(false);
			//cp.setHeading("Pie chart");
			cp.setFrame(false);
			cp.setSize(200, 200);
			cp.setLayout(new FitLayout());
	
			String url = "../../";
			url += "gxt/chart/open-flash-chart.swf";
	
			Chart chart = new Chart(url);
			chart.setBorders(false);
			//chart.setChartModel(getPieChartData());
			chart.setChartModel(getColumnChartData());
	
			cp.add(chart);
			cp.setStyleAttribute("float", "left");
			add(cp, new MarginData(0));
		}
		* /
				
	}
	*/

	private ChartModel getPieChartData() {
		ChartModel cm = new ChartModel("Sales by Region",
				"font-size: 14px; font-family: Verdana; text-align: center;");
		cm.setBackgroundColour("#fffff5");
		Legend lg = new Legend(Position.RIGHT, true);
		lg.setPadding(10);
		cm.setLegend(lg);

		
		PieChart pie = new PieChart();
		pie.setAlpha(0.5f);
		pie.setNoLabels(true);
		pie.setTooltip("#label# $#val#M<br>#percent#");
		pie.setColours("#ff0000", "#00aa00", "#0000ff", "#ff9900", "#ff00ff");
		pie.addSlices(new PieChart.Slice(600, "Limes", "Limes"));
		pie.addSlices(new PieChart.Slice(100, "Silk", "Silk"));
		pie.addChartListener(listener);

		cm.addChartConfig(pie);
		return cm;
	}
	
	/*
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
		* /
		
		
		
		
		return cm;
	}
	*/
	
}

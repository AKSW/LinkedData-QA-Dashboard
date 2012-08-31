package org.aksw.linkedqa.client.mvc.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.aksw.linkedqa.client.Constants;
import org.aksw.linkedqa.client.mvc.controllers.AppController;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.Model;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.table.NumberCellRenderer;
import com.extjs.gxt.ui.client.widget.tips.QuickTip;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;


public class LinksetGrid extends ContentPanel {

	private ColumnModel cm;
	
	
	private Grid<Model> grid;
	
	
	public Grid<Model> getGrid() {
		return grid;
	}
	
	
 	public LinksetGrid() {
 		setLayout(new FitLayout());
		//setLayout(new FlowLayout(10));
 		//setLayout(new FlowLayout());
		getAriaSupport().setPresentation(true);

		//final NumberFormat currency = NumberFormat.getCurrencyFormat();
		final NumberFormat percentFormat = NumberFormat.getFormat("0.00");
		final NumberFormat integerFormat = NumberFormat.getFormat("0");
		//final NumberCellRenderer<Grid<Model>> numberRenderer = new NumberCellRenderer<Grid<Model>>(number);
		final NumberCellRenderer<Grid<Model>> percentRenderer = new NumberCellRenderer<Grid<Model>>(percentFormat);
		final NumberCellRenderer<Grid<Model>> integerRenderer = new NumberCellRenderer<Grid<Model>>(integerFormat);
		//final NumberCellRenderer<Grid<Model>> dateRenderer = new NumberCellRenderer<Grid<Model>>(integerFormat);
		
		
		GridCellRenderer<Model> percentCellRenderer = new GridCellRenderer<Model>() {  
		      public String render(Model model, String property, ColumnData config, int rowIndex, int colIndex,  
		          ListStore<Model> store, Grid<Model> grid) {  
		        return percentRenderer.render(null, property, model.get(property));  
		      }  
		    };  
		
		GridCellRenderer<Model> integerCellRenderer = new GridCellRenderer<Model>() {  
		      public String render(Model model, String property, ColumnData config, int rowIndex, int colIndex,  
		          ListStore<Model> store, Grid<Model> grid) {  
		        return integerRenderer.render(null, property, model.get(property));  
		      }  
		};  
		
		GridCellRenderer<Model> dateCellRenderer = new GridCellRenderer<Model>() {  
		      public String render(Model model, String property, ColumnData config, int rowIndex, int colIndex,  
		          ListStore<Model> store, Grid<Model> grid) {
		    	  GWT.log("Property is " + property);
		    	  Date date = model.get(property);
		    	  GWT.log("Date is " + date);
		        return AppController.dateFormatter.format(date);  
		      }  
		};  
		
		GridCellRenderer<Model> packageNameRenderer = new GridCellRenderer<Model>() {  
		      public String render(Model model, String property, ColumnData config, int rowIndex, int colIndex,  
		          ListStore<Model> store, Grid<Model> grid) {  
		        
		    	  String packageId = model.get(property);
		    	  //return //integerRenderer.render(null, property, model.get(property));
		    	  return "<a href='https://github.com/LATC/24-7-platform/tree/master/link-specifications/" + packageId + "' target='_blank'>" + packageId + "</a>";
		      }  
		};  

		    
		//final NumberFormat integer = NumberFormat.getFormat("0");

		
		
		
		ToolBar toolBar = new ToolBar();
		toolBar.getAriaSupport().setLabel("Grid Options");

		toolBar.add(new LabelToolItem("Selection Mode: "));
		final SimpleComboBox<String> type = new SimpleComboBox<String>();
		type.getAriaSupport().setLabelledBy(toolBar.getItem(0).getId());
		type.setTriggerAction(TriggerAction.ALL);
		type.setEditable(false);
		type.setFireChangeEventOnSetValue(true);
		type.setWidth(100);
		type.add("Row");
		type.add("Cell");
		type.setSimpleValue("Row");
		type.addListener(Events.Change, new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent be) {
				boolean cell = type.getSimpleValue().equals("Cell");
				grid.getSelectionModel().deselectAll();
				if (cell) {
					grid.setSelectionModel(new CellSelectionModel<Model>());
				} else {
					grid.setSelectionModel(new GridSelectionModel<Model>());
				}
			}
		});
		toolBar.add(type);

		setTopComponent(toolBar);

		/*
		GridCellRenderer<Stock> change = new GridCellRenderer<Stock>() {
			public String render(Stock model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Stock> store, Grid<Stock> grid) {
				Object o = model.get(property);
				
				String v = "(not set)";
				if(o == null) {
					double val = (Double)o;
					String style = val < 0 ? "red"
							: GXT.isHighContrastMode ? "#00ff5a" : "green";
					v = number.format(val);
					
				}
				
				return "<span qtitle='"
						+ cm.getColumnById(property).getHeader() + "' qtip='"
						+ v + "' style='font-weight: bold;color:" + style
						+ "'>" + v + "</span>";
			}
		};*/

		/*
		GridCellRenderer<Stock> gridNumber = new GridCellRenderer<Stock>() {
			public String render(Stock model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Stock> store, Grid<Stock> grid) {
				return numberRenderer.render(null, property,
						model.get(property));
			}
		};*/

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId("name");
		column.setHeader("Linkset Name");
		column.setWidth(250);
		column.setRowHeader(false);
		column.setRenderer(packageNameRenderer);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("precision");
		column.setRenderer(percentCellRenderer);
		column.setHeader("PessPrec");
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setWidth(70);
		column.setRenderer(percentCellRenderer);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("recall");
		column.setHeader("PessRec");
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setWidth(70);
		column.setRenderer(percentCellRenderer);
		configs.add(column);

		
		column = new ColumnConfig();
		column.setId("estimatedPrecisionLowerBound");
		column.setRenderer(percentCellRenderer);
		column.setHeader("EstPrec low");
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setWidth(70);
		column.setRenderer(percentCellRenderer);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("estimatedPrecisionUpperBound");
		column.setHeader("EstPrec high");
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setWidth(90);
		column.setRenderer(percentCellRenderer);
		configs.add(column);

		
		
		column = new ColumnConfig();
		column.setId("linksetDuplicateSize");
		column.setHeader("Duplicates");
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setWidth(90);
		column.setRenderer(integerCellRenderer);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("linksetErrorCount");
		column.setHeader("Syntax Errors");
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setWidth(90);
		column.setRenderer(integerCellRenderer);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("linksetSize");
		column.setHeader("Triples");
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setWidth(90);
		column.setRenderer(integerCellRenderer);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("endDate");
		column.setHeader("Evaluation Date");
		column.setRenderer(dateCellRenderer);
		column.setWidth(100);
		configs.add(column);

		
		
		/*
		column = new ColumnConfig("change", "Change", 100);
		column.setAlignment(HorizontalAlignment.RIGHT);
		//column.setRenderer(change);
		configs.add(column);

		column = new ColumnConfig("date", "Finished", 100);
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setDateTimeFormat(DateTimeFormat.getFormat("MM/dd/yyyy"));
		configs.add(column);
*/
		
		//ListStore<TaskDescription> store = new ListStore<TaskDescription>();
		//store.add(getStocks());
		
		
		//store.add(TaskData.getStocks());

		//store.add(getStocks());
		
		cm = new ColumnModel(configs);

		//ContentPanel cp = new ContentPanel();
		setLayout(new FitLayout());
		setBodyBorder(true);
		// cp.setIcon(Resources.ICONS.table());
		setHeading("Available Linksets");
		setButtonAlign(HorizontalAlignment.CENTER);
		getHeader().setIconAltText("Grid Icon");
		
		
		//cp.setSize(600, 300);

		//final Grid<TaskDescription>
		
		/*
		GroupingView view = new GroupingView();  
	    view.setShowGroupedColumn(false);  
	    view.setForceFit(true);  
	    view.setGroupRenderer(new GridGroupRenderer() {  
	      public String render(GroupColumnData data) {  
	        String f = cm.getColumnById(data.field).getHeader();  
	        String l = data.models.size() == 1 ? "Item" : "Items";  
	        return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";  
	      }  
	    });*/  
		
		GroupingView view = new GroupingView();  
	    view.setShowGroupedColumn(false);  
	    view.setForceFit(true);  
	    view.setGroupRenderer(new GridGroupRenderer() {  
	      public String render(GroupColumnData data) {
	    	  // . data.field
	    	  String groupName = data.gvalue != null ? data.gvalue.toString() : ""; //cm.getColumnById(data.field).getHeader();
	    	  
	    	  return groupName;
	        /*
	    	  String f = cm.getColumnById(data.field).getHeader();  
	        String l = data.models.size() == 1 ? "Item" : "Items";  
	        return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";
	        */  
	      }  
	    });

		
		grid = new Grid<Model>(getStocks(), cm);
	    grid.setView(view);
		grid.setStyleAttribute("borderTop", "none");
		//grid.setAutoHeight(true);
		grid.setAutoExpandColumn("name");
		grid.setBorders(false);
		grid.setStripeRows(true);
		grid.setColumnLines(true);
		grid.setColumnReordering(true);
		grid.getAriaSupport().setLabelledBy(getHeader().getId() + "-label");
		add(grid);


		//add(cp);

		// needed to enable quicktips (qtitle for the heading and qtip for the
		// content) that are setup in the change GridCellRenderer
		new QuickTip(grid);
	}
	
	//private ListStore<TaskDescription> store = new ListStore<TaskDescription>();

	/*
	public void setModel(ListStore<TaskDescription> store) {
		this.store = store;
	}*/
	
	/*
	public static List<TaskDescription> getStocks() {
		List<TaskDescription> result = new ArrayList<TaskDescription>();

		result.add(new TaskDescription());

		return result;
	}*/
	
	public static ListStore<Model> getStocks() {
		//return new ListStore<TaskDescription>();
		return Registry.get(Constants.EVALUATION_STORE);
		//return result;
	}
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

	}
}

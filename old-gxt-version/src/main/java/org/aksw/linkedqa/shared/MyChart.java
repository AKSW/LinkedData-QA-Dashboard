package org.aksw.linkedqa.shared;

import com.extjs.gxt.ui.client.data.BaseModelData;


public class MyChart
	extends BaseModelData
{
	public MyChart() {
	}
		
	public MyChart(String type, String data) {
		super();
		setType(type);
		setData(data);
	}
	
	
	public String getType() {
		return get("type");
	}
	public void setType(String type) {
		set("type", type);
	}
	public String getData() {
		return get("data");
	}
	public void setData(String data) {
		set("data", data);
	}

	
	
	/*
	private String type;
	private String data;
	
	
	public MyChart() {
	}
		
	public MyChart(String type, String data) {
		super();
		this.type = type;
		this.data = data;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	*/
}

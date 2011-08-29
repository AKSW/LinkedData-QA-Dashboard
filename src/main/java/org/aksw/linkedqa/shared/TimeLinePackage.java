package org.aksw.linkedqa.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.Model;

public class TimeLinePackage
	extends BaseModel
	implements Serializable
{
	private TreeMap<Date, Model> map = new TreeMap<Date, Model>();
	
	
	public TimeLinePackage() {
		
	}

	
	public TimeLinePackage(String name) {
		this.set("name", name);
	}
	
	public String getName() {
		return this.get("name");
	}
	
	public TreeMap<Date, Model> getMap() {
		return map;
	}
	
	@Override
	public String toString()
	{
		return map.toString();
	}
}

package org.aksw.linkedqa.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;

@SuppressWarnings("serial")
public class TaskDescription 
	extends BaseModelData implements Serializable
{
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;

	public TaskDescription() {
		
		super.set("test", "blah");
		super.set("name", "hi");
		super.set("date", new Date());
	}

	public String getName() {
		return get("name");
	}
	
	public void setName(String name) {
		set("name", name);
	}
	
	public void setDate(Date date) {
		set("date", date);
	}
	
	
	public TaskDescription(Map<String, Object> properties) {
		
		super.setProperties(properties);
	}
}
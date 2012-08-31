package org.aksw.linkedqa.shared;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModel;

public class Package
	extends BaseModel
	implements Serializable
{
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;

	public Package() {
		
	}
	
	public Package(String name) {
		this.set("name", name);
	}
	
	public String getName() {
		return this.get("name");
	}
	
	public Package getParentPackageName() {
		return this.get("parentPackageName");
	}

	@Override
	public String toString() {
		return this.map.toString();
	}
}


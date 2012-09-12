package org.aksw.linkeddata.qa.domain;

import java.util.ArrayList;
import java.util.List;

public class LinkingBranch {
	
	private String id;
	private String name;
	private List<Linkset> linksets = new ArrayList<Linkset>();

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	};
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Linkset> getLinksets() {
		return linksets;
	}
}

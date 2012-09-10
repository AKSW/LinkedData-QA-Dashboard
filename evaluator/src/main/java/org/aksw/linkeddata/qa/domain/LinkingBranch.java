package org.aksw.linkeddata.qa.domain;

import java.util.ArrayList;
import java.util.List;

public class LinkingBranch {
	
	private String name;
	private List<Linkset> linksets = new ArrayList<Linkset>();

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

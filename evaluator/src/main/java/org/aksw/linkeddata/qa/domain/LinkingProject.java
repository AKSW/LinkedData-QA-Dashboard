package org.aksw.linkeddata.qa.domain;

import java.util.ArrayList;
import java.util.List;

public class LinkingProject {
	private List<LinkingBranch> branches = new ArrayList<LinkingBranch>();
	
	private String id;
	private String name;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<LinkingBranch> getBranches() {
		return branches;
	}
}

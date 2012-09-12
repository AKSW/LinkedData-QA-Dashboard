package org.aksw.linkeddata.qa.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Linkset {
	private String id;

	private Calendar date;
	private String specAuthorName;
	private String comment;
	private String revisionName;
	
	private File file;
	
	private List<Evaluation> evaluations = new ArrayList<Evaluation>();
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public List<Evaluation> getEvaluations() {
		return evaluations;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public String getSpecAuthorName() {
		return specAuthorName;
	}

	public void setSpecAuthorName(String specAuthorName) {
		this.specAuthorName = specAuthorName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getRevisionName() {
		return revisionName;
	}

	public void setRevisionName(String revisionName) {
		this.revisionName = revisionName;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
}

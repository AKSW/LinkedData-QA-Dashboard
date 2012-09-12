package org.aksw.linkeddata.qa.domain;

import java.io.File;
import java.util.Calendar;

public class Evaluation {
	private String id;
	private String authorName;
	private Calendar timestamp;
	
	private File positiveRefsetFile;
	private File negativeRefsetFile;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public Calendar getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}
	public File getPositiveRefsetFile() {
		return positiveRefsetFile;
	}
	public void setPositiveRefsetFile(File positiveRefsetFile) {
		this.positiveRefsetFile = positiveRefsetFile;
	}
	public File getNegativeRefsetFile() {
		return negativeRefsetFile;
	}
	public void setNegativeRefsetFile(File negativeRefsetFile) {
		this.negativeRefsetFile = negativeRefsetFile;
	}
	
	
}

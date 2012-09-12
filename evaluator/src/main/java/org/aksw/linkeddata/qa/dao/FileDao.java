package org.aksw.linkeddata.qa.dao;

import java.sql.Connection;

import com.hp.hpl.jena.rdf.model.Model;

public class FileDao {
	private Connection conn;
	
	public FileDao(Connection conn) {
		this.conn = conn;
	}
	
	public void writeModel(String fileId, Model model) {
		
	}
	
}

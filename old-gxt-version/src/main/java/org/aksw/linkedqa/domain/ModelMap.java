package org.aksw.linkedqa.domain;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class ModelMap {
	private Model model;
	private Resource subject;
	
	public RDFNode get(Property property) {
		StmtIterator it = model.listStatements(subject, property, (RDFNode)null);
		if(it.hasNext()) {
			
		}

		return null;
	}
	
	
}

package org.aksw.linkedqa.server;

import java.util.List;

import org.aksw.commons.sparql.api.core.QueryExecutionFactory;

class Dataset
{
	
}

public class MdsDao {
	private QueryExecutionFactory factory;
	
	public MdsDao(QueryExecutionFactory factory) 
	{
		this.factory = factory;
	}
	
	public List<Dataset> findDataset() {
		factory.createQueryExecution("Select ?s ?p ?o { ?s a <http://www.w3.org/ns/dcat#Dataset> . ?s ?p ?o .}");
		return null;
	}
}

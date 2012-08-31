package org.linkeddata.qa;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.aksw.commons.sparql.api.core.QueryExecutionFactory;
import org.aksw.commons.sparql.api.core.QueryExecutionStreaming;

import com.hp.hpl.jena.sparql.engine.http.HttpParams;


/**
 * Jersey resource for the QA Dashboard backend.
 * 
 * @author Claus Stadler <cstadler@informatik.uni-leipzig.de>
 *
 */
@Path("/test")
//@Produces("application/rdf+xml")
@Produces("text/plain")
public class RestService {

	//@Context
	//protected QueryExecutionFactory<QueryExecutionStreaming> sparqler = null;
	public static QueryExecutionFactory<QueryExecutionStreaming> sparqler = null;
	
	public RestService() {
		init();
	}
	
	
	public void init() {
	}
	
	
	public QueryExecutionFactory<QueryExecutionStreaming> getSparqler() throws Exception {
		return sparqler;
	}

	/*
	@GET
	public String executeQueryXml()
			throws Exception {
		String example = "<?xml version='1.0' encoding='ISO-8859-1'?><xml>Select * { ?s ?p ?o } Limit 10</xml>";
		return "No query specified. Example: ?query=" + StringUtils.urlEncode(example);
	}
	*/

	@GET
	public String test()
			throws Exception
	{
		return "Hi";
	}


}
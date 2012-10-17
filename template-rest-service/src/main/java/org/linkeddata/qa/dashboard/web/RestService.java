package org.linkeddata.qa.dashboard.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;


/**
 * Jersey resource for the QA Dashboard transition backend.
 * 
 * @author Claus Stadler <cstadler@informatik.uni-leipzig.de>
 *
 */
@Path("/service")
//@Produces("application/rdf+xml")
//@Produces("text/plain")
public class RestService {

	
	/**
	 *  
	 * @param context The servlet context.
	 */
	public RestService(@Context ServletContext context) {
	}
	

	@GET
	@Path("/detail")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDetailData(@QueryParam("id") String id)
			throws Exception
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("name", "dbpedia-linkedgeodata");
		map.put("tripleCount", 1000);
		
		Gson gson = new Gson();
		String result = gson.toJson(map);
		
		return result;
	}

	@GET
	@Path("/summary")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSummary()
			throws Exception
	{
		List<String> list = new ArrayList<String>();

		list.add("entry1");
		list.add("entry2");
		
		Gson gson = new Gson();
		String result = gson.toJson(list);
		
		return result;
	}
}


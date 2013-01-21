package org.linkeddata.qa.dashboard.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

import org.linkeddata.qa.dashboard.domain.LinksetMetadata;

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
	@Path("/details")
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
	public String getSummary()			throws Exception
	{
		//List<LinksetMetadata> list = new ArrayList<LinksetMetadata>();

		//list.add(new LinksetMetadata("Cities", "http://linkedgedata.org/sparql", "http://dbpedia.org/sparql", 1000));
		//list.add(new LinksetMetadata("Pubs", "http://linkedgedata.org/sparql", "http://dbpedia.org/sparql", 1000));
		// Connecting to DB
		//Gson gson = new Gson();
		//String result = gson.toJson(list);
		String result=getJson("jdbc:mysql://localhost:3306/","linkSetDB","com.mysql.jdbc.Driver","root","mofo");
		return result;
	}
	private String getJson(String url,String db,String driver,String userName,String password) throws SQLException
	{
		String infoList="{\"data\": [";
		Connection con = null;
		try
		  {
			  Class.forName(driver);
			  con = DriverManager.getConnection(url+db,userName,password);
			  String selectStatement="SELECT linkSetInfo FROM linkSetTbl";
			  Statement st = con.createStatement();
			  ResultSet linksRecords=st.executeQuery(selectStatement);
			  
			  while(linksRecords.next())
			  {
			  	infoList+=linksRecords.getString("linkSetInfo")+",";

			  }
			  
			  StringBuilder tmp = new StringBuilder(infoList);
			  tmp.replace(infoList.lastIndexOf(","), infoList.lastIndexOf(",") + 1, "]" );
			  infoList = tmp.toString();
			  infoList+="}";
		  }
		 
		  catch (SQLException s)
		  {
			  System.out.println("SQL statement is not executed!\n"+s.getMessage());
		  } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  finally
		  {
			  con.close();
		  }
		return infoList;
	}
	
	private List<LinksetMetadata> getMetaDataFromDB(String url,String db,String driver,String userName,String password) throws SQLException
	{
		List<LinksetMetadata> infoList= new ArrayList<LinksetMetadata>();
		Connection con = null;
		//String url = "jdbc:mysql://localhost:3306/";
		//String db = this.dbName;        
		//String driver = "com.mysql.jdbc.Driver";
		
		  try
		  {
			  Class.forName(driver);
			  con = DriverManager.getConnection(url+db,userName,password);
			  String selectStatement="SELECT linkSetInfo FROM linkSetTbl";
			  Statement st = con.createStatement();
			  ResultSet linksRecords=st.executeQuery(selectStatement);
			  //create the file to be the backup named after current date
			  //{"FileName":"http://linker.sindice.com/runtime-results/2011-02-10/dbpedia-lgd_airport.xml/spec.xml",
			  //"Source":"idu003d"dbpedia"","Target":"idu003d"linkedgeodata"","SourceEndpoint":"valueu003d"http://live.dbpedia.org/sparql/""
			  //,"TargetEndpoint":"valueu003d"http://linkedgeodata.org/sparql/"","LinkingType":"owl:sameAs","TriplesNo":0,
			  //"taskSetName":"dbpedia-lgd_airport.xml","taskSetPath":"http://linker.sindice.com/runtime-results/2011-02-10/dbpedia-lgd_airport.xml"}
			  while(linksRecords.next())
			  {
				 // String linkSetInfo=linksRecords.getString("linkSetInfo");
				  //var obj = jQuery.parseJSON(linkSetInfo);
				Gson gson = new Gson();
					
				//JSONObject myjson = new JSONObject(linkSetInfo);
				//JSONArray the_json_array = myjson.getJSONArray("SourceEndpoint");
				 
				infoList.add(new LinksetMetadata("Cities", "http://linkedgedata.org/sparql", "http://dbpedia.org/sparql", 1000));
				  //linksRecords.getString("linkSetName")+","+linksRecords.getString("linkSetPath")+","+linksRecords.getString("revisionDate")+","+linksRecords.getString("linkSetInfo")+"\n";
				 
			  }
		  }
		 
		  catch (SQLException s)
		  {
			  System.out.println("SQL statement is not executed!\n"+s.getMessage());
		  } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  finally
		  {
			  con.close();
		  }
		return infoList;
		
	}
}


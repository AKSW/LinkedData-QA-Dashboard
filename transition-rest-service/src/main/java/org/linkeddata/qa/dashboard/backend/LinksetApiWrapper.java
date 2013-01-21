package org.linkeddata.qa.dashboard.backend;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aksw.commons.util.XPathUtils;
import org.aksw.commons.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
public class LinksetApiWrapper 
{
	String dbName;
	String userName;
	String password;
	private Date recentDate;
	public LinksetApiWrapper(String dbName, String userName,String password)
	{
		this.dbName=dbName;
		this.userName=userName;
		this.password=password;
	}
	private  ArrayList<String> storedDates;
	public void preparingDatabase(int choice) throws ClassNotFoundException, SQLException
	{
		createBackup();
		if(choice==1)
			clearDB();
		else //choice = 2, so get recent folders only
			recentDate= getMaxDate();
	}
/*	private String getRecentDate() throws SQLException
	{
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/";
		String db = this.dbName;        
		String driver = "com.mysql.jdbc.Driver";
	  
		  try
		  {
			  Class.forName(driver);
			  con = DriverManager.getConnection(url+db,this.userName,this.password);
			  String selectStatement="SELECT revisionDate FROM linkSetTbl";
			  Statement st = con.createStatement();
			  ResultSet linksRecords=st.executeQuery(selectStatement);
			  String recentDate;
			  Date today=Date
			  while(linksRecords.next())
			  {
				  recentDate=linksRecords.getString("revisionDate")+","+linksRecords.getString("linkSetInfo")+"\n";
				  storedDates.add(storedDate);
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
	}*/
/*	private ArrayList<String> extractStoredDates() throws SQLException
	{
		ArrayList<String> storedDates= new ArrayList<String>();
		
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/";
		String db = this.dbName;        
		String driver = "com.mysql.jdbc.Driver";
		
		  try
		  {
			  Class.forName(driver);
			  con = DriverManager.getConnection(url+db,this.userName,this.password);
			  String selectStatement="SELECT revisionDate FROM linkSetTbl";
			  Statement st = con.createStatement();
			  ResultSet linksRecords=st.executeQuery(selectStatement);
			  			  
			  String storedDate;
			  while(linksRecords.next())
			  {
				  storedDate=linksRecords.getString("linkSetName")+","+linksRecords.getString("linkSetPath")+","+linksRecords.getString("revisionDate")+","+linksRecords.getString("linkSetInfo")+"\n";
				  storedDates.add(storedDate);
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
		
		
		return storedDates;
	}*/
	public void getDatedDirectories(String urlTopDirectory) throws IOException, SQLException, ParseException
	{ 	
		//get list of links of dated folder inside the top link
		ArrayList<String> ParentDatedFolders=extractFoldersLinks(urlTopDirectory);
		String dateExpression = "^(19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])$";
		//iterate over list of dated folders
		if(recentDate!=null) // this means that recentDate is assigned to max. date selected from database through fun. getMaxDate() as a result of assigning choice to 2,so we need the other datedfolders foloowing that date
			for (String ParentDatedFolder : ParentDatedFolders) 
			{
				Date parentFolderDate = new SimpleDateFormat("yyyy-MM-dd").parse(ParentDatedFolder);
				  
				if(ParentDatedFolder.matches(dateExpression) && recentDate.compareTo(parentFolderDate) < 0 ) //cparent folder date is after recent date retrieved from DB, so it is new
				{	//get linking tasks folders for each dated folder and extract information for each task
					getLinkingTasksFoldersInfo(urlTopDirectory,ParentDatedFolder);
				}
			}
		else// it is still null. we will get all dated folders
			for (String ParentDatedFolder : ParentDatedFolders) 
			{
				if(ParentDatedFolder.matches(dateExpression))
				{	//get linking tasks folders for each dated folder and extract information for each task
					getLinkingTasksFoldersInfo(urlTopDirectory,ParentDatedFolder);
				}
			}
	}
	public void getLinkingTasksFoldersInfo(String urlTopDirectory,String ParentDatedFolder) throws IOException, SQLException
	{ 
	
		String urlParentDatedDirectory=urlTopDirectory+ParentDatedFolder+"/";
		//each tuple corresponds to information about single task folder inside the parent_dated folder
		LinkSetTuple linkTuple;
		//1- Get all folders hyperlinks inside the dated folder page
		ArrayList<LinkSetTuple> linkTasksTuples= new ArrayList<LinkSetTuple>();
		ArrayList<String> linkingTasksList=extractFoldersLinks(urlParentDatedDirectory);
		//2- move over each linking task hyperlink
		Iterator<String> linkingTask = linkingTasksList.iterator();
		while( linkingTask.hasNext() ) 
		{
			String linkingTaskName =linkingTask.next();
			String specFileLink=urlParentDatedDirectory+linkingTaskName+"/spec.xml";
			String linksFileLink=urlParentDatedDirectory+linkingTaskName+"/links.nt";
			String voidFileLink=urlParentDatedDirectory+linkingTaskName+"/void.ttl";
			try
			{
				//create URL object to read file from online URL
				URL website = new URL(specFileLink); 
			    ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			    FileOutputStream fos = new FileOutputStream("information.xml");//create file to save the read information of the URL in current file to parse
			    fos.getChannel().transferFrom(rbc, 0, 1 << 24);// read from URL and save it in file
			}
			catch(IOException e)
			{
				System.out.println(specFileLink+" is not found");
			}
		    System.out.println();
			System.out.println("-------------------------------------------------------------------------------------------------------------------------");
			System.out.println("The current spec.xml file to extract information from: "+specFileLink);
			/////////////////////////////////  Get linking task information from specfile into linktuple object////////////////////////
			linkTuple=parseLinkTaskDownloadedFile();
			linkTuple.setTriplesNo(Integer.parseInt(getTriplesNumber(voidFileLink)));
			/////////////////////////////   set file information  ///////////////////////////////
			linkTuple.setTaskName(linkingTaskName);
			linkTuple.setTaskSetPath(urlParentDatedDirectory+linkingTaskName);
			linkTuple.setFileName(specFileLink);// add to tuple
			linkTuple.setRevisionDate(ParentDatedFolder);
			linkTuple.setLinksFile(linksFileLink);
		    System.out.println("THE TUPLE OBJECT:"+linkTuple);
		    linkTasksTuples.add(linkTuple);
		}
		writeToDB(ParentDatedFolder,linkTasksTuples);
		
	}
	private String getTriplesNumber(String voidURL)
	{
		BufferedReader in = null;
	    String inputLine;
		try {
		      URL url = new URL(voidURL);
		      in = new BufferedReader(new InputStreamReader(url.openStream()));
		      
		      while ((inputLine = in.readLine()) != null) 
		      {
		    	  Pattern pattern = Pattern.compile("void:triples\\s*(.*?);");
		    	  Matcher matcher = pattern.matcher(inputLine);
		    	  if (matcher.find())
		    	  {
		    		  return matcher.group(1);
		    	  }
		      }
		    } 
		catch (Exception e) 
		{
		      
		}  
	      
		return "0";
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Input: web page url
	 * Processing: This function takes input the page url that contains hyperlinks to each linking task folder, the linking task folder is named after the 
	 * date where the linking task created. In this function these names are extracted as strings and added to "foldersLinks" array list, which
	 * is returned from the function.
	 * Output: list of dates as string representing the linking tasks' folders names 
	 * */
	private ArrayList<String> extractFoldersLinks(String urlParentDirectory) 
	{
			ArrayList<String> foldersLinks=new ArrayList<String>();
		    BufferedReader in = null;
		    String inputLine;

		    try {
		      URL url = new URL(urlParentDirectory);
		      in = new BufferedReader(new InputStreamReader(url.openStream()));

		      while ((inputLine = in.readLine()) != null) 
		      {
		    	  Pattern pattern = Pattern.compile("<a href=\"(.*?)/\">(.*?)/</a>");
		    	  Matcher matcher = pattern.matcher(inputLine);
		    	  if (matcher.find() && matcher.group(1).equals(matcher.group(2)))
		    	  {
		    		//  if(choice==1) // means clear all in DB and retrieve links from begining
		    			  foldersLinks.add(matcher.group(1));
		    	//	  else //means retrieve recent links after the last date
		    	//	  {
		    			  
		    	//	  }
		    		  
		    	  }
		      }
		    } catch (Exception e) {
		      e.printStackTrace();
		    } finally 
		    {
		      if (in != null) 
		      {
		        try 
			        {
			          in.close();
			        } 
		        catch (IOException e) 
			        {
		          e.printStackTrace();
			        }
		      }
		    }
		    return foldersLinks;
	}
	private LinkSetTuple parseLinkTaskDownloadedFile()
	{
		LinkSetTuple linkTuple= new LinkSetTuple();
		try
		{
			Document doc = XmlUtils.openFile(new File("information.xml"));// open file where information is saved to extract the tags hierarchy info
			String str = XPathUtils.evalToString(doc, "Silk/Interlinks/Interlink/LinkType");
					
			NodeList nl= XPathUtils.evalToNodes(doc, "Silk/DataSources/DataSource/Param[2]");// This retrieves all parameters no.2 under any DataSource tag 
			int listLength=nl.getLength();
			nl= XPathUtils.evalToNodes(doc, "Silk/DataSources/DataSource/Param[1]/@value");
			
			////////////////////////////////////////////////////Get data sources///////////////////////////////////////////////////////////////////////////////
			nl= XPathUtils.evalToNodes(doc, "Silk/DataSources/DataSource");// gets list of Data Sources
				int length=nl.getLength();
			for(int i=0;i<length;i++)
			{
				System.out.println("Data Source No."+i+": "+nl.item(i).getAttributes().getNamedItem("id"));
				if(i%2==0)
					linkTuple.setSource(nl.item(i).getAttributes().getNamedItem("id").getTextContent());// add to tuple
				else
					linkTuple.setTarget(nl.item(i).getAttributes().getNamedItem("id").getTextContent());// add to tuple
			}
			///////////////////////////////////////////////////Get Endpoints ///////////////////////////////////////////////////////////////////////////////
			nl= XPathUtils.evalToNodes(doc, "Silk/DataSources/DataSource/Param");// gets list of SPARQL ENDPOINTS
			int count=0;
			for(int i=0;i<nl.getLength();i++)
			{
				
				if(nl.item(i).getAttributes().getNamedItem("name").getTextContent().equals("endpointURI"))
				{
					System.out.println("EndPoint No."+count+" "+nl.item(i).getAttributes().getNamedItem("value"));
					
					if(count%2==0)
						linkTuple.setSourceEndpoint(nl.item(i).getAttributes().getNamedItem("value").getTextContent());
					else
						linkTuple.setTargetEndpoint(nl.item(i).getAttributes().getNamedItem("value").getTextContent());
					count++;
				}
			}
			////////////////////////////////////////////////////Get Restrictions///////////////////////////////////////////////////////////////////////////////
			nl= XPathUtils.evalToNodes(doc, "Silk/Interlinks/Interlink/LinkCondition/Aggregate/Compare/Input");// gets list of linking predicates
			length=nl.getLength();
			for(int i=0;i<length;i++)
			{
				System.out.println("Predicates No."+i+": "+nl.item(i).getAttributes().getNamedItem("path"));
			}
			nl= XPathUtils.evalToNodes(doc, "Silk/Interlinks/Interlink/SourceDataset/RestrictTo");// gets list of linking predicates
			System.out.println("Restriction for Data source: "+nl.item(0).getTextContent());// retrieves the text content inside the node
			nl= XPathUtils.evalToNodes(doc, "Silk/Interlinks/Interlink/TargetDataset/RestrictTo");// gets list of linking predicates
			System.out.println("Restriction for Data Target: "+nl.item(0).getTextContent());// retrieves the text content inside the node
	
			System.out.println("Interlinking Type: "+str);
			////////////////////////////////////////////////////Get Linking Type///////////////////////////////////////////////////////////////////////////////
			linkTuple.setLinkingType(str);
		}
		catch(Exception e)
		{System.out.println(e.getMessage());}
		return linkTuple;
	}
	private void createBackup() throws ClassNotFoundException, SQLException
	{
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/";
		String db = this.dbName;        
		String driver = "com.mysql.jdbc.Driver";
		
		  try
		  {
			  Class.forName(driver);
			  con = DriverManager.getConnection(url+db,this.userName,this.password);
			  String selectStatement="SELECT * FROM linkSetTbl";
			  Statement st = con.createStatement();
			  ResultSet linksRecords=st.executeQuery(selectStatement);
			  //create the file to be the backup named after current date
			  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			  Date date = new Date();
			  File backupFileName = new File(dateFormat.format(date));
			  if(!backupFileName.exists()) 
			  { 
				  backupFileName.createNewFile();
			  } 
			  System.out.println(dateFormat.format(date));
			  FileOutputStream fos = new FileOutputStream(backupFileName,false);//create file to save the read information of the URL in current file to parse
			  
			  String linkRecord;
			  while(linksRecords.next())
			  {
				  linkRecord=linksRecords.getString("linkSetName")+","+linksRecords.getString("linkSetPath")+","+linksRecords.getString("revisionDate")+","+linksRecords.getString("linkSetInfo")+"\n";
				  byte[] contentInBytes = linkRecord.getBytes();
				  fos.write(contentInBytes);
				  fos.flush();
			  }
			  fos.close();
		  }
		  catch(IOException s)
		  {
			  System.out.println("Inpu/Output Exception!\n"+s.getMessage());
		  }
		 
		  catch (SQLException s)
		  {
			  System.out.println("SQL statement is not executed!\n"+s.getMessage());
		  }
		  finally
		  {
			  con.close();
		  }
	}
	private void clearDB() throws ClassNotFoundException, SQLException
	{
			Connection con = null;
			String url = "jdbc:mysql://localhost:3306/";
			String db = this.dbName;        
			String driver = "com.mysql.jdbc.Driver";
		  
			  try
			  {
				  Class.forName(driver);
				  con = DriverManager.getConnection(url+db,this.userName,this.password);
				  String clearStatement="DELETE FROM linkSetTbl";
				  Statement st = con.createStatement();
				  int delRecords=st.executeUpdate(clearStatement);
			  }
			  catch (SQLException s)
			  {
				  System.out.println("SQL statement is not executed!\n"+s.getMessage());
			  }
			  finally
			  {
				  con.close();
			  }
		 
	}
	private Date getMaxDate()
	{
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/";
		String db = this.dbName;        
		String driver = "com.mysql.jdbc.Driver";
		Date recentDate= null;
		  try
		  {
			  Class.forName(driver);
			  con = DriverManager.getConnection(url+db,this.userName,this.password);
			  String selectStatement="SELECT MAX(revisionDate) FROM linkSetTbl";
			  Statement st = con.createStatement();
			  ResultSet linksRecords=st.executeQuery(selectStatement);
			  
			  while(linksRecords.next())
			  {
				  recentDate =  linksRecords.getDate(1);
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
			  try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		  return recentDate;
	}
	private void writeToDB(String ParentDatedFolder,ArrayList<LinkSetTuple> linkTasksTuples) throws SQLException
	{
		  Connection con = null;
		  String url = "jdbc:mysql://localhost:3306/";
		  String db = this.dbName;        
		  String driver = "com.mysql.jdbc.Driver";
		  
		  try
		  {
			  Class.forName(driver);
			  con = DriverManager.getConnection(url+db,this.userName,this.password);
			  try
			  {
				  //String clearStatement="DELETE FROM linkSetTbl";
				  for (LinkSetTuple linkSetTuple : linkTasksTuples) 
				  {
					System.out.println("Inserting values in Mysql database table!");
					Gson jsonWriter= new Gson();
					String jsonString=jsonWriter.toJson(linkSetTuple);
					System.out.println(jsonString);
				  	System.out.println("1 row affected");
				  	Date dateFormat = new SimpleDateFormat("yyyy-MM-dd").parse(ParentDatedFolder);
					//Date date = new Date();
					String insertStatement="INSERT INTO linkSetTbl VALUES ('"+linkSetTuple.getTaskName()+"','"+linkSetTuple.getTaskSetPath()+"','"+ParentDatedFolder+"','"+jsonString+"')";
				  	Statement st = con.createStatement();
						 //int delRecords=st.executeUpdate(clearStatement);
				  	int insRecords=st.executeUpdate(insertStatement);
				  	//////////////////////////////////////
				  	
				  }
			  }
			  catch (SQLException s)
			  {
				  System.out.println("SQL statement is not executed!\n"+s.getMessage());
			  }
			  finally
			  {
				  con.close();
			  }
		  }
		  catch (Exception e)
		  {
			  e.printStackTrace();
		  }
		  finally
		  {
			  con.close();
		  }
	}
	
	
	
}

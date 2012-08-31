package org.aksw.linkedqa.server;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class MyApplicationContext
{
	private static Logger logger = Logger.getLogger(MyApplicationContext.class);
	
    private static MyApplicationContext instance = null;
	
    private ApplicationContext context;

    private static Connection dataBaseConnection = null;
    
    private MyApplicationContext()
    {
    	this.context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
    	//this.context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
    }
    
    public static MyApplicationContext get()
    {
    	if(instance == null)
    		instance = new MyApplicationContext();

    	return instance;
    }
    
    public String getSnapshotRepoPath()
    {
    	return get("snapshotRepoPath");
    }
    
    public String getPackageRepoPath()
    {
    	return get("packageRepoPath");
    }
    

    
    public String getMetricsSnapshotRepoPath()
    {
    	return get("metricsSnapshotRepoPath");
    }
    
    public String getMetricsPackageRepoPath()
    {
    	return get("metricsPackageRepoPath");
    }
    
    
    @SuppressWarnings("unchecked")
	public <T> T get(String beanName)
    {
    	return (T)context.getBean(beanName);
    }    
}
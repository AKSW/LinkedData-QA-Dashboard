package org.aksw.linkedqa.domain;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aksw.linkedqa.shared.Package;
import org.aksw.linkedqa.shared.TimeLinePackage;
import org.ini4j.InvalidFileFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extjs.gxt.ui.client.data.Model;


public class TimeLineCollectionRepository {
	private static final Logger logger = LoggerFactory.getLogger(TimeLineCollectionRepository.class);
	
	private RSnapshotRepository<PackageRepository> backend;
	
	private String intervalType = "hourly";

	private Map<String, TimeLinePackage> nameToPackage = new HashMap<String, TimeLinePackage>();
	
	public TimeLineCollectionRepository()
	{
	}
	
	
	public RSnapshotRepository<PackageRepository> getBackend() {
		return backend;
	}
	
	public TimeLineCollectionRepository(RSnapshotRepository<PackageRepository> backend) throws InvalidFileFormatException, IOException {
		this.backend = backend;
		
		// TODO Actually this loads all metadata about packages; additional data must be fetched
		// with specific methods
		this.loadAllPackages();
	}
	
	
	
	public NavigableMap<Integer, Date> getTimeLineTimeStamps()
	{
		NavigableMap<Integer, Date> result = new TreeMap<Integer, Date>();

		NavigableMap<Integer, PackageRepository> repos = backend.getRepos(intervalType);
				
		for(Entry<Integer, PackageRepository> entry : repos.entrySet()) {
			result.put(entry.getKey(), entry.getValue().getLastModificationDate().getTime());
		}
		
		return result;
	}
	
	/**
	 * Returns 
	 * 
	 * @return
	 */
	public NavigableSet<Integer> getTimeLines()
	{
		return new TreeSet<Integer>(backend.getRepos(intervalType).keySet());
	}
	
	/**
	 * Scans the latest version of the repo for its packages,
	 * and fetches these packages from all earlier versions.
	 * 
	 * 
	 * @return
	 * @throws IOException 
	 * @throws InvalidFileFormatException 
	 */
	public void loadAllPackages() throws InvalidFileFormatException, IOException {		
		//PackageRepository repo = backend.getLatest(intervalType);

		if(backend.getRepos(intervalType) == null) {
			logger.error("No repository for interval type '" + intervalType + "' found");
			return;
		}
		
		for(PackageRepository r : backend.getRepos(intervalType).values()) {
			System.out.println(r.getLastModificationDate().getTime());

			for(Package p : r.getMap().values()) {
		
				//BaseModel model = r.getPositiveEvaluation(p.getName()); //);.getName());
				Model model = r.getData(p.getName());
				if(model != null) {				
					p.setProperties(model.getProperties());
				}
				
				
				//System.out.println(p.getName());
				
				TimeLinePackage tlp = nameToPackage.get(p.getName());
				if(tlp == null) {				
					tlp = new TimeLinePackage(p.getName());

					nameToPackage.put(p.getName(), tlp);
				}
				tlp.getMap().put(r.getLastModificationDate().getTime(), p);
			}
		}
	}
	
	
	public TimeLinePackage getTimeLinePackage(String packageId) {
		return nameToPackage.get(packageId);
	}

	
	public  Map<String, Model> getEvaluations(int index)
			throws InvalidFileFormatException, IOException
	{
		Map<String, Model> result = new HashMap<String, Model>();

		for(Entry<String, TimeLinePackage> entry : nameToPackage.entrySet()) {
			Model p = entry.getValue().getMap().values().iterator().next();
			String name = (String)p.get("name");
			
			//BaseModel model = backend.getLatest(intervalType).getPositiveEvaluation(name); //);.getName());
			Model model = backend.get(index, intervalType).getData(name);
			if(model == null) {
				continue;
			}
			
			result.put(name, model);
		}
		
		System.out.println(result);
		return result;
	}
	
	/*
	public Model getPositiveEvaluation(String packageId) throws InvalidFileFormatException, IOException {
		return backend.getLatest(intervalType).getPositiveEvaluation(packageId); //);.getName());
	}*/

	public Map<String, Model> getLatestEvaluations()
				throws InvalidFileFormatException, IOException
	{
		Map<String, Model> result = new HashMap<String, Model>();

		for(Entry<String, TimeLinePackage> entry : nameToPackage.entrySet()) {
			Model p = entry.getValue().getMap().values().iterator().next();
			String name = (String)p.get("name");
			
			//BaseModel model = backend.getLatest(intervalType).getPositiveEvaluation(name); //);.getName());
			Model model = backend.getLatest(intervalType).getData(name);
			if(model == null) {
				continue;
			}
			
			result.put(name, model);
		}
		
		System.out.println(result);
		return result;
	}
	

}


package org.aksw.linkedqa.domain;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.aksw.linkedqa.shared.Package;
import org.aksw.linkedqa.shared.TimeLinePackage;
import org.ini4j.InvalidFileFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.Model;


public class TimeLineCollectionRepository {
	private static final Logger logger = LoggerFactory.getLogger(TimeLineCollectionRepository.class);
	
	private RSnapshotRepository<PackageRepository> backend;
	
	private String intervalType = "hourly";

	private Map<String, TimeLinePackage> nameToPackage = new HashMap<String, TimeLinePackage>();
	
	public TimeLineCollectionRepository(RSnapshotRepository<PackageRepository> backend) throws InvalidFileFormatException, IOException {
		this.backend = backend;
		
		this.loadAllPackages();
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
		
				BaseModel model = r.getPositiveEvaluation(p.getName()); //);.getName());
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

	
	/*
	public Model getPositiveEvaluation(String packageId) throws InvalidFileFormatException, IOException {
		return backend.getLatest(intervalType).getPositiveEvaluation(packageId); //);.getName());
	}*/

	public Map<String, BaseModel> getLatestEvaluations()
				throws InvalidFileFormatException, IOException
	{
		Map<String, BaseModel> result = new HashMap<String, BaseModel>();

		for(Entry<String, TimeLinePackage> entry : nameToPackage.entrySet()) {
			Model p = entry.getValue().getMap().values().iterator().next();
			String name = (String)p.get("name");
			
			BaseModel model = backend.getLatest(intervalType).getPositiveEvaluation(name); //);.getName());
			if(model == null) {
				continue;
			}
			
			result.put(name, model);
		}
		
		System.out.println(result);
		return result;
	}
	
		
}
package org.aksw.linkedqa.server.linksets;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LinksetRepository {
	private File repoRoot;
	
	private Map<String, LinksetItem> cache = new HashMap<String, LinksetItem>();
	
	public LinksetRepository(File repoRoot) {
		this.repoRoot = repoRoot;
	}
	
	/**
	 * Scan the repository and create objects for each item
	 * 
	 */
	private void reload() {
		
	}
	

	/*
	 * Maybe there shouldn't be any write access here.
	 * 
	public LinksetItem create(String name) {
		
		// Check for a cached instance
		
		File repo = new File(repoRoot.getAbsolutePath() + "/" + name);
		repo.mkdirs();
		
		LinksetItem result = new LinksetItem(name);
		
		
		cache.put(name, result);
		
		return result;
	}*/
	
	
}
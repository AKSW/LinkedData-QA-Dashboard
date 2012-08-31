package org.aksw.linkedqa.server.linksets;

import java.util.Date;
import java.util.NavigableMap;


public class LinksetConfig
{
	private String hash; // A hash value used for logically grouping linksets by their their configuration.
	
	private NavigableMap<Date, LinksetSnapshot>  snapshots;
	
	public LinksetConfig(String hash) {
		this.hash = hash;
	}
	
	public String getHash() {
		return hash;
	}
	
	public NavigableMap<Date, LinksetSnapshot> getSnapshots() {
		return snapshots;
	}
}

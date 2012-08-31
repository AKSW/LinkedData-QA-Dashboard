package org.aksw.linkedqa.server.linksets;

import java.util.List;



/**
 * A LinksetItem has a name, and a set of configurations.
 * For each configuration there is a set of snapshots.
 * 
 * 
 * 
 * @author raven
 *
 */
public class LinksetItem
{
	private String name;
	private List<LinksetConfig> snapshots;
	
	public LinksetItem(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public List<LinksetConfig> getSnapshots()
	{
		return snapshots;
	}
}


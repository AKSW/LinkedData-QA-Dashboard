package org.aksw.linkedqa.domain;

import java.util.HashMap;
import java.util.Map;

class PropertyDesc<T> {
	private Range<T> range;
	
	public PropertyDesc(Class<T> dataType, Range<T> range)
	{
		
	}
	
	public Range<T> getRange() {
		return range;
	}
}

public class PropertyManager {
	private static PropertyManager instance = null;
	
	private Map<String, PropertyDesc> nameToDesc = new HashMap<String, PropertyDesc>();
	
	public PropertyManager() {
		
	}
	
	/**
	 * Get the global instance
	 * 
	 * @return the global instance
	 */
	public static PropertyManager get() {
		if(instance == null) {
			instance = new PropertyManager();
		}
		
		return instance;
	}
	
	public PropertyDesc get(String name) {
		return nameToDesc.get(name);
	}
}

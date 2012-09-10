package org.aksw.linkeddata.qa.eval;

import java.util.ArrayList;
import java.util.List;

public class DataSeries {
	private List<Number> value = new ArrayList<Number>();
	private List<Number> before = new ArrayList<Number>();
	private List<Number> after = new ArrayList<Number>();

	public List<Number> getValue() {
		return value;
	}
	public List<Number> getBefore() {
		return before;
	}
	public List<Number> getAfter() {
		return after;
	}
	
	public void add(Number value, Number before, Number after) {
		this.value.add(value);
		this.before.add(before);
		this.after.add(after);
	}
	@Override
	public String toString() {
		return "value: " + value + "\nbefore: " + before + "\nafter: " + after;
	}
	
	
}
package org.aksw.linkedqa.shared.linksets;

class Bound {
	private double value;
	private boolean isInclusive;
	
	public Bound(double value, boolean isInclusive) {
		super();
		this.value = value;
		this.isInclusive = isInclusive;
	}
	
	public double getValue() {
		return value;
	}
	public boolean isInclusive() {
		return isInclusive;
	}
}

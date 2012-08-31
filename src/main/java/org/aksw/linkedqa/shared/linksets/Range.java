package org.aksw.linkedqa.shared.linksets;


class Range
{
	private Bound lowerBound;
	private Bound upperBound;
		
	public Range(Bound lowerBound, Bound upperBound) {
		super();
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	public Bound getLowerBound() {
		return lowerBound;
	}
	public Bound getUpperBound() {
		return upperBound;
	}	
}

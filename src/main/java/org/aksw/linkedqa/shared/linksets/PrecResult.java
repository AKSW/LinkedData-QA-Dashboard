package org.aksw.linkedqa.shared.linksets;

public class PrecResult {
	private double precision;
	private double recall;
	
	public PrecResult(double precision, double recall) {
		super();
		this.precision = precision;
		this.recall = recall;
	}

	public double getPrecision() {
		return precision;
	}

	public double getRecall() {
		return recall;
	}
}

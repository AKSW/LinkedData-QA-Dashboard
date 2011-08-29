package org.aksw.linkedqa.domain;


public class Range<T> {
	private RangeBound<T> min;
	private RangeBound<T> max;
	
	public Range(T min, T max) {
		this.min = new RangeBound<T>(min, min != null);
		this.max = new RangeBound<T>(max, max != null);
	}

	public Range(T min, boolean minInclusive, T max, boolean maxInclusive) {
		this.min = new RangeBound<T>(min, minInclusive);
		this.max = new RangeBound<T>(max, maxInclusive);
	}

	public RangeBound<T> getMin() {
		return min;
	}

	public RangeBound<T> getMax() {
		return max;
	}
	
	
	@Override
	public String toString() {
		String result = "";
		
		result += getMin().isInclusive() ? "[" : "]";
		
		result += getMin().getValue() == null ? "-co" : getMin().getValue();
		result += ", ";
		result += getMax().getValue() == null ? "+co" : getMax().getValue();
		
		result += getMax().isInclusive() ? "]" : "[";
		
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(new Range<Integer>(null, null));
	}
}


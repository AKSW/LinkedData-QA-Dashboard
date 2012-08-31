package org.aksw.linkedqa.domain;

/**
 * Not sure if there should be subclasses "UpperBound" and "LowerBound",
 * but I guess it would make sense.
 * 
 * @author raven
 *
 * @param <T>
 */
public class RangeBound<T> {
	private T value;
	private boolean inclusive;
	
	public RangeBound() {
		
	}
	
	public RangeBound(T value, boolean inclusive) {
		super();
		this.value = value;
		this.inclusive = inclusive;
	}

	public boolean isInclusive() {
		return inclusive;
	}

	public T getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (inclusive ? 1231 : 1237);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RangeBound other = (RangeBound) obj;
		if (inclusive != other.inclusive)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	/*
	@Override
	public String toString() {
		return "RangeBound [inclusive=" + inclusive + ", value=" + value + "]";
	}*/
}

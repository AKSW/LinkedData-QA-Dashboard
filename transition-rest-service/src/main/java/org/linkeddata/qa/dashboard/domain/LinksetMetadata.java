package org.linkeddata.qa.dashboard.domain;

public class LinksetMetadata {
	private String linkType;
	private String sourceEndpoint;
	private String targetEndpoint;
	private long tripleCount;
	
	public LinksetMetadata(String linkType, String sourceEndpoint,
			String targetEndpoint, long tripleCount) {
		super();
		this.linkType = linkType;
		this.sourceEndpoint = sourceEndpoint;
		this.targetEndpoint = targetEndpoint;
		this.tripleCount = tripleCount;
	}

	public String getLinkType() {
		return linkType;
	}

	public String getSourceEndpoint() {
		return sourceEndpoint;
	}

	public String getTargetEndpoint() {
		return targetEndpoint;
	}

	public long getTripleCount() {
		return tripleCount;
	}

	@Override
	public String toString() {
		return "LinksetData [linkType=" + linkType + ", sourceEndpoint="
				+ sourceEndpoint + ", targetEndpoint=" + targetEndpoint
				+ ", tripleCount=" + tripleCount + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((linkType == null) ? 0 : linkType.hashCode());
		result = prime * result
				+ ((sourceEndpoint == null) ? 0 : sourceEndpoint.hashCode());
		result = prime * result
				+ ((targetEndpoint == null) ? 0 : targetEndpoint.hashCode());
		result = prime * result + (int) (tripleCount ^ (tripleCount >>> 32));
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
		LinksetMetadata other = (LinksetMetadata) obj;
		if (linkType == null) {
			if (other.linkType != null)
				return false;
		} else if (!linkType.equals(other.linkType))
			return false;
		if (sourceEndpoint == null) {
			if (other.sourceEndpoint != null)
				return false;
		} else if (!sourceEndpoint.equals(other.sourceEndpoint))
			return false;
		if (targetEndpoint == null) {
			if (other.targetEndpoint != null)
				return false;
		} else if (!targetEndpoint.equals(other.targetEndpoint))
			return false;
		if (tripleCount != other.tripleCount)
			return false;
		return true;
	}
}

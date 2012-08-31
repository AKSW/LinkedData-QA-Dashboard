package org.aksw.linkedqa.shared.linksets;

public class RdfFileStats {
	private Integer tripleCount;
	private Integer duplicateCount;
	private Integer errorCount;
	
	public RdfFileStats(Integer tripleCount, Integer duplicateCount,
			Integer errorCount) {
		super();
		this.tripleCount = tripleCount;
		this.duplicateCount = duplicateCount;
		this.errorCount = errorCount;
	}

	public Integer getTripleCount() {
		return tripleCount;
	}
	public Integer getDuplicateCount() {
		return duplicateCount;
	}
	public Integer getErrorCount() {
		return errorCount;
	}
}

package org.aksw.linkedqa.shared.linksets;

import java.util.Date;


public class LinksetEvalResult
{
	private PrecResult positive;
	private PrecResult negative;
	
	private RdfFileStats refsetStats;
	private RdfFileStats linksetStats;

	private int intersectionSize;
	
	private Date startDate;
	private Date endDate;


	public LinksetEvalResult(PrecResult positive, PrecResult negative,
			RdfFileStats refsetStats, RdfFileStats linksetStats,
			int intersectionSize, Date startDate, Date endDate) {
		super();
		this.positive = positive;
		this.negative = negative;
		this.refsetStats = refsetStats;
		this.linksetStats = linksetStats;
		this.intersectionSize = intersectionSize;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public PrecResult getPositive() {
		return positive;
	}
	public PrecResult getNegative() {
		return negative;
	}
	public RdfFileStats getRefsetStats() {
		return refsetStats;
	}
	public RdfFileStats getLinksetStats() {
		return linksetStats;
	}
	public int getIntersectionSize() {
		return intersectionSize;
	}
	public Date getStartDate() {
		return startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
}


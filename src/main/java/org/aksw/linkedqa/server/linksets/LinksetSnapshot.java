package org.aksw.linkedqa.server.linksets;

import java.util.Date;

import org.aksw.linkedqa.shared.EvaluationResult;

class LinksetSnapshot
{
	private Date snapshotDate;
	
	private Date lastModificationDate;	
	private EvaluationResult evaluationResult;
	
	private LinksetItem parent;

	public LinksetSnapshot(Date snapshotDate, Date lastModificationDate,
			EvaluationResult evaluationResult, LinksetItem parent) {
		super();
		this.snapshotDate = snapshotDate;
		this.lastModificationDate = lastModificationDate;
		this.evaluationResult = evaluationResult;
		this.parent = parent;
	}

	public Date getSnapshotDate() {
		return snapshotDate;
	}

	public Date getLastModificationDate() {
		return lastModificationDate;
	}

	public EvaluationResult getEvaluationResult() {
		return evaluationResult;
	}

	public LinksetItem getParent() {
		return parent;
	}
}

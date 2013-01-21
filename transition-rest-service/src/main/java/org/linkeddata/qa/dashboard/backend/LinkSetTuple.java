package org.linkeddata.qa.dashboard.backend;

public class LinkSetTuple {
	@Override
	public String toString() {
		return "LinkSetTuple [FileName=" + FileName + ", Source=" + Source
				+ ", Target=" + Target + ", SourceEndpoint=" + SourceEndpoint
				+ ", TargetEndpoint=" + TargetEndpoint + ", LinkingType="
				+ LinkingType + ", TriplesNo=" + TriplesNo + "]";
	}
	private String FileName;// linkSetPath -spec.xml
	private String linksFile;
	private String Source;
	private String Target;
	private String SourceEndpoint;
	private String TargetEndpoint;
	private String LinkingType;
	private int TriplesNo=0;
	private String taskSetName;
	private String taskSetPath;
	private String revisionDate;
	
	public LinkSetTuple(){}
	public LinkSetTuple(String Source,	String Target,String SourceEndpoint,String TargetEndpoint,String LinkingType)
	{
		this.setSource(Source);
		this.setTarget(Target);
		this.setSourceEndpoint(SourceEndpoint);
		this.setTargetEndpoint(TargetEndpoint);
		this.setLinkingType(LinkingType);
	}
	public String getSource() {
		return Source;
	}
	public void setSource(String source) {
		Source = source;
	}
	public String getTarget() {
		return Target;
	}
	public void setTarget(String target) {
		Target = target;
	}
	public String getSourceEndpoint() {
		return SourceEndpoint;
	}
	public void setSourceEndpoint(String sourceEndpoint) {
		SourceEndpoint = sourceEndpoint;
	}
	public String getTargetEndpoint() {
		return TargetEndpoint;
	}
	public void setTargetEndpoint(String targetEndpoint) {
		TargetEndpoint = targetEndpoint;
	}
	public String getLinkingType() {
		return LinkingType;
	}
	public void setLinkingType(String linkingType) {
		LinkingType = linkingType;
	}
	public int getTriplesNo() {
		return TriplesNo;
	}
	public void setTriplesNo(int triplesNo) {
		TriplesNo = triplesNo;
	}
	public String getFileName() {
		return FileName;
	}
	public void setFileName(String fileName) {
		FileName = fileName;
	}
	public String getTaskName() {
		return taskSetName;
	}
	public void setTaskName(String taskName) {
		this.taskSetName = taskName;
	}
	public String getTaskSetPath() {
		return taskSetPath;
	}
	public void setTaskSetPath(String taskSetPath) {
		this.taskSetPath = taskSetPath;
	}
	public String getLinksFile() {
		return linksFile;
	}
	public void setLinksFile(String linksFile) {
		this.linksFile = linksFile;
	}
	public String getRevisionDate() {
		return revisionDate;
	}
	public void setRevisionDate(String revisionDate) {
		this.revisionDate = revisionDate;
	}

}

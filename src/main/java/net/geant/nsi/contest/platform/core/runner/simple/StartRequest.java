package net.geant.nsi.contest.platform.core.runner.simple;

public class StartRequest {
    private String requesterId;
    private String xmlData;
	
    public StartRequest(String requesterId, String xmlData) {
		super();
		this.requesterId = requesterId;
		this.xmlData = xmlData;
	}
	public final String getRequesterId() {
		return requesterId;
	}
	public final void setRequesterId(String requesterId) {
		this.requesterId = requesterId;
	}
	public final String getXmlData() {
		return xmlData;
	}
	public final void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}
	@Override
	public String toString() {
		return "StartRequest [requesterId=" + requesterId + ", xmlData="
				+ xmlData + "]";
	}
	
}

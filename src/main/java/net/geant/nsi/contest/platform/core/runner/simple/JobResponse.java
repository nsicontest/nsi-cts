package net.geant.nsi.contest.platform.core.runner.simple;

public class JobResponse {
    private String id;
    private String status;
    private String testReport;
    private String errorMsg;
    
    public JobResponse() {
    	
    }
    
	public JobResponse(String id, String status, String testReport,
			String errorMsg) {
		super();
		this.id = id;
		this.status = status;
		this.testReport = testReport;
		this.errorMsg = errorMsg;
	}
	
	public final String getId() {
		return id;
	}
	public final void setId(String id) {
		this.id = id;
	}
	public final String getStatus() {
		return status;
	}
	public final void setStatus(String status) {
		this.status = status;
	}
	public final String getTestReport() {
		return testReport;
	}
	public final void setTestReport(String testReport) {
		this.testReport = testReport;
	}
	public final String getErrorMsg() {
		return errorMsg;
	}
	public final void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "JobResponse [id=" + id + ", status=" + status + ", testReport="
				+ testReport + ", errorMsg=" + errorMsg + "]";
	}
    
	
}

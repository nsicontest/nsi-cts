package net.geant.nsi.contest.platform.core.runner.simple;

public class StartResponse {
    private String id;
    private String status;
    private String error;
    private String testReport;
    
    public StartResponse() {
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

	public final String getError() {
		return error;
	}

	public final void setError(String error) {
		this.error = error;
	}

	public final String getTestReport() {
		return testReport;
	}

	public final void setTestReport(String testReport) {
		this.testReport = testReport;
	}

	@Override
	public String toString() {
		return "StartResponse [id=" + id + ", status=" + status + ", error="
				+ error + ", testReport=" + testReport + "]";
	}
    
    
}

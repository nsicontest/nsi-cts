package net.geant.nsi.contest.platform.core.runner.data;

import java.util.UUID;

public class JobInfo {
	UUID id;
	JobStatus status;
	String errorMessage;
	String result;
	
	public JobInfo(UUID id, JobStatus status) {
		this.id = id;
		this.status = status;
	}
	
	public JobInfo(UUID id, JobStatus status, String errorMessage) {
		super();
		this.id = id;
		this.status = status;
		this.errorMessage = errorMessage;
	}

	public JobInfo(UUID id, JobStatus status, String errorMessage, String result) {
		super();
		this.id = id;
		this.status = status;
		this.errorMessage = errorMessage;
		this.result = result;
	}
 
	public final UUID getId() {
		return id;
	}
	public final void setId(UUID id) {
		this.id = id;
	}
	public final JobStatus getStatus() {
		return status;
	}
	public final void setStatus(JobStatus status) {
		this.status = status;
	}
	public final String getErrorMessage() {
		return errorMessage;
	}
	public final void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public final String getResult() {
		return result;
	}
	public final void setResult(String result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "JobInfo [id=" + id + ", status=" + status + ", errorMessage="
				+ errorMessage + ", result=" + result + "]";
	}
}

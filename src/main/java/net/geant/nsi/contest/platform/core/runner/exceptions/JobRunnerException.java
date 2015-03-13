package net.geant.nsi.contest.platform.core.runner.exceptions;

import java.util.UUID;

import net.geant.nsi.contest.platform.core.exceptions.CTSException;

public class JobRunnerException extends CTSException {
	private UUID jobId;
	
	public JobRunnerException() {
		super();
	}
	
	public JobRunnerException(UUID jobId) {
		super();
		this.jobId = jobId;
	}

	public JobRunnerException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}	
	
	public JobRunnerException(String arg0, Throwable arg1, UUID jobId) {
		super(arg0, arg1);
		this.jobId = jobId;
	}

	public JobRunnerException(String arg0) {
		super(arg0);
	}	
	
	public JobRunnerException(String arg0, UUID jobId) {
		super(arg0);
		this.jobId = jobId;
	}

	public JobRunnerException(Throwable arg0) {
		super(arg0);
	}

	public JobRunnerException(Throwable arg0, UUID jobId) {
		super(arg0);
		this.jobId = jobId;
	}

	public final UUID getJobId() {
		return jobId;
	}
	
}

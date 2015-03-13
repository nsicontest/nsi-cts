package net.geant.nsi.contest.platform.web.data;

import java.util.Date;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import net.geant.nsi.contest.platform.data.ResultStatus;
import net.geant.nsi.contest.platform.data.Test.Status;
import net.geant.nsi.contest.platform.web.data.forms.TemplateForm;

import org.springframework.data.annotation.CreatedDate;

public class Test {
	
	@NotNull
	private UUID testId;
	
	@CreatedDate
	private Date createdAt;
	
	@NotNull
	TemplateForm template;
	
	private Status status;
	
	private ResultStatus resultStatus;

	private String errorMessage;
	
	protected Test() {
	}
	
	public Test(UUID testId, TemplateForm template) {
		this.testId = testId;
		this.template = template;
	}
	
	public UUID getTestId() {
		return testId;
	}

	public void setTestId(UUID testId) {
		this.testId = testId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public TemplateForm getTemplate() {
		return template;
	}

	public void setTemplate(TemplateForm template) {
		this.template = template;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public ResultStatus getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}

	public final String getErrorMessage() {
		return errorMessage;
	}

	public final void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}

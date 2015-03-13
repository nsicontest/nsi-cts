package net.geant.nsi.contest.platform.web.data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import net.geant.nsi.contest.platform.data.AgentType;
import net.geant.nsi.contest.platform.data.ResultStatus;
import net.geant.nsi.contest.platform.data.TestCase.Status;
import net.geant.nsi.contest.platform.web.data.forms.TemplateForm;

import org.springframework.data.annotation.CreatedDate;

public class TestCase {
	@NotNull
	private UUID testCaseId;
	
	@CreatedDate
	private Date createdAt;	
	
	Status status;
	
	//TemplateForm template;
	
	private AgentType certification;
	
	ResultStatus resultStatus;
	
	int testsCount;
	
	List<Test> tests;
	
	public TestCase() {
		super();
	}

	public TestCase(UUID testCaseId, Date createdAt, Status status, ResultStatus resultStatus) {
		super();
		this.testCaseId = testCaseId;
		this.createdAt = createdAt;
		this.status = status;
		this.resultStatus = resultStatus;
	}

	public UUID getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(UUID testCaseId) {
		this.testCaseId = testCaseId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public final ResultStatus getResultStatus() {
		return resultStatus;
	}

	public final void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}

//	public TemplateForm getTemplate() {
//		return template;
//	}
//
//	public void setTemplate(TemplateForm template) {
//		this.template = template;
//	}
	
	public final List<Test> getTests() {
		return tests;
	}

	public final int getTestsCount() {
		return testsCount;
	}

	public final void setTestsCount(int testsCount) {
		this.testsCount = testsCount;
	}

	public final void setTests(List<Test> tests) {
		this.tests = tests;
	}

	public final AgentType getCertification() {
		return certification;
	}

	public final void setCertification(AgentType certification) {
		this.certification = certification;
	}
	
	
}

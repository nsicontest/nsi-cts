package net.geant.nsi.contest.platform.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class TestCase {	
	
	public enum Status {
		CREATED, SCHEDULED, INITIALIZING, RUNNING, DESTROYING, FINISHED;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	private UUID testCaseId;
	
	@CreatedDate
	private Date createdAt;	
	
	@ManyToOne(optional=false, cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
	private Project project;
	
	private Status status;
	
	private AgentType certification;
	
	@OneToMany(mappedBy="testCase", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	private List<Test> tests = new ArrayList<Test>();

	protected TestCase() {
		testCaseId = UUID.randomUUID();
		status = Status.CREATED;
		createdAt = new Date();
	}
	
	public TestCase(Project project) {
		this();
		this.project = project;
	}
	
	public TestCase(Project project, AgentType certification) {
		this(project);
		this.certification = certification;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public UUID getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(UUID testCaseId) {
		this.testCaseId = testCaseId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
		if(status == Status.SCHEDULED)
			for(Test t: tests)
				t.setStatus(Test.Status.SCHEDULED);
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public final AgentType getCertification() {
		return certification;
	}

	public final void setCertification(AgentType certification) {
		this.certification = certification;
	}

	public final List<Test> getTests() {
		return tests;
	}

	public void setTests(List<Test> tests) {
		this.tests = tests;
	}

	public Test getTest(UUID testId) throws ResourceNotFoundException {
		for(Test t : tests)
			if(t.getTestId() == testId)
				return t;
		throw new ResourceNotFoundException("Test case does not contain test " + testId);
	}
	
	public boolean hasTest(UUID testId) {
		try {
			return (getTest(testId) != null);
		} catch (ResourceNotFoundException e) {
			return false;
		}
	}
	
	@Transient
	public int countTests(Test.Status status) {
		int count = 0;
		for(Test test : tests)
			if(test.getStatus() == status)
				count++;
		return count;
	}
	
	@Transient
	public int countTests(ResultStatus resultStatus) {
		int count = 0;
		for(Test test : tests)
			if(test.getResultStatus() == resultStatus)
				count++;
		return count;
	}
	
//	@Transient
//	public Test getNext(Test.Status status) {
//		for(Test t : tests) {
//			if(t.getStatus() == status)
//				return t;
//		}
//		return null;
//	}
	
	@Transient
	public ResultStatus getResultStatus() {
		ResultStatus status = ResultStatus.PASSED;
		for(Test t : tests) {
			switch(t.getResultStatus()) {
			case FAILED:
				return ResultStatus.FAILED;
			case NA:
				return ResultStatus.NA;
			case PASSED:
				break;
			case PASSED_CONDITIONALLY:
				status = ResultStatus.PASSED_CONDITIONALLY;
				break;
			default:
				break;
			}
		}
		return status;
	}
	
	@Transient
	public Test createTest(TestCaseTemplate template) {
		Test test = new Test(this, template);
		tests.add(test);
		return test;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result
				+ ((testCaseId == null) ? 0 : testCaseId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestCase other = (TestCase) obj;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		if (testCaseId == null) {
			if (other.testCaseId != null)
				return false;
		} else if (!testCaseId.equals(other.testCaseId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TestCase [id=" + id + ", testCaseId=" + testCaseId
				+ ", createdAt=" + createdAt + ", project=" + (project != null ? project.getKey() : null)
				+ ", status=" + status + ", tests=" + tests.size() + "]";
	}
	
	
	
}

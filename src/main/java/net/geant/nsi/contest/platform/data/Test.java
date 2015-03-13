package net.geant.nsi.contest.platform.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Test {
	
	public enum Status {
		CREATED, SCHEDULED, INITIALIZING, INPROGRESS, /* RELEASING, */ FINISHED, INTERRUPTED, FAILED;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	private UUID testId;
	
	@CreatedDate
	private Date createdAt;		
	
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	private TestCase testCase;
	
	@OneToMany(mappedBy="test", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	private List<LogEntry> logs = new ArrayList<LogEntry>();
	
	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.EAGER)
	private TestCaseTemplate template;

	@OneToMany(mappedBy="test", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	private List<NSIInstance> instances = new ArrayList<NSIInstance>();
	
	private Status status;
	
	private ResultStatus resultStatus;
	
	
	//TODO: This should be changed and removed
	@Lob 
	@Basic(fetch=FetchType.LAZY)
	private String result;
	
	//TODO: This should be changed and removed
	private String errorMessage;

	protected Test() {
		testId = UUID.randomUUID();
		status = Status.CREATED;
		resultStatus = ResultStatus.NA;
		createdAt = new Date();
	}
	
	public Test(TestCase testCase, TestCaseTemplate template) {
		this();
		if(testCase == null)
			throw new IllegalArgumentException("TestCase is null.");
		if(template == null)
			throw new IllegalArgumentException("Template is null.");
		
		this.testCase = testCase;
		this.template = template;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public TestCase getTestCase() {
		return testCase;
	}

	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
	}

	public List<LogEntry> getLogs() {
		return logs;
	}

	public void setLogs(List<LogEntry> logs) {
		this.logs = logs;
	}

	public TestCaseTemplate getTemplate() {
		return template;
	}

	public void setTemplate(TestCaseTemplate template) {
		this.template = template;
	}

	public List<NSIInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<NSIInstance> instances) {
		this.instances = instances;
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
	
	public final String getResult() {
		return result;
	}

	public final void setResult(String result) {
		this.result = result;
	}

	public final String getErrorMessage() {
		return errorMessage;
	}

	public final void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
	@Transient
	public void addInstance(NSIInstance instance) {
		if(instance != null) {
			instance.setTest(this);
			instances.add(instance);
		}
	}
	
	@Transient
	public NSIInstance getInstance(String name) {
		for(NSIInstance i : instances)
			if(i.getName().equalsIgnoreCase(name))
				return i;
		return null;
	}
	
	@Transient
	public void removeInstance(String name) {
		for(NSIInstance i : instances)
			if(i.getName().equalsIgnoreCase(name)) {
				instances.remove(i);		
				return;
			}
	}

	@Transient
	public void addLog(LogEntry log) {
		if(log != null) {
			log.setTest(this);
			logs.add(log);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((template == null) ? 0 : template.hashCode());
		result = prime * result
				+ ((testCase == null) ? 0 : testCase.hashCode());
		result = prime * result + ((testId == null) ? 0 : testId.hashCode());
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
		Test other = (Test) obj;
		if (template == null) {
			if (other.template != null)
				return false;
		} else if (!template.equals(other.template))
			return false;
		if (testCase == null) {
			if (other.testCase != null)
				return false;
		} else if (!testCase.equals(other.testCase))
			return false;
		if (testId == null) {
			if (other.testId != null)
				return false;
		} else if (!testId.equals(other.testId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Test [id=" + id + ", testId=" + testId + ", createdAt="
				+ createdAt + ", testCase=" + testCase + ", logs=" + logs
				+ ", template=" + template + ", instances=" + instances
				+ ", status=" + status + ", resultStatus=" + resultStatus
				+ ", result=" + result + ", errorMessage=" + errorMessage + "]";
	}

}

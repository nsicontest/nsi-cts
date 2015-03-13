package net.geant.nsi.contest.platform.data;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Job {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	
	UUID jobId;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.EAGER)
	TestCase testCase;
	
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.EAGER)
	Test test;
	
	Long taskId;
	
	public Job() {
		super();
	}
	
	public Job(UUID jobId, TestCase testCase, Test test) {
		this();
		this.jobId = jobId;
		this.testCase = testCase;
		this.test = test;
	}
	
	public final Long getId() {
		return id;
	}
	public final void setId(Long id) {
		this.id = id;
	}
	public final UUID getJobId() {
		return jobId;
	}
	public final void setJobId(UUID jobId) {
		this.jobId = jobId;
	}
	public final TestCase getTestCase() {
		return testCase;
	}
	public final void setTestCase(TestCase testCase) {
		this.testCase = testCase;
	}
	public final Test getTest() {
		return test;
	}
	public final void setTest(Test test) {
		this.test = test;
	}
	
	
	
	public final Long getTaskId() {
		return taskId;
	}

	public final void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
		result = prime * result + ((test == null) ? 0 : test.hashCode());
		result = prime * result
				+ ((testCase == null) ? 0 : testCase.hashCode());
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
		Job other = (Job) obj;
		if (jobId == null) {
			if (other.jobId != null)
				return false;
		} else if (!jobId.equals(other.jobId))
			return false;
		if (test == null) {
			if (other.test != null)
				return false;
		} else if (!test.equals(other.test))
			return false;
		if (testCase == null) {
			if (other.testCase != null)
				return false;
		} else if (!testCase.equals(other.testCase))
			return false;
		return true;
	}
	
}

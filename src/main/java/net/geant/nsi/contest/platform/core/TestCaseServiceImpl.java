package net.geant.nsi.contest.platform.core;

import java.util.List;
import java.util.UUID;

import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.core.exceptions.TestException;
import net.geant.nsi.contest.platform.core.runner.TestRunner;
import net.geant.nsi.contest.platform.core.tasks.TaskManager;
import net.geant.nsi.contest.platform.data.AgentType;
import net.geant.nsi.contest.platform.data.Project;
import net.geant.nsi.contest.platform.data.Test;
import net.geant.nsi.contest.platform.data.TestCase;
import net.geant.nsi.contest.platform.data.TestCase.Status;
import net.geant.nsi.contest.platform.data.TestCaseTemplate;
import net.geant.nsi.contest.platform.persistence.JobDAO;
import net.geant.nsi.contest.platform.persistence.TestCaseDAO;
import net.geant.nsi.contest.platform.persistence.TestDAO;
import net.geant.nsi.contest.platform.persistence.exceptions.CTSPersistenceException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestCaseServiceImpl implements TestCaseService, TestCaseListener  {
	private final static Logger log = Logger.getLogger(TestCaseServiceImpl.class);
	
	@Autowired
	TestCaseDAO testCases;
	
	@Autowired
	TestDAO tests;

	@Autowired
	JobDAO jobs;
	
//	@Autowired
//	TestCaseRepository testCases;
//	
//	@Autowired 
//	JobReposiory jobs;
	
	@Autowired
	ProjectService projects;
	
	@Autowired
	TaskManager taskManager;

	@Autowired
	TestRunner testRunner;
	
	@Autowired
	TestCaseTemplateService templates;
	
	@Autowired
	Jaxb2Marshaller oxm;
	
	private long testTimeout = 3000*1000;
	private long pullPeriod = 60*1000;

	public TestCaseServiceImpl() {
		
	}
	
	public TestCaseServiceImpl(int testTimeout, int pullPeriod) {
		if(testTimeout < 0) testTimeout = 0;
		if(pullPeriod < 0) pullPeriod = 0;
		
		this.testTimeout = testTimeout * 1000;
		this.pullPeriod = this.pullPeriod * 1000;
	}
	
	public TestCase createFor(Project project, List<TestCaseTemplate> templates) throws ResourceNotFoundException {
		if(project == null)
			throw new IllegalArgumentException("Project cannot be null");
		
		try {
			project = projects.findBy(project.getKey());
		} catch (ResourceNotFoundException e) {
			throw new ResourceNotFoundException("Project " + project.getKey() + " not found");
		}
		
		TestCase testCase = new TestCase(project);
		if(templates != null) {
			for(TestCaseTemplate t : templates) {
				Test test = testCase.createTest(t);
				//tests.save(test);
			}
		}
		
		testCase = testCases.save(testCase);
		
		project.addTestCase(testCase);
		projects.update(project);
		return testCase;
		//return testCases.save(testCase);
		//project.addTestCase(testCase);
		//projects.update(project);
		
		//return testCase;
	}
	
	public TestCase createCertificatedFor(Project project, AgentType type) throws ResourceNotFoundException {
		if(project == null)
			throw new IllegalArgumentException("Project cannot be null");
		
		try {
			project = projects.findBy(project.getKey());
		} catch (ResourceNotFoundException e) {
			throw new ResourceNotFoundException("Project " + project.getKey() + " not found");
		}
			
		List<TestCaseTemplate> selectedTemplates = templates.findBy(type);
		if(selectedTemplates == null || selectedTemplates.isEmpty())
			throw new ResourceNotFoundException("Unable to create test case for " + project.getKey() + " as there are no templates for selected type " + type);
		
		TestCase testCase = new TestCase(project, type);

		if(selectedTemplates != null) {
			for(TestCaseTemplate t : selectedTemplates) {
				if(t.isCertification()) {
					Test test = testCase.createTest(t);
					//tests.save(test);
				}
			}
		}
		
		testCase = testCases.save(testCase);
		
		project.addTestCase(testCase);
		projects.update(project);
		return testCase;
	}

	
	
	protected void perform(TestCase testCase) {
		
		if(testCase.getStatus() != TestCase.Status.CREATED) {
			log.warn("Test case " + testCase.getTestCaseId() + " already executed.");
			return;
		}
		
		testCase.setStatus(Status.SCHEDULED);
		
		TestCaseProcessTask task = new TestCaseProcessTask(testCase, testRunner, this, oxm);
		task.setPollPeriod(getPullPeriod());
		task.setTimeOutPerTest(getTestTimeout());
		
		taskManager.queue(task, "Project '" + testCase.getProject().getName() + "' (" + testCase.getProject().getKey().toString() + ")");
		
		testCases.save(testCase);
	}
	
	@Transactional
	public void perform(UUID testCaseId) throws ResourceNotFoundException {
		TestCase testCase;
		try {
			testCase = testCases.findByTestCaseId(testCaseId);
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Unable to find test case " + testCaseId, ex);
		}
		
		if(testCase == null)
			throw new ResourceNotFoundException("Unable to find test case " + testCaseId);
		
		perform(testCase);
	}
	
	@Transactional 
	public List<TestCase> getAll(Project project) throws ResourceNotFoundException {
		if(project == null)
			throw new IllegalArgumentException("Project cannot be null");

		try {
			return testCases.findByProject(project);
		} catch (CTSPersistenceException e) {
			throw new ResourceNotFoundException("Unable to get test cases for project " + project.getKey());
		}
	}
	
	@Transactional
	public TestCase findBy(UUID testCaseId) throws ResourceNotFoundException {
		TestCase tc = null;
		try {
			tc = testCases.findByTestCaseId(testCaseId);
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Unable to find test case " + testCaseId + " due to some errors. ", ex);
		}
		if(tc == null)
			throw new ResourceNotFoundException("Unable to find test case " + testCaseId);
		return tc;
	}

	
	
//	//@Override
//	@Transactional
//	public void update(JobInfo jobInfo) {
//		log.info("Updating job... " + jobInfo);
//		Job job = null;
//		try {
//			job = jobs.findByJobId(jobInfo.getId());
//		} catch (CTSPersistenceException ex) {
//			//TODO: update
//			log.error("Unable to retrieve job " + jobInfo.getId(), ex);
//		}
//		
//		if(job == null) {
//			log.error("Unable to find job " + jobInfo.getId());
//			return;
//		}
//		
//		Test test = job.getTest();
//		if(test.getStatus() != Test.Status.INPROGRESS) {
//			log.error("Incoming update for test " + test.getTestId() + " that is not in progress.");
//			return;
//		}
//			
//		updateTestStatus(jobInfo, test);
//		
//		test.setResult(jobInfo.getResult());
//		
//		tests.save(test);
//		testCases.save(test.getTestCase());
//		if(jobInfo.getStatus() == JobStatus.ABORTED_ERROR 
//				|| jobInfo.getStatus() == JobStatus.COMPLETED_ERROR
//				|| jobInfo.getStatus() == JobStatus.COMPLETED_OK) {
//			taskExecutor.cancel(job.getTaskId());
//			executeNextTest(job.getTestCase());
//		}
//	}
//
//	private void updateTestStatus(JobInfo jobInfo, Test test) {
//		switch(jobInfo.getStatus()) {
//			case ABORTED_ERROR:
//			case COMPLETED_ERROR:
//				test.setStatus(Test.Status.FINISHED);
//				test.setResultStatus(ResultStatus.FAILED);
//				break;
//			case COMPLETED_OK:
//				test.setStatus(Test.Status.FINISHED);
//				test.setResultStatus(ResultStatus.PASSED);
//				break;
//			case QUEUED:
//				test.setStatus(Test.Status.SCHEDULED);
//				break;
//			case STARTED:
//				test.setStatus(Test.Status.INPROGRESS);
//				break;
//			default:
//				break;
//		}
//	}
//
//	//@Override
//	@Transactional
//	public void notifyError(UUID jobId, Exception ex) {
//		log.error("Error reported for job " + jobId, ex);
//		
//		Job job = null;
//		try {
//			job = jobs.findByJobId(jobId);
//		} catch (CTSPersistenceException e) {
//			log.error("Cannot find job " + jobId, ex);
//		}
//		
//		if(job == null) {
//			log.error("Unable to find job " + jobId);
//			return;
//		}
//		
//		Test test = job.getTest();
//		if(test.getStatus() != Test.Status.INPROGRESS) {
//			log.error("Incoming error notification for test " + test.getTestId() + " that is not in progress.");
//			return;
//		} 
//		
//		test.setErrorMessage(ex.getMessage());
//		test.setStatus(Test.Status.FINISHED);
//		test.setResultStatus(ResultStatus.FAILED);
//		taskExecutor.cancel(job.getTaskId());
//		
//		tests.save(test);
//		testCases.save(test.getTestCase());
//		
//		executeNextTest(job.getTestCase());
//	}
//	
//	protected void executeNextTest(TestCase testCase) {
//		log.debug("Execute next test for test case " + testCase.getTestCaseId());
//		
//		Test test = testCase.getNext(Test.Status.CREATED);
//		if(test != null) {
//			log.debug("Executing test " + test.getTestId());
//			Job job = executeTest(test);
//		} else {
//			log.info("No more test to execute in test case " + testCase.getTestCaseId());
//			testCase.setStatus(TestCase.Status.FINISHED);
//			testCases.save(testCase);
//		}
//	}
//	
//	@Transactional
//	protected synchronized Job executeTest(Test test) {
//		log.debug("Execute test for test " + test.getTestId());
//				
//		JobInfo jobInfo = null;
//		Job job = null;
//
//		try {
//			if((job = jobs.findByTest(test)) != null) {
//				log.warn("Job already executed for test " + test.getTestId());
//				return job;
//			}
//		} catch (CTSPersistenceException e) {
//			log.error("Unable to find job for test " + test.getTestId());
//			return null;
//		}
//		
//		try {
//			log.debug("Executing test " + test.getTestId());
//			jobInfo = testRunner.start(test.getTestId(), test.getTemplate().getTemplate());
//			job = new Job(jobInfo.getId(), test.getTestCase(), test);
//			test.setStatus(Test.Status.INPROGRESS);
//			
//			PullTestResultsTask task = new PullTestResultsTask(jobInfo.getId(), testRunner, this);
//			//TODO: fix it
//			//long taskId = taskExecutor.addTask(task, pullPeriod);
//						
//			//job.setTaskId(taskId);
//			
//		} catch (JobRunnerException ex) {
//			test.setStatus(Test.Status.FINISHED);
//			test.setResultStatus(ResultStatus.FAILED);
//			log.error("Unable to start test " + test.getTestId(), ex);
//		} finally {
//			if(job != null) jobs.save(job);
//			testCases.save(test.getTestCase());
//		}
//		return job;
//	}

	@Override
	public List<Test> getTestsFor(UUID testCaseId)
			throws ResourceNotFoundException {
		
		TestCase testCase = null;
		try {
			testCase = testCases.findByTestCaseId(testCaseId);
			return tests.findByTestCase(testCase);
		} catch (CTSPersistenceException ex) {
			throw new ResourceNotFoundException("Unable to get tests for testcase " + testCaseId, ex);
		}
	}

	public Test findTest(UUID testId) throws ResourceNotFoundException {
		Test test = null;
		try {
			test = tests.findByTestId(testId);
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Unable to find test " + testId + " due to some errors.", ex);
		}
		if(test == null)
			throw new ResourceNotFoundException("Unable to find test " + testId);
		return test;
	}
	
	public final long getTestTimeout() {
		return testTimeout;
	}

	public final void setTestTimeout(long testTimeout) {
		if(testTimeout < 0) testTimeout = 0;		
		this.testTimeout = testTimeout * 1000;
	}

	public final long getPullPeriod() {
		return pullPeriod;
	}

	public final void setPullPeriod(long pullPeriod) {
		if(pullPeriod < 0) pullPeriod = 0;		
		this.pullPeriod = this.pullPeriod * 1000;
	}

	@Override
	@Transactional
	public void update(TestCase testCase) {
		if(testCase == null) {
			log.error("Unable to update empty test case.");
			return;
		}
			
		log.info("TestCase: " + testCase.getTestCaseId() + " update");
		testCases.save(testCase);
	}
	
	@Override
	@Transactional
	public void update(Test test) {
		if(test == null) {
			log.error("Unable to update empty test.");
			return;
		}
		log.info("Test: " + test.getTestId() + " update " + test.getStatus());
		tests.save(test);		
	}

	@Override
	@Transactional
	public void update(Test test, TestException testException) {
		if(test == null) {
			log.error("Unable to update empty test case.");
			return;
		}
		
		log.error("Test: " + test.getTestId() + " reports error " + test.getErrorMessage(), testException);
		tests.save(test);
	}
	
	
	
}

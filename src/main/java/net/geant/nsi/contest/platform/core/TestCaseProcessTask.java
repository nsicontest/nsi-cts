package net.geant.nsi.contest.platform.core;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.StringSource;

import net.geant.nsi.contest.platform.core.exceptions.TestException;
import net.geant.nsi.contest.platform.core.runner.TestRunner;
import net.geant.nsi.contest.platform.core.runner.data.JobInfo;
import net.geant.nsi.contest.platform.core.runner.exceptions.JobRunnerException;
import net.geant.nsi.contest.platform.data.ResultStatus;
import net.geant.nsi.contest.platform.data.Test;
import net.geant.nsi.contest.platform.data.TestCase;
import net.geant.nsi.contest.platform.data.TestCase.Status;
import net.geant.nsi.contest.platform.web.data.runner.Operation;
import net.geant.nsi.contest.platform.web.data.runner.Result;
import net.geant.nsi.contest.platform.web.data.runner.Scenario;
import net.geant.nsi.contest.platform.web.data.runner.Section;

public class TestCaseProcessTask implements Runnable {
	private final static Logger log = Logger.getLogger(TestCaseProcessTask.class);
			
	TestCase testCase;
	
	TestRunner testRunner;
	
	TestCaseListener listener;
	
	Jaxb2Marshaller oxm;
	
	long timeOutPerTest = 0;
	long pollPeriod = 15 * 1000; //15 sec
	
	public TestCaseProcessTask(TestCase testCase, TestRunner testRunner, TestCaseListener listener, Jaxb2Marshaller oxm) {
		if(testCase == null)
			throw new IllegalArgumentException("testCase is null");
		if(testRunner == null)
			throw new IllegalArgumentException("testRunner is null");
		if(listener == null)
			throw new IllegalArgumentException("listener is null");
		if(oxm == null)
			throw new IllegalArgumentException("oxm is null");
		
		this.testCase = testCase;
		this.testRunner = testRunner;
		this.listener = listener;
		this.oxm = oxm;
	}
	
	@Override
	public void run() {
		log.info("Starting task for test case " + testCase.getTestCaseId());
		
		testCase.setStatus(Status.RUNNING);
		listener.update(testCase);
		
		boolean continueNext = true;
		for(Test test : testCase.getTests()) {
			continueNext = process(test);
			if(!continueNext)
				break;
		}
		
		testCase.setStatus(Status.FINISHED);
		listener.update(testCase);
		
		log.info("Finishing task for test case " + testCase.getTestCaseId());
	}

	/**
	 * perform single test 
	 * 
	 * @param test
	 * @return true if test case should continue, false otherwise
	 */
	private boolean process(Test test) {
		log.info("Starting test " + test.getTestId());
		try {
			long startTime = System.currentTimeMillis();
			
			JobInfo jobInfo = testRunner.start(test.getTestId(), test.getTemplate().getTemplate());
			
			log.debug("Test: " + test.getTestId() + " JobInfo: " + jobInfo);
			
			test.setStatus(Test.Status.INITIALIZING);
			listener.update(test);
			
			while(timeOutPerTest == 0 || startTime + timeOutPerTest >= System.currentTimeMillis()) {
				Thread.sleep(pollPeriod);
				jobInfo = testRunner.get(jobInfo.getId());
			
				log.debug("Test: " + test.getTestId() + " JobInfo: " + jobInfo);
				
				updateTestStatus(jobInfo, test);
				updateTestResult(jobInfo, test);
				
				listener.update(test);
				
				if(test.getStatus() == Test.Status.FINISHED
					|| test.getStatus() == Test.Status.FAILED)
					break;
			}
			if(test.getStatus() != Test.Status.FINISHED)
				test.setStatus(Test.Status.FAILED);
		} catch (JobRunnerException e) {
			log.error("Error reported in test " + test.getTestId(), e);
			String errorMsg = "Unable to process test due to '" + e.getMessage() + "'";
			test.setResultStatus(ResultStatus.FAILED);
			test.setStatus(Test.Status.FAILED);
			test.setErrorMessage(errorMsg);
			listener.update(test, new TestException(errorMsg, e));
		} catch (InterruptedException e) {
			log.error("Error reported in test " + test.getTestId(), e);
			String errorMsg = "TestCase task has been interrupted. " + e.getMessage();
			test.setResultStatus(ResultStatus.FAILED);
			test.setStatus(Test.Status.INTERRUPTED);
			test.setErrorMessage(errorMsg);
			listener.update(test, new TestException(errorMsg, e));
			return false;
		}
		
		log.info("Finishing test " + test.getTestId());
		return true;
	}

	private void updateTestResult(JobInfo jobInfo, Test test) {
		test.setResult(jobInfo.getResult());
		test.setErrorMessage(jobInfo.getErrorMessage());
	}
	
	private void updateTestStatus(JobInfo jobInfo, Test test) {
		switch(jobInfo.getStatus()) {
			case ABORTED_ERROR:
			case COMPLETED_ERROR:
				test.setStatus(Test.Status.FINISHED);
				test.setResultStatus(ResultStatus.FAILED);
				test.setErrorMessage(jobInfo.getErrorMessage());
				break;
			case COMPLETED_OK:
				test.setStatus(Test.Status.FINISHED);
				//TODO: fix it
				//test.setResultStatus(ResultStatus.PASSED);
				try {
					test.setResultStatus(computeResultStatus(jobInfo));
				} catch(Exception ex) {
					log.error("Error parsing results for testcase " + testCase.getTestCaseId() + " in test " + test.getTestId(), ex);
					test.setStatus(Test.Status.FINISHED);
					test.setResultStatus(ResultStatus.FAILED);
					test.setErrorMessage("Unable to parse result.");					
				}
				
				break;
			case QUEUED:
				test.setStatus(Test.Status.SCHEDULED);
				break;
			case STARTED:
				test.setStatus(Test.Status.INPROGRESS);
				break;
			default:
				break;
		}
	}
	
	protected ResultStatus computeResultStatus(JobInfo jobInfo) {
		Scenario scenario = (Scenario) oxm.unmarshal(new StringSource(jobInfo.getResult()));

		boolean conditionallyPassed = false;
		
		if(scenario == null)
			return ResultStatus.NA;
			
		Section section = scenario.getSection();
		if(section == null)
			return ResultStatus.PASSED;
		
		for(Operation op : section.getOperations()) {
			Result result = op.getResult();
			if(result == null) 
				return ResultStatus.NA;
			else if(result.isSuccess())
				continue;
			else if(!result.isSuccess() && op.isContinueOnError()) {
				conditionallyPassed = true;
				continue;
			} else if(!result.isSuccess())
				return ResultStatus.FAILED;
		}
		
		return (conditionallyPassed ? ResultStatus.PASSED_CONDITIONALLY : ResultStatus.PASSED);
	}
	
	public final void setTimeOutPerTest(long timeOutPerTest) {
		if(timeOutPerTest < 0)
			this.timeOutPerTest = 0;
		else
			this.timeOutPerTest = timeOutPerTest;
	}

	public final void setPollPeriod(long pollPeriod) {
		if(pollPeriod < 1000)
			pollPeriod = 1000;
		else
			this.pollPeriod = pollPeriod;
	}

	@Override
	public String toString() {
		return "TestCaseProcessTask [testCase=" + testCase + ", testRunner="
				+ testRunner + ", listener=" + listener + ", timeOutPerTest="
				+ timeOutPerTest + ", pollPeriod=" + pollPeriod + "]";
	}

}

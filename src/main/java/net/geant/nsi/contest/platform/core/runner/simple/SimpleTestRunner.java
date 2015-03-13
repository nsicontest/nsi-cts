package net.geant.nsi.contest.platform.core.runner.simple;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import net.geant.nsi.contest.platform.core.runner.TestRunner;
import net.geant.nsi.contest.platform.core.runner.data.JobInfo;
import net.geant.nsi.contest.platform.core.runner.data.JobStatus;
import net.geant.nsi.contest.platform.core.runner.exceptions.JobRunnerException;

public class SimpleTestRunner implements TestRunner {
	private final static Logger log = Logger.getLogger(SimpleTestRunner.class);
	
	String url;
	
	public SimpleTestRunner(String url) throws MalformedURLException {
		URL u = new URL(url);
		this.url = url;
	}
	
	@Override
	public JobInfo start(UUID testId, String template) throws JobRunnerException {
		RestTemplate client = new RestTemplate();
		StartRequest request = new StartRequest(testId.toString(), template);
		
		StartResponse response = null;
		try {
			log.info("Start runner " + url + "/job for " + request.getRequesterId());
			response = client.postForObject(url + "/job", request, StartResponse.class);
			log.debug("Response for requesterId=" + request.getRequesterId() + " : " + response);
		} catch(RestClientException ex) {
			log.error("Unable to start a new job for test " + testId, ex);
			throw new JobRunnerException("Unable to start a new job for test " + testId, ex);
		}
		
		JobStatus status = JobStatus.valueOf(response.getStatus());
		
		return new JobInfo(UUID.fromString(response.getId()),
							status,
							response.getError());
	}

	@Override
	public JobInfo get(UUID jobId) throws JobRunnerException {
		RestTemplate client = new RestTemplate();
		
		JobResponse response = null;
		try {
			log.info("Query runner " + url + "/job for jobId=" + jobId);
			response = client.getForObject(url + "/job/" + jobId, JobResponse.class);
			log.debug("Response for jobId=" + jobId + " : " + response);
		} catch(RestClientException ex) {
			log.error("Unable to retrieve information for job " + jobId, ex);
			throw new JobRunnerException("Unable to retrieve information for job " + jobId, ex);
		}
		
		JobStatus status = JobStatus.valueOf(response.getStatus());
		
		return new JobInfo(UUID.fromString(response.getId()),
							status,
							response.getErrorMsg(),
							response.getTestReport());
	}

}

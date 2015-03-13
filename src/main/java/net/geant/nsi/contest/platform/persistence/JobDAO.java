package net.geant.nsi.contest.platform.persistence;

import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import net.geant.nsi.contest.platform.data.Job;
import net.geant.nsi.contest.platform.data.Test;
import net.geant.nsi.contest.platform.data.TestCase;
import net.geant.nsi.contest.platform.data.TestCaseTemplate;
import net.geant.nsi.contest.platform.persistence.exceptions.CTSPersistenceException;

public class JobDAO extends GenericDAO<Job> {

	public JobDAO() {
		setClass(Job.class);
	}
	
	public Job findByJobId(UUID jobId) throws CTSPersistenceException {
		if(jobId == null)
			throw new IllegalArgumentException("Job id is empty.");
		
		try {
			Query query = em.createQuery("from " + clazz.getName() + " j where j.jobId=:jobId");
			query.setParameter("jobId", jobId);
			return (Job)query.getSingleResult();
		} catch(NoResultException ex) {
			return null;
		} catch(NonUniqueResultException ex) {
			throw new CTSPersistenceException("Ambiguous number of jobs '" + jobId + "'.");
		} catch(Exception ex) {
			throw new CTSPersistenceException("Unable to query job '" + jobId + "'. ", ex);
		}
	}
	
	public Job findByTest(Test test) throws CTSPersistenceException {
		if(test == null)
			throw new IllegalArgumentException("Test is null");
		
		try {
			Query query = em.createQuery("select j from " + clazz.getName() + " j join j.test t where t=:test");
			query.setParameter("test", test);
			return (Job)query.getSingleResult();
		} catch(NoResultException ex) {
			return null;
		} catch(NonUniqueResultException ex) {
			throw new CTSPersistenceException("Ambiguous number of jobs for test '" + test.getTestId() + "'.");
		} catch(Exception ex) {
			throw new CTSPersistenceException("Unable to query job for test '" + test.getTestId() + "'. ", ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Job> findByTestCase(TestCase testCase) throws CTSPersistenceException {
		if(testCase == null)
			throw new IllegalArgumentException("Test case is null");
		
		try {
			Query query = em.createQuery("select j from " + clazz.getName() + " j join j.testCase tc where tc=:testCase");
			query.setParameter("testCase", testCase);
			return query.getResultList();
		} catch (Exception ex) {
			throw new CTSPersistenceException("Unable to get jobs for test case " + testCase.getTestCaseId(), ex);
		}
	}
	
}

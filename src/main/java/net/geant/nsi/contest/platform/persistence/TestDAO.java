package net.geant.nsi.contest.platform.persistence;

import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import net.geant.nsi.contest.platform.data.Test;
import net.geant.nsi.contest.platform.data.TestCase;
import net.geant.nsi.contest.platform.persistence.exceptions.CTSPersistenceException;

public class TestDAO extends GenericDAO<Test>{
	
	public TestDAO() {
		setClass(Test.class);
	}
	
	public List<Test> findByTestCase(TestCase testCase) throws CTSPersistenceException {
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
	
	public Test findByTestId(UUID testId) throws CTSPersistenceException {
		if(testId == null)
			throw new IllegalArgumentException("test id is empty.");
		
		try {
			Query query = em.createQuery("from " + clazz.getName() + " t where t.testId=:testId");
			query.setParameter("testId", testId);
			return (Test)query.getSingleResult();
		} catch(NoResultException ex) {
			return null;
		} catch(NonUniqueResultException ex) {
			throw new CTSPersistenceException("Ambiguous number of test '" + testId + "'.");
		} catch(Exception ex) {
			throw new CTSPersistenceException("Unable to query test '" + testId + "'. ", ex);
		}
	}
	
}

package net.geant.nsi.contest.platform.persistence;

import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import net.geant.nsi.contest.platform.data.Project;
import net.geant.nsi.contest.platform.data.TestCase;
import net.geant.nsi.contest.platform.persistence.exceptions.CTSPersistenceException;

public class TestCaseDAO extends GenericDAO<TestCase> {
	public TestCaseDAO() {
		setClass(TestCase.class);
	}
	
	public TestCase findByTestCaseId(UUID testCaseId) throws CTSPersistenceException {
		if(testCaseId == null)
			throw new IllegalArgumentException("test case id is empty.");
		
		try {
			Query query = em.createQuery("from " + clazz.getName() + " tc where tc.testCaseId=:testCaseId");
			query.setParameter("testCaseId", testCaseId);
			return (TestCase)query.getSingleResult();
		} catch(NoResultException ex) {
			return null;
		} catch(NonUniqueResultException ex) {
			throw new CTSPersistenceException("Ambiguous number of test cases '" + testCaseId + "'.");
		} catch(Exception ex) {
			throw new CTSPersistenceException("Unable to query test case '" + testCaseId + "'. ", ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<TestCase> findByProject(Project project) throws CTSPersistenceException {
		if(project == null)
			throw new IllegalArgumentException("Project is null");
		
		try {
			Query query = em.createQuery("select tc from " + clazz.getName() + " tc join tc.project p where p=:project order by tc.createdAt desc");
			query.setParameter("project", project);
			return query.getResultList();
		} catch (Exception ex) {
			throw new CTSPersistenceException("Unable to get test cases for project " + project.getKey(), ex);
		}	
	}
	
}

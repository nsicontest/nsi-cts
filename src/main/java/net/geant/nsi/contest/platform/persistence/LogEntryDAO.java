package net.geant.nsi.contest.platform.persistence;

import java.util.List;

import javax.persistence.Query;

import net.geant.nsi.contest.platform.data.LogEntry;
import net.geant.nsi.contest.platform.data.Test;
import net.geant.nsi.contest.platform.persistence.exceptions.CTSPersistenceException;

public class LogEntryDAO extends GenericDAO<LogEntry> {
	public LogEntryDAO() {
		setClass(LogEntry.class);
	}
	
	public List<LogEntry> findByTest(Test test) throws CTSPersistenceException {
		if(test == null)
			throw new IllegalArgumentException("Test is null");
		
		try {
			Query query = em.createQuery("select e from " + clazz.getName() + " e join e.test t where t=:test");
			query.setParameter("test", test);
			return query.getResultList();
		} catch (Exception ex) {
			throw new CTSPersistenceException("Unable to get entries for for test " + test.getTestId(), ex);
		}
	}
}

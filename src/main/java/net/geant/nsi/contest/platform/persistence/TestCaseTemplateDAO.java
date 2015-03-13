package net.geant.nsi.contest.platform.persistence;

import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import net.geant.nsi.contest.platform.data.AgentType;
import net.geant.nsi.contest.platform.data.TestCaseTemplate;
import net.geant.nsi.contest.platform.persistence.exceptions.CTSPersistenceException;

public class TestCaseTemplateDAO extends GenericDAO<TestCaseTemplate> {
	
	public TestCaseTemplateDAO() {
		setClass(TestCaseTemplate.class);
	}
	
	public TestCaseTemplate findByTemplateId(UUID templateId) throws CTSPersistenceException {
		if(templateId == null)
			throw new IllegalArgumentException("Template id is empty.");
		
		try {
			Query query = em.createQuery("from " + clazz.getName() + " tmpl where tmpl.templateId=:templateId");
			query.setParameter("templateId", templateId);
			return (TestCaseTemplate)query.getSingleResult();
		} catch(NoResultException ex) {
			return null;
		} catch(NonUniqueResultException ex) {
			throw new CTSPersistenceException("Ambiguous number of templates '" + templateId + "'.");
		} catch(Exception ex) {
			throw new CTSPersistenceException("Unable to query template'" + templateId + "'. ", ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<TestCaseTemplate> findByType(AgentType type) throws CTSPersistenceException {
		if(type == null)
			throw new IllegalArgumentException("Type is null.");
		
		try {
			Query query = em.createQuery("from " + clazz.getName() + " tmpl where tmpl.type=:type");
			query.setParameter("type", type);
			return query.getResultList();
		} catch (Exception ex) {
			throw new CTSPersistenceException("Unable to get templates for " + type, ex);
		}	
	}
	
}

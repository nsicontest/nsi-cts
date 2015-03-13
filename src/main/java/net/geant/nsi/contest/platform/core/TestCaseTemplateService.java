package net.geant.nsi.contest.platform.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.data.AgentType;
import net.geant.nsi.contest.platform.data.TestCaseTemplate;
import net.geant.nsi.contest.platform.persistence.TestCaseTemplateDAO;
import net.geant.nsi.contest.platform.persistence.TestCaseTemplateRepository;
import net.geant.nsi.contest.platform.persistence.exceptions.CTSPersistenceException;
import net.geant.nsi.contest.platform.web.data.Alert;

public class TestCaseTemplateService {

	@Autowired
	TestCaseTemplateDAO templates;
	
//	@Autowired
//	TestCaseTemplateRepository templates;
	
	@Transactional(readOnly=true)
	public List<TestCaseTemplate> getAll() {
		return templates.findAll();
	}

	@Transactional(readOnly=true)
	public TestCaseTemplate findBy(UUID templateId) throws ResourceNotFoundException {
		TestCaseTemplate template = null;
		try {
			template = templates.findByTemplateId(templateId);
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Could not get template " + templateId + " due to some errors.", ex);
		}
		if(template == null)
			throw new ResourceNotFoundException("Template " + templateId + " not found.");
		return template;
	}
	
	@Transactional(readOnly=true)
	public List<TestCaseTemplate> findBy(List<UUID> templateIds) throws ResourceNotFoundException {
		List<TestCaseTemplate> selectedTemplates = new ArrayList<TestCaseTemplate>();
		
		for(UUID templateId : templateIds) {
			TestCaseTemplate template = null;
			template = findBy(templateId);
			selectedTemplates.add(template);
		}
		
		return selectedTemplates;
	}
	
	@Transactional(readOnly=true)
	public List<TestCaseTemplate> findBy(AgentType type) throws ResourceNotFoundException {
		try {
			return templates.findByType(type);
		} catch (CTSPersistenceException ex) {
			throw new ResourceNotFoundException("Unable to get templates for " + type, ex);
		}
	}
	
	@Transactional
	public TestCaseTemplate create(String name, AgentType type, boolean certification, String template) throws CTSException {
		try {
			TestCaseTemplate testCaseTemplate = new TestCaseTemplate(name, type, certification, template);
			templates.save(testCaseTemplate);
			return testCaseTemplate;
		} catch(Exception ex) {
			throw new CTSException("Could not create template.", ex);
		}
	}
	
	@Transactional
	public void update(TestCaseTemplate template) throws CTSException {
		try {
			if(templates.findByTemplateId(template.getTemplateId()) != null) {
				templates.save(template);
			} else 
				throw new ResourceNotFoundException("Could not update template as it does not exists.");
		} catch(Exception ex) {
			throw new CTSException("Could not update template.", ex);
		}
	}
	
	@Transactional(readOnly=true)
	public long count() {
		return templates.count();
	}
}

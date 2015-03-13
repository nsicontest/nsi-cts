package net.geant.nsi.contest.platform.web.api;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import net.geant.nsi.contest.platform.core.ProjectService;
import net.geant.nsi.contest.platform.core.TestCaseService;
import net.geant.nsi.contest.platform.core.TestCaseTemplateService;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.data.Project;
import net.geant.nsi.contest.platform.web.data.Test;
import net.geant.nsi.contest.platform.web.data.TestCase;
import net.geant.nsi.contest.platform.web.data.forms.NewCertificationForm;
import net.geant.nsi.contest.platform.web.data.forms.NewTestCaseForm;
import net.geant.nsi.contest.platform.web.exceptions.BadRequestException;
import net.geant.nsi.contest.platform.web.exceptions.UnsupportedAuthException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController("apiTestCasesController")
@PreAuthorize("hasPermission(#key, 'isAcceptedProjectMember')")
@RequestMapping(value="/api/projects/{key}/testcases", produces="application/json")
public class TestCasesController extends AbstractBaseRestController {
	private final static Logger log = Logger.getLogger(TestCasesController.class);
	
	@Autowired
	ProjectService projects;
	
	@Autowired
	TestCaseService testCases;
	
	@Autowired
	TestCaseTemplateService templates;
	
	@RequestMapping(method=RequestMethod.GET)
	@Transactional
	public List<TestCase> list(Principal principal, @PathVariable("key") UUID key) throws UnsupportedAuthException, ResourceNotFoundException {
		log.info("Get testcases for project " + key + " by user " + getCurrentUser(principal));
		
		net.geant.nsi.contest.platform.data.Project project = projects.findBy(key);
		return (List<TestCase>) converter.convert(testCases.getAll(project));
	}
	
	@RequestMapping(method=RequestMethod.POST, consumes="application/json")
	@Transactional
	public TestCase create(Principal principal, @PathVariable("key") UUID key, @RequestBody NewTestCaseForm newTestCase) throws BadRequestException, ResourceNotFoundException, UnsupportedAuthException {
		log.info("Create a new testcase for project " + key + " by user " + getCurrentUser(principal));
		
		if(newTestCase == null || newTestCase.getTemplateIds() == null || newTestCase.getTemplateIds().isEmpty())
			throw new BadRequestException("TestCase object is missing or contains empty templates.");
			
		net.geant.nsi.contest.platform.data.Project project = projects.findBy(key);
		List<net.geant.nsi.contest.platform.data.TestCaseTemplate> selectedTemplates = templates.findBy(newTestCase.getTemplateIds());
		net.geant.nsi.contest.platform.data.TestCase testCase = testCases.createFor(project, selectedTemplates);
		testCases.perform(testCase.getTestCaseId());
		
		log.info("New testcase " + testCase.getTestCaseId() + " for project " + key + " created by " + getCurrentUser(principal));
		
		return (TestCase) converter.convert(testCase);
	}
	
	@RequestMapping(value="/certificate", method=RequestMethod.POST, consumes="application/json")
	@Transactional
	public TestCase createCertification(Principal principal, @PathVariable("key") UUID key, @RequestBody NewCertificationForm newCertification) throws BadRequestException, ResourceNotFoundException, UnsupportedAuthException {
		log.info("Create a new testcase for project " + key + " by user " + getCurrentUser(principal));
		
		if(newCertification == null || newCertification.getType() == null)
			throw new BadRequestException("Certification object is missing or contains empty type.");
			
		net.geant.nsi.contest.platform.data.Project project = projects.findBy(key);
		net.geant.nsi.contest.platform.data.TestCase testCase = testCases.createCertificatedFor(project, newCertification.getType());
		testCases.perform(testCase.getTestCaseId());
		
		log.info("New certification ("+ newCertification.getType() + ") testcase " + testCase.getTestCaseId() + " for project " + key + " created by " + getCurrentUser(principal));
		
		return (TestCase) converter.convert(testCase);
	}
	
	
	@RequestMapping(value="/{testCaseId}", method=RequestMethod.GET)
	@Transactional
	public TestCase getTestCase(Principal principal, @PathVariable("key") UUID key, @PathVariable("testCaseId") UUID testCaseId) throws UnsupportedAuthException, ResourceNotFoundException, BadRequestException {
		log.info("Get testcase " + testCaseId + " for project " + key + " by user " + getCurrentUser(principal));
		net.geant.nsi.contest.platform.data.Project project = projects.findBy(key);
		
		
		//TODO: fix it
		//checkTestCaseInProject(testCaseId, project);
		
		net.geant.nsi.contest.platform.data.TestCase testCase = testCases.findBy(testCaseId);

		//if(project == null || !project.equals(testCase.getProject()))
		//	throw new BadRequestException("Project or test case are not correlated.");
		
		return (TestCase)converter.convert(testCase);
	}
	
	@RequestMapping(value="/{testCaseId}/results", method=RequestMethod.GET)
	@Transactional
	public TestCase showResults(Principal principal, @PathVariable("key") UUID key, @PathVariable("testCaseId") UUID testCaseId) throws UnsupportedAuthException, ResourceNotFoundException, BadRequestException {
		log.info("Get testcase results for " + testCaseId);
		
		TestCase testCase = getTestCase(principal, key, testCaseId);
		testCase.setTests((List<Test>) converter.convert(testCases.getTestsFor(testCaseId)));
		
		return testCase;
	}
	
	
	private boolean checkTestCaseInProject(UUID testCaseId, Project project) throws ResourceNotFoundException {
		if(project == null)
			throw new IllegalArgumentException("project is null");
		if(testCaseId == null)
			throw new IllegalArgumentException("testCaseId is null");
		

		return (project.getTestCase(testCaseId) != null);
		
	}
}

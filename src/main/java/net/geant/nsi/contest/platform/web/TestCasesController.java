package net.geant.nsi.contest.platform.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import net.geant.nsi.contest.platform.core.ProjectService;
import net.geant.nsi.contest.platform.core.TestCaseServiceImpl;
import net.geant.nsi.contest.platform.core.TestCaseService;
import net.geant.nsi.contest.platform.core.TestCaseTemplateService;
import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.data.Project;
import net.geant.nsi.contest.platform.data.ResultStatus;
import net.geant.nsi.contest.platform.data.Test;
import net.geant.nsi.contest.platform.data.TestCase;
import net.geant.nsi.contest.platform.data.TestCaseTemplate;
import net.geant.nsi.contest.platform.web.data.Alert;
import net.geant.nsi.contest.platform.web.data.Alert.AlertType;
import net.geant.nsi.contest.platform.web.data.convert.ConverterImpl;
import net.geant.nsi.contest.platform.web.data.forms.NewCertificationForm;
import net.geant.nsi.contest.platform.web.data.forms.NewTestCaseForm;
import net.geant.nsi.contest.platform.web.exceptions.RedirectException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.xml.transform.StringSource;

@Controller
@PreAuthorize("hasPermission(#key, 'isAcceptedProjectMember')")
@RequestMapping("/projects/{key}")
public class TestCasesController extends AbstractBaseController {
	private final static Logger log = Logger.getLogger(TestCasesController.class);
			
	@Autowired
	ProjectService projects;

	@Autowired
	TestCaseService testCases;
	
	@Autowired
	TestCaseTemplateService templates;	
	
	@Autowired
	ConverterImpl converter;
	
	@Autowired
	Jaxb2Marshaller oxm;
	
	@RequestMapping(value="/testcases", method=RequestMethod.GET)
	public String show(Model model, RedirectAttributes attr, @PathVariable UUID key) throws CTSException {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		Project project = getProject(key, alerts);

		model.addAttribute("project", converter.convert(project));
		model.addAttribute("testcases", converter.convert(testCases.getAll(project)));
		
		return "projects/testcases/testcases";
	}
	
	@RequestMapping(value="/testcases/new", method=RequestMethod.GET)
	public String create(Model model, RedirectAttributes attr, @PathVariable UUID key) {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		Project project = getProject(key, alerts);
		
		model.addAttribute("newTestCaseForm", new NewTestCaseForm());
		model.addAttribute("project", converter.convert(project));
		model.addAttribute("templates", converter.convert(templates.getAll()));
		
		return "projects/testcases/new";
	}
	
	@RequestMapping(value="/testcases/new", method=RequestMethod.POST)
	@Transactional
	public String create(Model model, RedirectAttributes attr, @PathVariable UUID key, @Valid NewTestCaseForm form, BindingResult bindingResults) {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		if(bindingResults.hasErrors()) {
			alerts.add(Alert.failure("Invalid new test case definition."));
			return "redirect:/projects/" + key;
		}
		
		Project project = getProject(key, alerts);
		
		//TODO: simplyfy with findBy(List)
		List<TestCaseTemplate> selectedTemplates = new ArrayList<TestCaseTemplate>();
		for(UUID templateId : form.getTemplateIds()) {
			TestCaseTemplate template = null;
			try {
				template = templates.findBy(templateId);
				selectedTemplates.add(template);
			} catch (ResourceNotFoundException ex) {
				log.warn("Selected template " + templateId + " is no longer available. Skipping it.", ex);
				alerts.add(Alert.warning("Template " + templateId + " is no longer available. Skipped."));
			}
		}
		
		TestCase testCase = null;
		try {
			testCase = testCases.createFor(project, selectedTemplates);
			testCases.perform(testCase.getTestCaseId());
		} catch (ResourceNotFoundException e) {
			alerts.add(new Alert(AlertType.failure, "Failure!", "Unable to find project " + key));
			return "redirect:/projects";			
		}
		
		alerts.add(new Alert(AlertType.success, "Success!", "Test case created."));
		return "redirect:/projects/" + key + "/testcases/" + testCase.getTestCaseId();
	}	
	
	@RequestMapping(value="/testcases/certification/new", method=RequestMethod.POST)
	@Transactional
	public String createCertification(Model model, RedirectAttributes attr, @PathVariable UUID key, @Valid NewCertificationForm form, BindingResult bindingResults) {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		if(bindingResults.hasErrors()) {
			alerts.add(Alert.failure("Invalid new test case definition."));
			return "redirect:/projects/" + key;
		}
		
		Project project = getProject(key, alerts);	
		
		TestCase testCase = null;
		try {
			testCase = testCases.createCertificatedFor(project, form.getType());
			testCases.perform(testCase.getTestCaseId());
		} catch (ResourceNotFoundException ex) {
			log.error("Unable to process certification test case.", ex);
			alerts.add(new Alert(AlertType.failure, "Failure!", "Unable to start certification test for project " + key));
			return "redirect:/projects/"+key;			
		}
		
		alerts.add(new Alert(AlertType.success, "Success!", "Certification test case for " + form.getType() + " has been created."));
		return "redirect:/projects/" + key + "/testcases/" + testCase.getTestCaseId();
	}
	
	
	@RequestMapping(value="/testcases/{testCaseId}", method=RequestMethod.GET)
	@Transactional
	public String show(Model model, RedirectAttributes attr, @PathVariable UUID key, @PathVariable UUID testCaseId ) {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		Project project = getProject(key, alerts);
		TestCase testCase = getTestCase(key, testCaseId, alerts);
			
		model.addAttribute("project", converter.convert(project));
		model.addAttribute("testCase", converter.convert(testCase));
		//model.addAttribute("tests", converter.convert(testCase.getTests()));
		return "projects/testcases/testcase";
	}
	
	@RequestMapping(value="/testcases/{testCaseId}/results", method=RequestMethod.GET)
	@Transactional
	public String showResults(Model model, @PathVariable UUID key, @PathVariable UUID testCaseId) {
		List<Alert> alerts = prepareAlerts(model, null);
		
		Project project = getProject(key, alerts);
		TestCase testCase = getTestCase(key, testCaseId, alerts);
			
		model.addAttribute("project", converter.convert(project));
		model.addAttribute("testCase", converter.convert(testCase));
		model.addAttribute("tests", converter.convert(testCase.getTests()));
		
		Map<String, Object> summary = new HashMap<String, Object>();
		model.addAttribute("summary", summary);
		
		summary.put("failed", testCase.countTests(ResultStatus.FAILED));
		summary.put("success", testCase.countTests(ResultStatus.PASSED));
		summary.put("warning", testCase.countTests(ResultStatus.PASSED_CONDITIONALLY));
		
		summary.put("total", testCase.getTests().size());
		summary.put("done", testCase.countTests(Test.Status.FINISHED));
		
		return "projects/testcases/results :: testcase";
	}
	
	/**
	 * 
	 * To be replaced
	 * 
	 * @param model
	 * @param attr
	 * @param key
	 * @param testcaseId
	 * @return
	 */
	@RequestMapping(value="/testcases/test")
	public String test(Model model, RedirectAttributes attr, @PathVariable UUID key) {
		return "projects/testcases/test";
	}
	
	@RequestMapping(value="/testcases/{testCaseId}/tests/{testId}", method=RequestMethod.GET)
	@Transactional
	public String showTest(Model model, RedirectAttributes attr, @PathVariable UUID key, @PathVariable UUID testCaseId, @PathVariable UUID testId) {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		Project project = getProject(key, alerts);		
		TestCase testCase = getTestCase(key, testCaseId, alerts);
		Test test = getTest(key, testCaseId, testId, alerts);
		
		model.addAttribute("project", converter.convert(project));
		model.addAttribute("testCase", converter.convert(testCase));
		model.addAttribute("test", converter.convert(test));
		model.addAttribute("testResults", (test.getResult() != null ? oxm.unmarshal(new StringSource(test.getResult())) : null));
		
		return "projects/testcases/test";
	}
	
	@RequestMapping(value="/testcases/{testCaseId}/tests/{testId}/results", method=RequestMethod.GET)
	@Transactional
	public String showTestResults(Model model, @PathVariable UUID key, @PathVariable UUID testCaseId, @PathVariable UUID testId) {
		List<Alert> alerts = prepareAlerts(model, null);
		
		Project project = getProject(key, alerts);
		TestCase testCase = getTestCase(key, testCaseId, alerts);
		Test test = getTest(key, testCaseId, testId, alerts);
		
		model.addAttribute("project", converter.convert(project));
		model.addAttribute("testCase", converter.convert(testCase));
		model.addAttribute("test", converter.convert(test));
		model.addAttribute("testResults", (test.getResult() != null ? oxm.unmarshal(new StringSource(test.getResult())) : null));		
		
		
		return "projects/testcases/results :: test";
	}
	
	protected Project getProject(UUID key, List<Alert> alerts) {
		try {
			return projects.findBy(key);
		} catch (ResourceNotFoundException ex) {
			alerts.add(new Alert(AlertType.failure, "Failure!", "Unable to find project " + key));
			throw new RedirectException("/projects", alerts);
		}
	}
	
	protected TestCase getTestCase(UUID projectKey, UUID testCaseId, List<Alert> alerts) {
		try {
			return testCases.findBy(testCaseId);
		} catch(Exception ex) {
			alerts.add(new Alert(AlertType.failure, "Failure!", "Unable to find test case " + testCaseId));
			throw new RedirectException("/projects/"+projectKey, alerts);
		}		
	}
	
	protected Test getTest(UUID projectKey, UUID testCaseId, UUID testId, List<Alert> alerts) {
		try {
			return testCases.findTest(testId);
		} catch (ResourceNotFoundException ex) {
			alerts.add(new Alert(AlertType.failure, "Failure!", "Unable to find test " + testId));
			throw new RedirectException("/projects/"+projectKey+"/testcases/"+testCaseId, alerts);	
		}		
	}
	
}

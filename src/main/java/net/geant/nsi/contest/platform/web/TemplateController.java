package net.geant.nsi.contest.platform.web;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import net.geant.nsi.contest.platform.core.TestCaseTemplateService;
import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.data.TestCaseTemplate;
import net.geant.nsi.contest.platform.web.data.Alert;
import net.geant.nsi.contest.platform.web.data.Alert.AlertType;
import net.geant.nsi.contest.platform.web.data.forms.NewTemplateForm;
import net.geant.nsi.contest.platform.web.data.forms.TemplateForm;
import net.geant.nsi.contest.platform.web.exceptions.RedirectException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/templates")
@PreAuthorize("isAuthenticated()")
public class TemplateController extends AbstractBaseController {
	private final static Logger log = Logger.getLogger(TemplateController.class);
			
	private final String basePath = "templates";
	private final String baseUrl = "/templates";
	
	@Autowired
	TestCaseTemplateService templates;
	
	@ModelAttribute("templates")
	public List<TemplateForm> getTemplates() {
		return (List<TemplateForm>) converter.convert(templates.getAll());
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String show(Model model) {
		return basePath + "/templates";
	}
	
	@RequestMapping(value="/{templateId}", method=RequestMethod.GET)
	public String showDetails(Model model, RedirectAttributes attr, @PathVariable UUID templateId) {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		TestCaseTemplate template = getTemplate(templateId, alerts);
	
		model.addAttribute("templateForm", converter.convertDetailed(template));
		return basePath + "/template";
	}

	@RequestMapping(value="/{templateId}", method=RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional
	public String save(Model model, RedirectAttributes attr, @PathVariable UUID templateId, @Valid TemplateForm templateForm, BindingResult bindingResults) {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		if(!templateId.equals(templateForm.getTemplateId()))
			bindingResults.reject("templateId");
		if(bindingResults.hasErrors()) {
			alerts.add(Alert.failure("Template contains some errors."));
			return basePath + "/template";
		}
		
		
		TestCaseTemplate template = getTemplate(templateId, alerts);
		template.setName(templateForm.getName());
		template.setType(templateForm.getType());
		template.setTemplate(templateForm.getTemplate());
		template.setCertification(templateForm.isCertification());
		
		try {
			templates.update(template);
		} catch(ResourceNotFoundException ex) { 
			log.error("Could not find template.", ex);
			alerts.add(Alert.failure("Template could not find template."));		
		} catch (CTSException ex) {
			log.error("Unable to update template.", ex);
			alerts.add(Alert.failure("Template could not be updated."));
		}
		
		return "redirect:" + baseUrl;
	}
	
	@RequestMapping(value="/new", method=RequestMethod.GET)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String create(Model model) {
		model.addAttribute("newTemplateForm", new NewTemplateForm());
		return basePath + "/new";
	}
	
	@RequestMapping(value="/new", method=RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional
	public String create(Model model, RedirectAttributes attr, @Valid NewTemplateForm form, BindingResult bindingResults) {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		if(bindingResults.hasErrors()) {
			alerts.add(Alert.failure("Template contains some errors."));
			return basePath + "/new";
		}
		
		try {
			templates.create(form.getName(), form.getType(), form.isCertification(), form.getTemplate());
			alerts.add(Alert.success("Template created."));
		} catch(CTSException ex) {
			log.error("Could not create template.", ex);
			alerts.add(Alert.failure("Template could not be created."));
		}
		
		return "redirect:" + baseUrl;
	}

	protected TestCaseTemplate getTemplate(UUID templateId, List<Alert> alerts) {
		try {
			return templates.findBy(templateId);
		} catch (ResourceNotFoundException ex) {
			alerts.add(new Alert(AlertType.failure, "Failure!", "Unable to find template " + templateId));
			throw new RedirectException("/templates", alerts);
		}
	}
	
}

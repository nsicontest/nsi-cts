package net.geant.nsi.contest.platform.web.api;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import net.geant.nsi.contest.platform.core.TestCaseTemplateService;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.web.data.forms.TemplateForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController("apiTemplateController")
@RequestMapping(value="/api/templates", produces="application/json")
public class TemplateController extends AbstractBaseRestController {
	
	@Autowired
	TestCaseTemplateService templates;
	
	@RequestMapping(method=RequestMethod.GET)
	@Transactional
	public List<TemplateForm> list(Principal principal) {
		return (List<TemplateForm>) converter.convert(templates.getAll());
	}
	
	
	@RequestMapping(value="/{templateId}", method=RequestMethod.GET)
	@Transactional
	public TemplateForm showTemplate(Principal principal, @PathVariable UUID templateId) throws ResourceNotFoundException {
		return (TemplateForm) converter.convertDetailed(templates.findBy(templateId));
	}
	
	
	
}

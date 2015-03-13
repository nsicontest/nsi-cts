package net.geant.nsi.contest.platform.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import net.geant.nsi.contest.platform.core.AdminService;
import net.geant.nsi.contest.platform.core.ProjectService;
import net.geant.nsi.contest.platform.core.UserService;
import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.data.Project;
import net.geant.nsi.contest.platform.web.data.Alert;
import net.geant.nsi.contest.platform.web.data.Alert.AlertType;
import net.geant.nsi.contest.platform.web.data.User;
import net.geant.nsi.contest.platform.web.data.UserData;
import net.geant.nsi.contest.platform.web.data.forms.LoginForm;
import net.geant.nsi.contest.platform.web.data.forms.ProfileForm;
import net.geant.nsi.contest.platform.web.data.forms.RegistrationForm;
import net.geant.nsi.contest.platform.web.data.forms.RegistrationForm.Action;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SiteController extends AbstractBaseController implements ResourceLoaderAware {
	private final static Logger log = Logger.getLogger(SiteController.class);
	
	ResourceLoader resourceLoader = null;
	
	@Autowired
	UserService userService;
	
	@Autowired
	ProjectService projectService;
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index(Model model, @ModelAttribute User user) {
		model.addAttribute(new RegistrationForm());
		
		if(user != null && user.getUserId() != null) {
			return (user.isAdmin() ? "redirect:/admin" : "redirect:/projects");
		}
			
		return "index";
	}
	
	@RequestMapping(value="/home", method=RequestMethod.GET) 
	public String home(Model model) {
		return "home";
	}
	
	@RequestMapping(value="/login", method={RequestMethod.POST, RequestMethod.GET}) 
	public String login(RedirectAttributes attr, @ModelAttribute UserData userData, @ModelAttribute User user) {
		List<Alert> alerts = prepareAlerts(null, attr);
		alerts.add(Alert.success("Login success", "You have been logged in!"));
		
		if(user != null && user.getUserId() != null) {
			return (user.isAdmin() ? "redirect:/admin" : "redirect:/projects");
		}
		
		return "redirect:/home";
	}
	
	@RequestMapping(value="/loginerror", method=RequestMethod.GET) 
	public String loginError(RedirectAttributes attr) {
		List<Alert> alerts = prepareAlerts(null, attr);
		alerts.add(Alert.failure("Login failure", "Unable to login!"));
		return "redirect:/";
	}	
	
	@RequestMapping(value="/logout", method={RequestMethod.GET, RequestMethod.POST})
	public String logout(Model model, HttpSession session) {
		session.invalidate();
		List<Alert> alerts = prepareAlerts(model, null);
		alerts.add(Alert.success("Logout success", "You have been logged out!"));
		return "index";
	}
	
	
	/**
	 * 
	 * Register a new user and project (optionally)
	 * 
	 * @param loginForm
	 * @param registrationForm
	 * @param bindingResult
	 * @param model
	 * @param attr
	 * @return
	 */
	@RequestMapping(value="/register", method=RequestMethod.POST)
	@Transactional
	public String registerNewUser(Model model, RedirectAttributes attr, LoginForm loginForm, @Valid RegistrationForm registrationForm, BindingResult bindingResult) {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		Action action = registrationForm.getAction();
		String email = registrationForm.getEmail();
		String username = registrationForm.getUsername();
		String password = registrationForm.getPassword();
		String project = registrationForm.getProject();
		
		// Extra validation
		if(action == null)
			bindingResult.rejectValue("action", null);
		else if(action != Action.NONE) {
			if(registrationForm.getProject() == null || registrationForm.getProject().trim().length() == 0)
				bindingResult.rejectValue("project", null);
		}
		if(bindingResult.hasErrors()) {
			alerts.add(Alert.failure("Registration failed", "There are invalid fields!"));			
			return "index";
		}
				
		// User registration
		try {
			userService.register(email, username, password);
			log.info("User '" + email + "' registered successfully.");
			alerts.add(Alert.success("Registration success", "You have been successfully registered!"));
		} catch (Exception ex) {
			log.error("Unable to register user '" + email +"'", ex);
			alerts.add(Alert.failure("Registration failed", "Unable to register a new user!"));
			return "index";
		}
		
		// Project creation or joining
		try {
			net.geant.nsi.contest.platform.data.User user = userService.findBy(email);
			
			if(action == Action.CREATE) {
				projectService.create(project, user);
				alerts.add(Alert.success("Project " + project + " has been successfully created."));
			} else if(action == Action.JOIN) {
				Project p=projectService.findBy(UUID.fromString(project));
				p.addUser(user);
				projectService.update(p);
				alerts.add(Alert.success("You are awaiting for joining the project " + project));
			}
		} catch(Exception ex) {
			log.error("Unable to create or join the project '" + project +"'", ex);
			alerts.add(Alert.failure("Unable to create or join to the project " + project + "!"));			
		}
		
		return "redirect:/";
	}
	
	/**
	 * Handles all documentation pages
	 *
	 */
	@RequestMapping(value="/docs/**", method=RequestMethod.GET)
	public String showDocumentation(Model model, HttpServletRequest request) {
		String subDocUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		log.debug("SubDocUrl: " + subDocUrl);
		
		if(subDocUrl == null || subDocUrl.equals("/docs") || subDocUrl.equals("docs"))
			return "docs/index";
		
		return "docs/"+subDocUrl;
	}
	
	@RequestMapping(value="/terms", method=RequestMethod.GET)
	public String showTerms(Model model) throws ResourceNotFoundException {		
		try {
			Resource termsRes = resourceLoader.getResource("classpath:terms.txt");
			String terms = FileUtils.readFileToString(termsRes.getFile(), "utf-8");
			model.addAttribute("terms", terms);
			return "terms";
		} catch (IOException ex) {
			log.error("Unable to load terms file", ex);
			throw new ResourceNotFoundException("Unable to load terms file.", ex);
		}
	}
	
	/**
	 * For handling custom error pages defined in web.xml file
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/error")
	public String error(Model model) {
		String code = "#" + System.currentTimeMillis();
		log.error("Unhandled exception for " + code);
		
		model.addAttribute("code", code);
		
		return "error/exception";
	}
	
	@RequestMapping(value="/error/403")
	public String customError(Model model) {
		log.warn("Access denied ");
		
		model.addAttribute("title", "Security issue.");
		model.addAttribute("message", "Access denied.");
		
		return "error/custom";
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}	
}

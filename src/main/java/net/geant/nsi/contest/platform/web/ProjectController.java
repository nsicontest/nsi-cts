package net.geant.nsi.contest.platform.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import net.geant.nsi.contest.platform.core.ProjectService;
import net.geant.nsi.contest.platform.core.TestCaseService;
import net.geant.nsi.contest.platform.core.TestCaseServiceImpl;
import net.geant.nsi.contest.platform.core.TestCaseTemplateService;
import net.geant.nsi.contest.platform.core.UserService;
import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.core.exceptions.UserNotFoundException;
import net.geant.nsi.contest.platform.data.UserAcl.Status;
import net.geant.nsi.contest.platform.web.data.Alert;
import net.geant.nsi.contest.platform.web.data.Alert.AlertType;
import net.geant.nsi.contest.platform.web.data.Project;
import net.geant.nsi.contest.platform.web.data.User;
import net.geant.nsi.contest.platform.web.data.UserAcl;
import net.geant.nsi.contest.platform.web.data.UserData;
import net.geant.nsi.contest.platform.web.data.forms.FileUploadForm;
import net.geant.nsi.contest.platform.web.data.forms.JoinProjectForm;
import net.geant.nsi.contest.platform.web.data.forms.NewCertificationForm;
import net.geant.nsi.contest.platform.web.data.forms.NewProjectForm;
import net.geant.nsi.contest.platform.web.data.forms.NewTestCaseForm;
import net.geant.nsi.contest.platform.web.data.forms.ProjectForm;
import net.geant.nsi.contest.platform.web.exceptions.RedirectException;
import net.geant.nsicontest.topology.Nsa;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/projects")
public class ProjectController extends AbstractBaseController {
	private final static Logger log = Logger.getLogger(ProjectController.class);
	
	@Autowired
	TestCaseService testcases;
	
	@Autowired
	UserService users;
	
	@Autowired
	ProjectService projects;
	
	@Autowired
	TestCaseTemplateService templates;
		
	@RequestMapping(method=RequestMethod.GET)
	public String showProjects(RedirectAttributes attr, Model model, @ModelAttribute User user) {		
		List<Alert> alerts = prepareAlerts(model, attr);
		
		try {
			model.addAttribute("projects", converter.convert(projects.getByUser(user.getUserId())));
		} catch (CTSException ex) {
			log.error("Unable to get projects for user " + user.getEmail(), ex);
			alerts.add(Alert.failure("Unable to find user projects."));
			return "redirect:/home";
		}
		
		return "projects/projects";
	}
	
	@RequestMapping(value="/{key}", method=RequestMethod.GET)
	@PreAuthorize("hasPermission(#key, 'isAcceptedProjectMember')")
	@Transactional
	public String showProject(RedirectAttributes attr, Model model, @ModelAttribute UserData userData, @PathVariable("key") UUID key) throws CTSException {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		net.geant.nsi.contest.platform.data.Project project = getProject(key, alerts);
		
		userData.setSelectedProject((Project) converter.convert(project));
		
		model.addAttribute("newTestCaseForm", new NewTestCaseForm());
		model.addAttribute("project", converter.convert(project));
		model.addAttribute("uploadTopologyForm", new FileUploadForm());
		model.addAttribute("templates", converter.convert(templates.getAll()));
		model.addAttribute("testcases", converter.convert(testcases.getAll(project)));
		model.addAttribute("newCertificationForm", new NewCertificationForm());
		
		model.addAttribute("topologyGenerated", existAgentTopologies(key));
		model.addAttribute("topologyUploaded", existUserAgentTopology(key));
		
		try {
			if(existUserAgentTopology(key) && existAgentTopologies(key)) 
				model.addAttribute("agents", getAllProjectAgents(key));
			else if(existAgentTopologies(key))
				model.addAttribute("agents", getProjectAgents(key));
		} catch(ResourceNotFoundException ex) {
			log.error("Unable to get agents.", ex);
			alerts.add(Alert.failure(ex.getMessage()));
		}
		
		return "projects/project";
	}
	
	@RequestMapping(value="/{key}/configuration", method=RequestMethod.GET)
	@PreAuthorize("hasPermission(#key, 'isAcceptedProjectMember')")
	@Transactional
	public String showProjectConfiguration(RedirectAttributes attr, Model model, @ModelAttribute UserData userData, @PathVariable("key") UUID key) {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		net.geant.nsi.contest.platform.data.Project project = getProject(key, alerts);
		
		//TODO: add converter
		userData.setSelectedProject((Project) converter.convert(project));
		model.addAttribute("projectForm", new ProjectForm(project.getKey(), project.getName(), project.getNetworkId()));
		model.addAttribute("uploadTopologyForm", new FileUploadForm());
		return "projects/projectConfiguration";
	}
	
	@RequestMapping(value="/{key}/configuration", method=RequestMethod.POST)
	@PreAuthorize("hasPermission(#key, 'isAcceptedProjectMember')")
	@Transactional
	public String updateProjectConfiguration(RedirectAttributes attr, Model model, @ModelAttribute User user, @ModelAttribute UserData userData, @PathVariable("key") UUID key, @Valid ProjectForm form, BindingResult bindingResults) {
		List<Alert> alerts = prepareAlerts(model, attr);

		if(bindingResults.hasErrors()) {
			alerts.add(Alert.failure("Project form has some errors."));
			return "projects/projectConfiguration";
		}
		
		net.geant.nsi.contest.platform.data.Project project = getProject(key, alerts);
		
		project.setName(form.getName());
		project.setNetworkId(form.getNetworkId());
//		project.setUrl(form.getUrl());
//		project.setType(form.getType());
		
		projects.update(project);
		userData.setSelectedProject((Project) converter.convert(project));
		
		alerts.add(Alert.success("Project has been successfully updated."));
		
		return "redirect:/projects/" + key;

	}
	
	@RequestMapping(value="/{key}", method=RequestMethod.DELETE)
	@PreAuthorize("hasPermission(#key, 'isAcceptedProjectMember')")
	@Transactional
	public String delete(RedirectAttributes attr, @ModelAttribute User user, @PathVariable("key") UUID key) throws UserNotFoundException {
		List<Alert> alerts = prepareAlerts(null, attr);
		
		net.geant.nsi.contest.platform.data.Project project = getProject(key, alerts);
		net.geant.nsi.contest.platform.data.User u = users.findBy(user.getUserId());

		boolean result = projects.delete(project, u);
		if(!result) 
			return "redirect:/error/403";
	
		alerts.add(Alert.success("Project " + key + " has been removed."));		
		return "redirect:/projects";
	}
	
	@RequestMapping(value="/new", method=RequestMethod.GET)
	public String create(Model model) {
		model.addAttribute("newProjectForm", new NewProjectForm());
		return "projects/new";
	}
	
	@RequestMapping(value="/new", method=RequestMethod.POST)
	@Transactional
	public String create(RedirectAttributes attr, Model model, @ModelAttribute User user, @Valid NewProjectForm form, BindingResult bindingResults) throws CTSException {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		if(bindingResults.hasErrors()) {
			alerts.add(Alert.failure("Project form has some errors."));
			return "projects/new";
		}
		
		try {
			net.geant.nsi.contest.platform.data.User owner = users.findBy(user.getUserId());
			
			net.geant.nsi.contest.platform.data.Project p = projects.create(form.getName(), owner);
			//p.setNetworkId(form.getNetworkId());
			//p.setUrl(form.getUrl());
			projects.update(p);
			return "redirect:/projects";
		} catch(Exception ex) {
			log.error("Problem when creating a new project " + form.getName(), ex);
			alerts.add(Alert.failure("Problem during creating project " + form.getName() + ". Please contact administrator."));
			return "projects/new";
		}
	}	
	
	@RequestMapping(value="/join", method=RequestMethod.GET)
	public String join(Model model) {
		model.addAttribute("joinProjectForm", new JoinProjectForm());
		model.addAttribute("allProjects", converter.convert(projects.getAll()));
		
		return "projects/join";
	}
	
	@RequestMapping(value="/join", method=RequestMethod.POST)
	@Transactional
	public String join(RedirectAttributes attr, Model model, @ModelAttribute User user, @Valid JoinProjectForm form, BindingResult bindingResults) {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		if(bindingResults.hasErrors()) {
			alerts.add(Alert.failure("Project form has some errors."));
			return "projects/join";
		}

		try {
			net.geant.nsi.contest.platform.data.Project project = getProject(form.getKey(), "/projects/join", alerts);
			
			boolean added = project.addUser(users.findBy(user.getUserId()));
			if(!added) {
				alerts.add(Alert.failure("User cannot be added to the project " + form.getKey()));
				return "redirect:/projects/join";				
			}
			projects.update(project);
		} catch(Exception ex) {
			log.error("Problem when joining to the project " + form.getKey() + " by " + user.getEmail(), ex);
			alerts.add(Alert.failure("Problem during joining project " + form.getKey() + ". Please contact administrator."));
			return "redirect:/projects/join";
		}
		
		alerts.add(Alert.success("You have been added to project " + form.getKey() +". Please wait for acceptance."));
		return "redirect:/projects";
	}
	
	@RequestMapping(value="/{key}/users", method=RequestMethod.GET)
	@PreAuthorize("hasPermission(#key, 'isAcceptedProjectMember')")
	@Transactional
	public String showUsers(RedirectAttributes attr, Model model, @ModelAttribute User user, @ModelAttribute UserData userData, @PathVariable("key") UUID key) {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		try {
			net.geant.nsi.contest.platform.data.Project project = getProject(key, alerts);
			List<net.geant.nsi.contest.platform.data.UserAcl> acls = projects.getUsersAcls(key);
			
			userData.setSelectedProject((Project) converter.convert(project));
			
			model.addAttribute("project", converter.convert(project));
			
			List<User> users = new ArrayList<User>();
			Map<UUID, UserAcl> usersAcl = new HashMap<UUID, UserAcl>();
			for(net.geant.nsi.contest.platform.data.UserAcl acl : acls) {
				users.add((User) converter.convert(acl.getUser()));
				usersAcl.put(acl.getUser().getUserId(), (UserAcl) converter.convert(acl));
			}
			model.addAttribute("users", users);
			model.addAttribute("acls", usersAcl);
			
			return "projects/users";
		} catch(Exception ex) {
			log.error("Unable to list project " + key + " users.", ex);
			alerts.add(Alert.failure("Unable to list project " + key + " users."));
			return "redirect:/projects";
		}
	}
	
	@RequestMapping(value="/{key}/users/{actionUserId}", params="action", method=RequestMethod.GET)
	@PreAuthorize("hasPermission(#key, 'isAcceptedProjectMember')")
	@Transactional
	public String userAction(RedirectAttributes attr, Model model, @ModelAttribute User user, @PathVariable("key") UUID key, @PathVariable("actionUserId") UUID actionUserId, @RequestParam("action") String action) {
		log.info("Perform action " + action + " on user " + actionUserId + " in project " + key);
		List<Alert> alerts = prepareAlerts(model, attr);			
		
		try {
			net.geant.nsi.contest.platform.data.User managedUser = users.findBy(actionUserId);
			net.geant.nsi.contest.platform.data.Project project = getProject(key, alerts);
			//net.geant.nsi.contest.platform.data.UserAcl acl = projects.getUserAcl(key, userId);
			net.geant.nsi.contest.platform.data.UserAcl acl = project.getUserAcl(managedUser);
			
			if("delete".equalsIgnoreCase(action)) {
				log.info("Removing user " + actionUserId + " from project " + key);
				project.removeUser(managedUser);
				projects.update(project);
				alerts.add(Alert.success("User " + managedUser.getEmail() + " removed from project."));
			} else if("accept".equalsIgnoreCase(action)) {
				log.info("Accepting user " + actionUserId + " from project " + key);
				acl.setStatus(Status.ACCEPTED);
				projects.update(project);
				alerts.add(Alert.success("User " + managedUser.getEmail() + " accepted for project."));
			} else {
				alerts.add(Alert.failure("Unknown action."));				
			}
		} catch(Exception ex) {
			log.error("Unable to perform action " + action + " on user " + actionUserId + " in project " + key, ex);
			alerts.add(Alert.failure("Unable to perform action " + action + " on user " + actionUserId + " in project " + key));
		}
		return "redirect:/projects/" + key + "/users";
	}
	
	protected net.geant.nsi.contest.platform.data.Project getProject(UUID key, List<Alert> alerts) {
		return getProject(key, "/projects", alerts);
	}

	protected net.geant.nsi.contest.platform.data.Project getProject(UUID key, String redirect, List<Alert> alerts) {
		try {
			return projects.findBy(key);
		} catch (ResourceNotFoundException ex) {
			alerts.add(new Alert(AlertType.failure, "Failure!", "Unable to find project " + key));
			throw new RedirectException(redirect, alerts);
		}		
	}
	
}

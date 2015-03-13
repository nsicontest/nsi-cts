package net.geant.nsi.contest.platform.web;

import java.util.List;
import java.util.UUID;

import net.geant.nsi.contest.platform.core.AdminService;
import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.core.exceptions.UserNotFoundException;
import net.geant.nsi.contest.platform.web.data.Alert;
import net.geant.nsi.contest.platform.web.data.Alert.AlertType;
import net.geant.nsi.contest.platform.web.data.Statistics;
import net.geant.nsi.contest.platform.web.data.User;
import net.geant.nsi.contest.platform.web.exceptions.RedirectException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController extends AbstractBaseController {
	private final static Logger log = Logger.getLogger(AdminController.class);
	
	@Autowired
	AdminService adminService;
	
	public AdminController() {
	
	}

	@RequestMapping(method=RequestMethod.GET)
	public String showAdmin(Model model) {
		prepareAlerts(model, null);
		
		Statistics stats = new Statistics();
		
		stats.setUsersCount(adminService.getUsers().count());
		stats.setProjectsCount(adminService.getProjects().count());
		stats.setTemplatesCount(adminService.getTemplates().count());
		
		model.addAttribute("stats", stats);
		
		return "admin/index";
	}
	
	@RequestMapping(value="/projects", method=RequestMethod.GET)
	public String showProjects(Model model) {
		model.addAttribute("projects", converter.convert(adminService.getProjects().getAll()));
		return "admin/projects";
	}
	
	@RequestMapping(value="/projects/{key}", method=RequestMethod.GET)
	public String showProject(RedirectAttributes attr, Model model, @ModelAttribute User user, @PathVariable("key") UUID key) {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		net.geant.nsi.contest.platform.data.Project project = getProject(key, alerts);
		
		return "admin/project";
	}
	
	@RequestMapping(value="/projects/{key}", method=RequestMethod.DELETE) 
	@Transactional
	public String removeProject(RedirectAttributes attr, @PathVariable("key") UUID key ){
		List<Alert> alerts = prepareAlerts(null, attr);
		
		
		net.geant.nsi.contest.platform.data.Project project = getProject(key, alerts);
		
		try {
			log.info("Removing project with key=" + key);
			adminService.getProjects().delete(key);
			alerts.add(Alert.success("Project " + key + " removed."));
		} catch (CTSException ex) {
			log.error("Unable to remove project with key=" + key, ex);
			alerts.add(Alert.failure("Project " + key + " cannot be removed."));
		}
		
		return "redirect:/admin/projects";
	}
	
	@RequestMapping(value="/users", method=RequestMethod.GET)
	public String showUsers(Model model) {
		model.addAttribute("users", converter.convert(adminService.getUsers().getAll()));
		return "admin/users";
	}
	
	@RequestMapping(value="/users/{removeUserId}", method=RequestMethod.DELETE) 
	@Transactional
	public String removeUser(RedirectAttributes attr, @PathVariable("removeUserId") UUID removeUserId ) {
		List<Alert> alerts = prepareAlerts(null, attr);
		
		net.geant.nsi.contest.platform.data.User user = null;
		try {
			user = adminService.getUsers().findBy(removeUserId);
			if(user == null)
				throw new UserNotFoundException("User " + removeUserId + " does not exists.");
		} catch (CTSException ex) {
			log.error("Unable to find user " + removeUserId, ex); 
			alerts.add(Alert.failure("User with userId " + removeUserId + " cannot be found."));
			return "redirect:/admin/users";
		}
		
		try {
			log.info("Removing user with userId=" + removeUserId);
			adminService.getUsers().delete(removeUserId);
			alerts.add(Alert.success("User " + user.getEmail() + " removed."));
		} catch (CTSException ex) {
			log.error("Unable to remove user with userId=" + removeUserId, ex);
			alerts.add(Alert.failure("User " + removeUserId + " cannot be removed."));
		}
		
		return "redirect:/admin/users";
	}

	protected net.geant.nsi.contest.platform.data.Project getProject(UUID key, List<Alert> alerts) {
		try {
			return adminService.getProjects().findBy(key);
		} catch (ResourceNotFoundException ex) {
			alerts.add(new Alert(AlertType.failure, "Failure!", "Unable to find project " + key));
			throw new RedirectException("/admin/projects", alerts);
		}
	}
}

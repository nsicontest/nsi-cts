package net.geant.nsi.contest.platform.web;

import java.util.ArrayList;
import java.util.List;

import net.geant.nsi.contest.platform.core.ProjectService;
import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.web.data.Project;
import net.geant.nsi.contest.platform.web.data.User;
import net.geant.nsi.contest.platform.web.data.UserData;
import net.geant.nsi.contest.platform.web.data.convert.Converter;
import net.geant.nsi.contest.platform.web.data.forms.LoginForm;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Provides common model attributes for web controllers.
 * 
 * @author mikus
 *
 */
@ControllerAdvice
public class CommonController {
	private final static Logger log = Logger.getLogger(CommonController.class);
	
	@Autowired
	ProjectService projectService;
	
	@Autowired
	UserData userData;
	
	@Autowired
	Converter converter;
	
	@ModelAttribute("loginForm")
	public LoginForm getLoginForm() {
		return new LoginForm();
	}
	
//	@ModelAttribute("user")
//	public User getUser() {
//		log.debug("Authentication: " + SecurityContextHolder.getContext().getAuthentication() + " Principal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//		return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//	}
	
	@ModelAttribute("user")
	public User getUser(Authentication auth) {
		//TODO: workaround for spring autowiring problem
		auth = SecurityContextHolder.getContext().getAuthentication();
		
		log.debug("Authentication: " + auth + " Principal: " + (auth != null ? auth.getPrincipal() : "null"));
		return (User)(auth != null && (auth.getPrincipal() instanceof User) ? auth.getPrincipal() : null);		
	}
	
	@ModelAttribute("userProjects")
	public List<Project> getUserProjects(@ModelAttribute User user) {
		List<Project> userProjects = null;
		if(user != null && user.getUserId() != null) {
			try {
				userProjects = (List<Project>) converter.convert(projectService.getByUser(user.getUserId()));
			} catch (CTSException ex) {
				log.error("Unable to get user projects " + user, ex);
			}
		}
		return userProjects;
	}
	
	@ModelAttribute("userData")
	public UserData getUserData() {
		return userData;
	}
}

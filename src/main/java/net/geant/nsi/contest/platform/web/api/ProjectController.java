package net.geant.nsi.contest.platform.web.api;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import net.geant.nsi.contest.platform.core.ProjectService;
import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.web.data.Project;
import net.geant.nsi.contest.platform.web.data.User;
import net.geant.nsi.contest.platform.web.data.convert.Converter;
import net.geant.nsi.contest.platform.web.exceptions.UnsupportedAuthException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("apiProjectController")
@RequestMapping(value="/api/projects", produces="application/json")
public class ProjectController extends AbstractBaseRestController {

	private final static Logger log = Logger.getLogger(ProjectController.class);
	
	@Autowired
	ProjectService projects;
	
	@RequestMapping(method=RequestMethod.GET)
	@Transactional
	public List<Project> list(Principal principal) throws UnsupportedAuthException, ResourceNotFoundException {
		User user = getCurrentUser(principal);
		
		log.info("Get projects for user " + user);
		
		return (List<Project>) converter.convert(projects.getByUser(user.getUserId()));
	}
	
	@RequestMapping(value="/{key}", method=RequestMethod.GET)
	@Transactional
	public Project showProject(Principal principal, @PathVariable("key") UUID key) throws UnsupportedAuthException, ResourceNotFoundException {
		User user = getCurrentUser(principal);
		log.info("Get project " + key + " for uesr " + user);
		
		net.geant.nsi.contest.platform.data.Project project;
		
		try {
			project = projects.findBy(key);
			return (Project) converter.convert(project);
		} catch (ResourceNotFoundException ex) {
			log.error("Unable to get project " + key + " for user " + user, ex);
			throw ex;
		}
	}

	
	
}

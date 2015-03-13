package net.geant.nsi.contest.platform.core;

import java.util.List;
import java.util.UUID;

import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.data.Project;
import net.geant.nsi.contest.platform.data.User;
import net.geant.nsi.contest.platform.data.UserAcl;
import net.geant.nsi.contest.platform.data.UserAcl.Status;
import net.geant.nsi.contest.platform.persistence.ProjectDAO;
import net.geant.nsi.contest.platform.persistence.ProjectRepository;
import net.geant.nsi.contest.platform.persistence.exceptions.CTSPersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {
	@Autowired
	private ProjectDAO projects;
	
	@Autowired
	private UserService users;
	
//	@Autowired
//	private ProjectRepository projects;
	
	public ProjectService() {
	
	}	
	
	public Project create(String name) {
		return create(name, null);
	}
	
	@Transactional
	public Project create(String name, User owner) {
		Project project = new Project(name);
		if(owner != null)
			project.addUser(owner, Status.ACCEPTED);
		
		return projects.save(project);
	}
	
	@Transactional
	public void delete(UUID key) throws CTSException {
		Project project = projects.findByKey(key);
		if(project != null)
			projects.delete(project.getId());
		else
			throw new ResourceNotFoundException("Unable to find project " + key);
	}

	@Transactional
	public void delete(Project project) {
		if(project == null)
			throw new IllegalArgumentException("Project is null");
		projects.delete(project.getId());
	}

	/**
	 * 
	 * Checks acl if user is a full member
	 * 
	 * Returns true if user is allowed to delete; false in other case
	 * 
	 * @param project
	 * @param byMember
	 * @throws CTSException 
	 */	
	@Transactional
	public boolean delete(Project project, User byMember) {
		UserAcl acl = project.getUserAcl(byMember);
		if(acl == null || acl.getStatus() != Status.ACCEPTED)
			return false;
		delete(project);
		return true;
	}
	
	@Transactional(readOnly=true)
	public Project findBy(UUID key) throws ResourceNotFoundException {
		Project project = null;
		try {
			project = projects.findByKey(key);
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Problem with finding project " + key + " due to some errors.", ex);
		}
		if(project == null)
			throw new ResourceNotFoundException("Unable to find project " + key);
		return project;
	}
	
	@Transactional(readOnly=true)
	public List<Project> getByUser(UUID userId) throws ResourceNotFoundException {
		try {
			return projects.getByUser(userId);
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Unable to get user projects.", ex);
		}
	}
	
	@Transactional(readOnly=true)
	public UserAcl getUserAcl(UUID key, UUID userId) throws ResourceNotFoundException {
		try {
			return projects.getUserAcl(key, userId);
		} catch(Exception ex) {
			throw new ResourceNotFoundException("Unable to get user " + userId + " ACL for project " + key, ex);
		}
	}
	
	@Transactional(readOnly=true)
	public List<UserAcl> getUsersAcls(UUID key) throws ResourceNotFoundException {
		try {
			return projects.getUserAcls(key);
		} catch(Exception ex) {
			throw new ResourceNotFoundException("Unable to get users ACLs for project " + key, ex);
		}
	}
	
	@Transactional
	public void deleteUserFromAll(UUID userId) throws ResourceNotFoundException {
		try {
			User user = users.findBy(userId);
			
			List<Project> userProjects = this.getByUser(userId);
			for(Project p : userProjects) {
				p.removeUser(user);
				projects.save(p);
			}
			
		} catch(Exception ex) {
			throw new ResourceNotFoundException("Unable to remove user " + userId + " from all its projects.", ex);
		}
	}
	
	@Transactional
	public void update(Project project) {
		projects.save(project);
	}
	
	@Transactional(readOnly=true)
	public List<Project> getAll() {
		return projects.findAll();
	}
	
	@Transactional(readOnly=true)
	public long count() {
		return projects.count();
	}
}

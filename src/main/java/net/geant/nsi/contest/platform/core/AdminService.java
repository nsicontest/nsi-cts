package net.geant.nsi.contest.platform.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.geant.nsi.contest.platform.core.exceptions.AuthException;
import net.geant.nsi.contest.platform.core.exceptions.BadCredentialsException;
import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.core.exceptions.UserExistsException;
import net.geant.nsi.contest.platform.core.exceptions.UserNotFoundException;
import net.geant.nsi.contest.platform.data.Project;
import net.geant.nsi.contest.platform.data.User;
import net.geant.nsi.contest.platform.data.UserAcl;
import net.geant.nsi.contest.platform.data.UserAcl.Status;
import net.geant.nsi.contest.platform.data.UserRole;
import net.geant.nsi.contest.platform.persistence.ProjectDAO;
import net.geant.nsi.contest.platform.persistence.RoleDAO;
import net.geant.nsi.contest.platform.persistence.UserDAO;
import net.geant.nsi.contest.platform.persistence.exceptions.CTSPersistenceException;

@Service
public class AdminService {
	@Autowired
	private ProjectService projects;
	
	@Autowired
	private UserService users;

	@Autowired
	private TestCaseTemplateService templates;
	
	public ProjectService getProjects() {
		return projects;
	}

	public UserService getUsers() {
		return users;
	}
	
	public TestCaseTemplateService getTemplates() {
		return templates;
	}
}

package net.geant.nsi.contest.platform.core;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.geant.nsi.contest.platform.core.exceptions.AuthException;
import net.geant.nsi.contest.platform.core.exceptions.BadCredentialsException;
import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.core.exceptions.UserExistsException;
import net.geant.nsi.contest.platform.core.exceptions.UserNotFoundException;
import net.geant.nsi.contest.platform.data.User;
import net.geant.nsi.contest.platform.data.UserRole;
import net.geant.nsi.contest.platform.persistence.RoleDAO;
import net.geant.nsi.contest.platform.persistence.UserDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	@Autowired
	private UserDAO users;
	
	@Autowired
	private RoleDAO roles;
	
	@Autowired
	private ProjectService projects;
	
//	@Autowired
//	private UserRepository users;
//	
//	@Autowired
//	private RoleRepository roles;
	
	public UserService() {
	}

	private String userDefaultRole = null;
	
	@Transactional(readOnly=true)
	public User authenticate(String email, String password) throws CTSException {
		User user = null;
		
		try {
			user = users.findByEmail(email);
			if(user == null)
				throw new UserNotFoundException("No such user represented by " + email);
		} catch(Exception ex) {
			throw new AuthException("Unable to authenticate user. Reason: " + ex.getMessage(), ex);
		}
				
		if(password == null)
			throw new BadCredentialsException("Password is empty.");

		if(!password.equals(user.getPassword()))
			throw new BadCredentialsException("Invalid password.");
		
		return user;
	}
	
	public boolean register(String email, String username, String password) throws Exception {
		return register(email, username, password, new String[] {userDefaultRole});
	}
	
	@Transactional
	public boolean register(String email, String username, String password, String[] roleNames) throws CTSException {
		User user = users.findByEmail(email);
		if(user != null) 
			throw new UserExistsException("User already exists");
		
		user = new User(email, username, password);
		
		Set<UserRole> assignedRoles = user.getRoles();//new HashSet<UserRole>();
		for(String roleName : roleNames) {
			UserRole role = roles.findByName(roleName);
			assignedRoles.add(role);
		}
		//user.setRoles(assignedRoles);
		
		users.save(user);
		
		return true;
	}
	
	@Transactional(readOnly=true)
	public List<User> getAll() {
		return users.findAll();
	}
	
	@Transactional
	public User findBy(UUID userId) throws UserNotFoundException {
		User user = null;
		try {
			user = users.findByUserId(userId);
		} catch(Exception ex) {
			throw new UserNotFoundException("Unable to find user " + userId + " due to some errors. ", ex);
		}
		if(user == null)
			throw new UserNotFoundException("Unable to find user " + userId);
		return user;
	}

	@Transactional(readOnly=true)
	public User findBy(String email) throws UserNotFoundException {
		try {
			return users.findByEmail(email);
		} catch (Exception ex) {
			throw new UserNotFoundException("Unable to find user by email=" + email, ex);
		}
	}

	@Transactional
	public void update(User user) throws CTSException {
		if(user != null) {
			try {
				if(users.findByUserId(user.getUserId()) == null)
					throw new UserNotFoundException("Unable to find user " + user.getUserId());
				users.save(user);
			} catch (Exception ex) {
				throw new CTSException("Unable to store user " + user.getUserId(), ex);
			}
		}
	}

	@Transactional
	public void delete(User user) {
		if(user != null)
			users.delete(user.getId());
	}
	
	@Transactional
	public void delete(UUID userId) throws CTSException {
		User user = users.findByUserId(userId);
		if(user != null) {
			projects.deleteUserFromAll(userId);
			users.delete(user.getId());
		} else
			throw new ResourceNotFoundException("Unable to find user with userId="+userId);
	}
	
	@Transactional
	public void storeRoles(List<UserRole> roles) {
		for(UserRole role : roles) {
			this.roles.save(role);
		}
	}
	
	@Transactional(readOnly=true)
	public long count() {
		return users.count();
	}
	
	@Transactional(readOnly=true)
	public List<UserRole> getRoles() {
		return roles.findAll();
	}

	
	public String getUserDefaultRole() {
		return userDefaultRole;
	}

	public void setUserDefaultRole(String userDefaultRole) {
		this.userDefaultRole = userDefaultRole;
	}
}

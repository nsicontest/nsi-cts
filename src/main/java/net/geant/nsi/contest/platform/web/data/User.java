package net.geant.nsi.contest.platform.web.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

public class User {
	UUID userId;
	String email;
	String username;
	Set<Role> roles = new HashSet<Role>();

	protected User() {
	}
	
	public User(UUID userId, String email, String username) {
		this.userId = userId;
		this.email = email;
		this.username = username;
	}
	
	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		if(username != null && username.trim().length() > 0) return username;
		return email;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public boolean isAdmin() {
		for(Role role : roles)
			if(role.isAdmin())
				return true;
		return false;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", email=" + email + "]";
	}
}

package net.geant.nsi.contest.platform.web.data.forms;

import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.geant.nsi.contest.platform.validation.FieldMatch;

@FieldMatch(field="confirmPassword", matchWith="password")
public class ProfileForm {
	
	@NotNull
	UUID userId;
	
	@NotNull
	String email;
	
	@Size(max=30)
	String username;
	
	@Size(max=30)
	String password;
	
	@Size(max=30)
	String confirmPassword;
	
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	
}

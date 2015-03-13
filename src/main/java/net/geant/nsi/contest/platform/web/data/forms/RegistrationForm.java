package net.geant.nsi.contest.platform.web.data.forms;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.geant.nsi.contest.platform.validation.FieldMatch;

@FieldMatch(field="passwordConfirm", matchWith="password")
public class RegistrationForm {

	public enum Action {
		NONE("none"),
		CREATE("create"),
		JOIN("join");
		
		private String value;
		
		Action(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
	
	@NotNull
	private String email;
	private String username;
	
	@NotNull
	@Size(min=6)
	private String password;
	
	@NotNull
	@Size(min=6)
	private String passwordConfirm;
	
	@NotNull
	private Action action = Action.NONE;
	
	private String project;
	
	@AssertTrue
	private boolean termsAgreed;
	
	public RegistrationForm() {
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

	

	public Action getAction() {
		return action;
	}


	public void setAction(Action action) {
		this.action = action;
	}


	public String getProject() {
		return project;
	}


	public void setProject(String project) {
		this.project = project;
	}


	public String getPasswordConfirm() {
		return passwordConfirm;
	}


	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}


	public boolean isTermsAgreed() {
		return termsAgreed;
	}


	public void setTermsAgreed(boolean termsAgreed) {
		this.termsAgreed = termsAgreed;
	}

	
}

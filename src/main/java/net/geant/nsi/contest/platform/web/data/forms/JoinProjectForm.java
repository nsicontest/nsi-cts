package net.geant.nsi.contest.platform.web.data.forms;

import java.util.UUID;

import javax.validation.constraints.NotNull;

public class JoinProjectForm {
	@NotNull
	UUID key;
	
	public JoinProjectForm() {
		super();
	}

	public JoinProjectForm(UUID key) {
		this();
		this.key = key;
	}

	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}
}

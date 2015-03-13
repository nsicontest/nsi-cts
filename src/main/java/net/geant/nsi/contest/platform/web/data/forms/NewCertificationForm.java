package net.geant.nsi.contest.platform.web.data.forms;

import javax.validation.constraints.NotNull;

import net.geant.nsi.contest.platform.data.AgentType;

public class NewCertificationForm {
	@NotNull
	AgentType type;

	public NewCertificationForm() {
		super();
	}
	
	public NewCertificationForm(AgentType type) {
		super();
		this.type = type;
	}

	public final AgentType getType() {
		return type;
	}

	public final void setType(AgentType type) {
		this.type = type;
	}
	
	
}

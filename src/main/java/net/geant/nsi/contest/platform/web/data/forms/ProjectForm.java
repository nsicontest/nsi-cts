package net.geant.nsi.contest.platform.web.data.forms;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import net.geant.nsi.contest.platform.data.AgentType;

public class ProjectForm extends NewProjectForm {
	
	@NotNull
	private UUID key;
	
	@NotNull
	private String networkId;
	
//	@NotNull
//	private AgentType type;
	
	public ProjectForm() {
		
	}
	
	public ProjectForm(UUID key, String name, String networkId) {
		super(name);
		this.key = key;
		this.networkId = networkId;
	}
	
	
//	public ProjectForm(UUID key, String name, String url) {
//		super(name, url);
//		this.key = key;
//	}

	
	
//	public ProjectForm(UUID key, String name, String url, AgentType type) {
//		this(key, name, url);
//		this.type = type;
//	}
	
	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public final String getNetworkId() {
		return networkId;
	}

	public final void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

//	public AgentType getType() {
//		return type;
//	}
//
//	public void setType(AgentType type) {
//		this.type = type;
//	}
}

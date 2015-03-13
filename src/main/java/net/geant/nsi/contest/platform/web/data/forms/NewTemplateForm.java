package net.geant.nsi.contest.platform.web.data.forms;

import javax.validation.constraints.NotNull;

import net.geant.nsi.contest.platform.data.AgentType;

public class NewTemplateForm {
	@NotNull
	String name;
	
	String template;

	@NotNull
	AgentType type;
	
	boolean certification;
	
	public NewTemplateForm() {
	}
	
	public NewTemplateForm(String name, AgentType type, boolean certification) {
		this.type = type;
		this.name = name;
		this.certification = certification;
	}
	
	public NewTemplateForm(String name, AgentType type, boolean certification, String template) {
		this(name, type, certification);
		this.template = template;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public final AgentType getType() {
		return type;
	}

	public final void setType(AgentType type) {
		this.type = type;
	}

	public final boolean isCertification() {
		return certification;
	}

	public final void setCertification(boolean certification) {
		this.certification = certification;
	}
	
	
}

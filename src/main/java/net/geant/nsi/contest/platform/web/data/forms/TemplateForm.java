package net.geant.nsi.contest.platform.web.data.forms;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import net.geant.nsi.contest.platform.data.AgentType;

public class TemplateForm extends NewTemplateForm {

	@NotNull
	UUID templateId;
	
	protected TemplateForm() {
		super();
	}
	
	public TemplateForm(UUID templateId, String name, AgentType type, boolean certification) {
		super(name, type, certification);
		this.templateId = templateId;
	}
		
	public TemplateForm(UUID templateId, String name, AgentType type, boolean certification, String template) {
		super(name, type, certification, template);
		this.templateId = templateId;
	}
	
	public UUID getTemplateId() {
		return templateId;
	}

	public void setTemplateId(UUID templateId) {
		this.templateId = templateId;
	}
	
}

package net.geant.nsi.contest.platform.web.data.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

public class NewTestCaseForm {
	@NotNull
	List<UUID> templateIds = new ArrayList<UUID>();
	
	
	public NewTestCaseForm() {
	}
	
	public NewTestCaseForm(List<UUID> templateIds) {
		this.templateIds = templateIds;
	}

	public List<UUID> getTemplateIds() {
		return templateIds;
	}

	public void setTemplateIds(List<UUID> templateIds) {
		this.templateIds = templateIds;
	}
	
}

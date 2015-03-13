package net.geant.nsi.contest.platform.web.data.forms;

import javax.validation.constraints.NotNull;

public class NewProjectForm {
	@NotNull
	protected String name;
	
//	protected String url;

	public NewProjectForm() {
	}
	
	public NewProjectForm(String name) {
		this.name = name;
	}
	
//	public NewProjectForm(String name, String url) {
//		this(name);
//		this.url = url;
//	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

//	public String getUrl() {
//		return url;
//	}
//
//	public void setUrl(String url) {
//		this.url = url;
//	}
}

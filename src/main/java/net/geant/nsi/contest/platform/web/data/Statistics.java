package net.geant.nsi.contest.platform.web.data;

public class Statistics {

	private long usersCount;
	private long projectsCount;
	private long templatesCount;
	
	public Statistics() {
	}

	public long getUsersCount() {
		return usersCount;
	}

	public void setUsersCount(long usersCount) {
		this.usersCount = usersCount;
	}

	public long getProjectsCount() {
		return projectsCount;
	}

	public void setProjectsCount(long projectsCount) {
		this.projectsCount = projectsCount;
	}

	public long getTemplatesCount() {
		return templatesCount;
	}

	public void setTemplatesCount(long templatesCount) {
		this.templatesCount = templatesCount;
	}
	
}

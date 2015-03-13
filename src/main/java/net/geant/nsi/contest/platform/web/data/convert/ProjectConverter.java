package net.geant.nsi.contest.platform.web.data.convert;

import net.geant.nsi.contest.platform.web.data.Project;


public class ProjectConverter extends AbstractConvertType<net.geant.nsi.contest.platform.data.Project, Project> {

	public ProjectConverter(Class<net.geant.nsi.contest.platform.data.Project> type) {
		super(type);
	}

	@Override
	public Project convert(net.geant.nsi.contest.platform.data.Project project) {
		Project p = new Project(project.getKey(), project.getName());
		p.setNetworkId(project.getNetworkId());
		p.setUsersCount(project.getUsersCount());
//		p.setType(project.getType());
//		p.setUrl(project.getUrl());
		return p;
	}

	@Override
	public Project convertDetailed(
			net.geant.nsi.contest.platform.data.Project project) {
		return convert(project);
	}

}

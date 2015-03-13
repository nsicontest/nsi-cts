package net.geant.nsi.contest.platform.auth.permissions;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import net.geant.nsi.contest.platform.auth.PermissionException;
import net.geant.nsi.contest.platform.core.ProjectService;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.data.Project;
import net.geant.nsi.contest.platform.web.data.User;

public class IsAcceptedProjectMemberPermission extends CTSAbstractPermission {
	private final static Logger log = Logger.getLogger(IsAcceptedProjectMemberPermission.class);
	
	@Autowired
	ProjectService projects;

	@Override
	protected boolean checkTargetParam(Object targetDomainObject) {
		if(targetDomainObject == null || !(targetDomainObject instanceof UUID))
			throw new PermissionException("Invalid target object " + targetDomainObject + ". Expected UUID type");
		
		return true;
	}

	@Override
	protected boolean isAllowedInternal(Authentication authentication, Object targetDomainObject) throws Exception {
		log.debug("Checking auth=" + authentication + " on object " + targetDomainObject);
		User user = getUser(authentication);
		Project project = projects.findBy((UUID) targetDomainObject);
		log.debug("Auth user " + user + " on project " + project + " hasAccess=" + project.hasUserAccess(user.getUserId()));
		return project.hasUserAccess(user.getUserId());
	}
}

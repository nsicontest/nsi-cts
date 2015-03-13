package net.geant.nsi.contest.platform.auth.permissions;

import net.geant.nsi.contest.platform.web.data.User;

import org.springframework.security.core.Authentication;

public class IsAdminPermission extends CTSAbstractPermission {
	
	@Override
	protected boolean checkTargetParam(Object targetDomainObject) {
		return true;
	}

	@Override
	protected boolean isAllowedInternal(Authentication authentication,
			Object targetDomainObject) throws Exception {
		User user = getUser(authentication);
		return user.isAdmin();
	}

}

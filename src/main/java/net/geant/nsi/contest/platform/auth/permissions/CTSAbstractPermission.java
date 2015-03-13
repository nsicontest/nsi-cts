package net.geant.nsi.contest.platform.auth.permissions;

import org.springframework.security.core.Authentication;

import net.geant.nsi.contest.platform.auth.CTSPermission;
import net.geant.nsi.contest.platform.auth.PermissionException;
import net.geant.nsi.contest.platform.web.data.User;

public abstract class CTSAbstractPermission implements CTSPermission {

	
	
	@Override
	public boolean isAllowed(Authentication authentication,
			Object targetDomainObject) {
		checkUserParam(authentication);
		checkTargetParam(targetDomainObject);
		try {
			return isAllowedInternal(authentication, targetDomainObject);
		} catch(Exception ex) {
			throw new PermissionException("Permission processing problem.", ex);
		}
	}

	protected abstract boolean isAllowedInternal(Authentication authentication, Object targetDomainObject) throws Exception;
	
	protected boolean checkUserParam(Authentication authentication) {
		if(authentication == null || !(authentication.getPrincipal() instanceof User))
			throw new PermissionException("Invalid authentication object content");
		
		return true;
	}
	
	protected abstract boolean checkTargetParam(Object targetDomainObject);
	
	protected User getUser(Authentication authentication) {
		return (User)authentication.getPrincipal();
	}
}

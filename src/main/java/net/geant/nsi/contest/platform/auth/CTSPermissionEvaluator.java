package net.geant.nsi.contest.platform.auth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

public class CTSPermissionEvaluator implements PermissionEvaluator {

	Map<String, CTSPermission> permissions = new HashMap<String, CTSPermission>();
	
	public CTSPermissionEvaluator() {	
	}
	
	public CTSPermissionEvaluator(Map<String, CTSPermission> permissions) {
		if(permissions == null)
			throw new IllegalArgumentException("Permission map is null");
		
		this.permissions = permissions;
	}
	
	@Override
	public boolean hasPermission(Authentication authentication,
			Object targetDomainObject, Object permission) {
		if(checkParams(authentication, targetDomainObject, permission))
			return checkPermission(authentication, targetDomainObject, permission);
		return false;
	}

	private boolean checkPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		CTSPermission perm = permissions.get((String)permission);
		if(perm == null)
			throw new PermissionNotDefinedException("No permission " + permission + " is defined.");
		
		return perm.isAllowed(authentication, targetDomainObject);
	}
	
	private boolean checkParams(Authentication authentication, Object targetDomainObject, Object permission) {
		return (authentication != null && targetDomainObject != null && permission instanceof String);
	}
	
	@Override
	public boolean hasPermission(Authentication authentication,
			Serializable targetId, String targetType, Object permission) {
		throw new PermissionNotDefinedException("Not supported permission method");
	}

	public final Map<String, CTSPermission> getPermissions() {
		return permissions;
	}

}

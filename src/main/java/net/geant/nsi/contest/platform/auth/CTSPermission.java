package net.geant.nsi.contest.platform.auth;

import org.springframework.security.core.Authentication;

public interface CTSPermission {
	boolean isAllowed(Authentication authentication, Object targetDomainObject);
}

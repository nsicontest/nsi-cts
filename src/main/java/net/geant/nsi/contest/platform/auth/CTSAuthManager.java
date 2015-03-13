package net.geant.nsi.contest.platform.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


/**
 * Required for handling @PreAuthenticate annotations with 'hasRole()' expressions
 * 
 * @author mikus
 *
 */
public class CTSAuthManager implements AuthenticationManager {
	@Autowired
	private CTSAuthProvider authProvider;

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		return authProvider.authenticate(authentication);
	}
}

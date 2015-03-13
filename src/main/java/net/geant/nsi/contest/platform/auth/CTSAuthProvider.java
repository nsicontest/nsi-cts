package net.geant.nsi.contest.platform.auth;

import java.util.ArrayList;
import java.util.List;

import net.geant.nsi.contest.platform.core.UserService;
import net.geant.nsi.contest.platform.core.exceptions.AuthException;
import net.geant.nsi.contest.platform.core.exceptions.UserNotFoundException;
import net.geant.nsi.contest.platform.data.User;
import net.geant.nsi.contest.platform.data.UserRole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component("authenticationProvider")
public class CTSAuthProvider implements AuthenticationProvider {

	private final static Logger log = LoggerFactory.getLogger(CTSAuthProvider.class);
	
	@Autowired 
	UserService userService;
	
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {		
		String email = null;
		String password = null;
		
		try {
			email = authentication.getName();
			password = authentication.getCredentials().toString();
			
			log.info("Authentication of '" + email + "' with " + (password != null ? "'[***]'" : "<empty>") + " password.");
			
			User user = userService.authenticate(email, password);
			
			List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
			for(UserRole role : user.getRoles()) {
				roles.add(new SimpleGrantedAuthority(role.getName()));
			}
			return new UsernamePasswordAuthenticationToken(convert(user), authentication, roles);
		} catch(UserNotFoundException ex) {
			log.warn("User '"+ email +"' does not exist.");
			throw new UsernameNotFoundException("User '"+ email +"' does not exist.", ex);
		} catch(AuthException ex) {
			log.warn("Invalid credentials or user does not exist for '" + email +"'.");
			throw new BadCredentialsException("Invalid credentials or user does not exist.", ex);
		} catch(Exception ex) {
			log.error("Internal error.", ex);
			throw new AuthenticationServiceException("Internal error.", ex);
		}
	}

	protected net.geant.nsi.contest.platform.web.data.User convert(User user) {
		net.geant.nsi.contest.platform.web.data.User u = new net.geant.nsi.contest.platform.web.data.User(user.getUserId(), user.getEmail(), user.getUsername());
		for(UserRole role : user.getRoles())
			u.getRoles().add(convert(role));
		return u;
	}
	
	protected net.geant.nsi.contest.platform.web.data.Role convert(UserRole role) {
		return new net.geant.nsi.contest.platform.web.data.Role(role.getName(), role.isAdmin());
	}
	
	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	@Override
	public String toString() {
		return "CTS Auth Provider";
	}
}

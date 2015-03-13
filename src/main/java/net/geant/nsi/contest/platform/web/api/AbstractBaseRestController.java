package net.geant.nsi.contest.platform.web.api;

import java.security.Principal;

import net.geant.nsi.contest.platform.web.data.User;
import net.geant.nsi.contest.platform.web.data.convert.Converter;
import net.geant.nsi.contest.platform.web.exceptions.UnsupportedAuthException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class AbstractBaseRestController {

	@Autowired
	protected Converter converter;
	
	protected User getCurrentUser() {
		return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	protected User getCurrentUser(Principal principal) throws UnsupportedAuthException {
		if(principal instanceof UsernamePasswordAuthenticationToken) {
			return (User)((UsernamePasswordAuthenticationToken) principal).getPrincipal();
		} else
			throw new UnsupportedAuthException("Unsupported principal type - " + principal);
	}
	
}

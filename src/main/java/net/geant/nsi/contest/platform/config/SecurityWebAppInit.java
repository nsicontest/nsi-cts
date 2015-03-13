package net.geant.nsi.contest.platform.config;

import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

@Order(1)
public class SecurityWebAppInit extends
		AbstractSecurityWebApplicationInitializer {
	
}

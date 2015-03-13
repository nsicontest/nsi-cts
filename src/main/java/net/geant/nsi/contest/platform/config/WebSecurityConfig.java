package net.geant.nsi.contest.platform.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.AccessDeniedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.geant.nsi.contest.platform.auth.CTSAuthManager;
import net.geant.nsi.contest.platform.auth.CTSAuthProvider;
import net.geant.nsi.contest.platform.auth.CTSPermissionEvaluator;
import net.geant.nsi.contest.platform.auth.permissions.IsAcceptedProjectMemberPermission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableWebMvcSecurity
@PropertySource("classpath:cts.properties")
@ComponentScan(basePackages = {"net.geant.nsi.contest.platform.auth"})
public class WebSecurityConfig /*extends WebSecurityConfigurerAdapter*/ {
	
	private final static Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);
	private static final String HTTPS_ENABLED = "https.enabled";
	
	@Autowired
	private Environment env;
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		return new CTSAuthProvider();
	}
	
	@Bean
	public AuthenticationManager authenticationManager() {
		return new CTSAuthManager();
	}
	
	
//	@Bean
//	public DefaultMethodSecurityExpressionHandler securityExpressionHandler() {
//		return new DefaultMethodSecurityExpressionHandler();
//	}
	
//	@Autowired
//	private AuthenticationProvider authProvider;
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder authBuilder, AuthenticationProvider authProvider) {
		log.debug("Setup CTS authentication provider. " + authProvider);
		authBuilder.authenticationProvider(authProvider);
	}
	
	


	public static class RestAccessDeniedHandler implements AccessDeniedHandler {
		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException)
				throws IOException, ServletException {
			
            response.setContentType("application/vnd.captech-v1.0+json");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            PrintWriter out = response.getWriter();
            out.print("{\"message\":\"You are not privileged to request this resource.\", \"access-denied\":true,\"cause\":\"AUTHORIZATION_FAILURE\"}");
            out.flush();
            out.close();			
		}
    }



	public static class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
 
            response.setContentType("application/vnd.captech-v1.0+json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter out = response.getWriter();
            out.print("{\"message\":\"Full authentication is required to access this resource.\", \"access-denied\":true,\"cause\":\"NOT AUTHENTICATED\"}");
            out.flush();
            out.close();
        }
    }



	
	@Configuration
	@Order(1)
	public static class ApiSecurity extends WebSecurityConfigurerAdapter {
		@Autowired
		Environment env;
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			log.info("API Security initialization.");
			
			boolean httpsEnabled = parseBoolean(env.getProperty(HTTPS_ENABLED));
			log.info("API HTTPS enabled: " + httpsEnabled);
			
			if(httpsEnabled)
				http.requiresChannel().anyRequest().requiresSecure();
			
			http.csrf().disable();
			
			http.antMatcher("/api/**").authorizeRequests().
					antMatchers("/api/ping").permitAll().
					antMatchers("/api/**").hasRole("USER").
				and().
					httpBasic().authenticationEntryPoint(new RestAuthenticationEntryPoint()).
				and().
					sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
				and().
					exceptionHandling().accessDeniedHandler(new RestAccessDeniedHandler());
		}		
	}
	
	@Configuration
	@Order(2)
	public static class WebSecurity extends WebSecurityConfigurerAdapter {
		@Autowired
		Environment env;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			log.info("Web Security initialization.");
			
			boolean httpsEnabled = parseBoolean(env.getProperty(HTTPS_ENABLED));
			log.info("Web HTTPS enabled: " + httpsEnabled);
			
			if(httpsEnabled)
				http.requiresChannel().anyRequest().requiresSecure();
			
			http.csrf().disable();
			
			http.
				authorizeRequests().
					//antMatchers("/api/**").anonymous().		// TODO: provide basic auth if necessary
				    antMatchers("/", "/resources/**", "/register", "/login", "/loginerror", "/terms", "/error", "/docs/**").permitAll().
					antMatchers("/admin/**", "/executor/**").hasRole("ADMIN").
					antMatchers("/projects/**").hasRole("USER").
					antMatchers("/profile/**","/home/**","/templates/**").authenticated().
					//anyRequest().authenticated().
				and().
					formLogin().
						usernameParameter("email").
						passwordParameter("password").
						loginPage("/").
						loginProcessingUrl("/login").
						defaultSuccessUrl("/login", true).
						failureUrl("/loginerror").
						permitAll().
				and().
					logout().
						logoutUrl("/logout").logoutSuccessUrl("/").
				and().
					sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS).
				and().
					exceptionHandling().accessDeniedPage("/error/403");
			
		}
	}
	
	
	
	@Bean
	public IsAcceptedProjectMemberPermission isAcceptedProjectMember() {
		return new IsAcceptedProjectMemberPermission();
	}
	
	@Bean
	public PermissionEvaluator permissionEvaluator() {
		CTSPermissionEvaluator pe = new CTSPermissionEvaluator();
		pe.getPermissions().put("isAcceptedProjectMember", isAcceptedProjectMember());
		
		return pe;
	}
	
	protected static boolean parseBoolean(String value) {
		if(value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equals("1")))
			return true;
		else
			return false;
	}
}

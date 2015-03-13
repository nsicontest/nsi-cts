package net.geant.nsi.contest.platform.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.geant.nsi.contest.platform.core.AdminService;
import net.geant.nsi.contest.platform.core.UserService;
import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.core.exceptions.UserExistsException;
import net.geant.nsi.contest.platform.data.UserRole;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * For custom initialization, e.g. database population, loading custom files, etc.
 * 
 * @author mikus
 *
 */
@Component
public class StartupInit implements ApplicationListener<ContextRefreshedEvent> {
	private final static Logger log = Logger.getLogger(StartupInit.class);
	
	String ADMIN_EMAIL = "admin.email";
	String ADMIN_USERNAME = "admin.username";
	String ADMIN_PASSWORD = "admin.password";
	String USER_ROLE_DEFAULT = "user.role.default";
	
	@Autowired
	private Environment env;
	
	@Autowired
	UserService userService;
	
	boolean initialized = false;
	
	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(initialized) {
			log.info("Custom initialization already done. Skipping...");
			return;
		}

		log.info("Startup - DB initialization.");
				
		try {
			List<UserRole> roles = new ArrayList<UserRole>();
			if(userService.count() == 0) {
				log.info("Adding user roles.");
				roles.add(new UserRole("ROLE_ADMIN", true));
				roles.add(new UserRole("ROLE_USER"));
				userService.storeRoles(roles);
			} else
				log.info("Skipping adding new user roles as there are already ones.");
		} catch(Exception ex) {
			log.error("Unable to store user roles");
			return;
		}
		
		userService.setUserDefaultRole("ROLE_USER");
		
		String adminEmail = env.getProperty(ADMIN_EMAIL, "admin@localhost");
		String adminUsername = env.getProperty(ADMIN_USERNAME, "admin");
		String adminPassword = env.getProperty(ADMIN_PASSWORD);
		
		log.debug("Admin: email='" +adminEmail + "' username='" + adminUsername + "' password=" + (adminPassword!=null ? "[***]" : "<empty>"));
		
		try {
			if(userService.findBy(adminEmail) == null) {
				log.info("Registering admin account for " + adminEmail);
				userService.register(adminEmail, adminUsername, adminPassword, new String[] {"ROLE_ADMIN"});
			} else
				log.info("Admin " + adminEmail + " already registered.");
		} catch (UserExistsException e) {
			log.warn("Admin user already exists. Registration skipped.");
		} catch (CTSException e) {
			log.error("Unable to register admin user.", e);
		}
		
		createUploadDir(env.getProperty("topology.location", "."));
		
		initialized = true;
	}

	protected void createUploadDir(String path) {
		File dir = new File(path);
		if(!dir.exists()) {
			log.info("Creating folder " + path);
			dir.mkdirs();
		} else
			log.info("Folder " + path + " already exists.");
	}
}

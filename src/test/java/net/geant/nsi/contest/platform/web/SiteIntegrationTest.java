package net.geant.nsi.contest.platform.web;

import net.geant.nsi.contest.platform.config.CoreConfig;
import net.geant.nsi.contest.platform.config.PersistenceConfig;
import net.geant.nsi.contest.platform.config.WebConfig;
import net.geant.nsi.contest.platform.config.WebSecurityConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfig.class, WebConfig.class, PersistenceConfig.class, WebSecurityConfig.class})
public class SiteIntegrationTest {

	@InjectMocks
	SiteController controller;
	
	MockMvc mockMvc;
	
	@Autowired
	WebApplicationContext webAppContext;
	
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = standaloneSetup(controller).build();
	}
	
	@Test
	public void checkHome() throws Exception {
		 fail("Not implemented");
	}

	
}

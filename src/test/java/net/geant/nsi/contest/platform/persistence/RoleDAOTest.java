package net.geant.nsi.contest.platform.persistence;

import java.util.ArrayList;
import java.util.List;

import net.geant.nsi.contest.platform.config.PersistenceConfig;
import net.geant.nsi.contest.platform.data.UserRole;
import net.geant.nsi.contest.platform.persistence.exceptions.CTSPersistenceException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={PersistenceConfig.class}/* loader = AnnotationConfigContextLoader.class, */ )
@Transactional()
@TransactionConfiguration(defaultRollback=true)
public class RoleDAOTest {
	
//	@Autowired
//	RoleDAO roles;
	
	@Autowired
	RoleRepository roles;
	
	@Before
	public void init() {
		roles.save(new UserRole("ADMIN"));
		roles.save(new UserRole("USER"));		
	}
	
	@Test
	public void testStore() {
		assertEquals(2, roles.findAll().size());
	}
	
	@Test
	public void testFindRole() throws CTSPersistenceException {
		assertNotNull(roles.findByName("ADMIN"));
	}
	
	@Test
	public void testNotFindRole() throws CTSPersistenceException {
		assertNull(roles.findByName("NOTEXISTS"));
	}
	
}

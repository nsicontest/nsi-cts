package net.geant.nsi.contest.platform.persistence;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import net.geant.nsi.contest.platform.config.PersistenceConfig;
import net.geant.nsi.contest.platform.data.User;
import net.geant.nsi.contest.platform.data.UserRole;
import net.geant.nsi.contest.platform.persistence.exceptions.CTSPersistenceException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={PersistenceConfig.class}/* loader = AnnotationConfigContextLoader.class, */ )
@Transactional()
@TransactionConfiguration(defaultRollback=true)
public class UsersDAOTest {

//	@Autowired
//	UserDAO users;
//	
//	@Autowired
//	RoleDAO roles;
	
	@Autowired
	UserRepository users;
	
	@Autowired
	RoleRepository roles;
	
	
	User user1;
	User user2;
	User user3;
	
	@Before
	public void init() {
		UserRole role = roles.save(new UserRole("USER"));
		
		user1 = new User("tester@tester.com");
		user1.setUsername("tester");
		user1.setPassword("secret1");
		user1.getRoles().add(role);
		user1 = users.save(user1);
		
		user2 = new User("tester2@tester.com");
		user2.setUsername("tester2");
		user2.setPassword("secret2");
		user2.getRoles().add(role);
		user2 = users.save(user2);
		
		user3 = new User("tester3@tester.com");
		user3.setUsername("tester3");
		user3.setPassword("secret3");
		user3.getRoles().add(role);
		user3 = users.save(user3);			
		
	}
	
	@After
	public void cleanup() {
	}
	
	@Test
	public void testSave() {		
		assertNotNull(user1.getId());
		assertEquals(3, users.count());
	}


	@Test
	public void testRemove() {
		assertEquals(3, users.count());
		
		users.delete(user1.getId());

		assertEquals(2, users.count());//getAll().size());
		assertEquals(1, roles.findAll().size());
	}
	
	@Test
	public void testFindByEmail() throws CTSPersistenceException {
		assertNotNull("tester3@tester.com", users.findByEmail("tester3@tester.com").getEmail());
	}
	
	@Test
	public void testFindByUUID() throws CTSPersistenceException {
		User user = users.findByUserId(user2.getUserId());
		assertNotNull(user);
		assertEquals(user2, user);
	}
	

}

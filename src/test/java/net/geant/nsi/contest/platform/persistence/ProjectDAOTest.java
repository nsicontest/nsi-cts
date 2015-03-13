package net.geant.nsi.contest.platform.persistence;

import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;

import net.geant.nsi.contest.platform.config.PersistenceConfig;
import net.geant.nsi.contest.platform.data.Project;
import net.geant.nsi.contest.platform.data.User;
import net.geant.nsi.contest.platform.data.UserAcl;
import net.geant.nsi.contest.platform.data.UserAcl.Status;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={PersistenceConfig.class}/* loader = AnnotationConfigContextLoader.class, */ )
@Transactional()
@TransactionConfiguration(defaultRollback=true)
public class ProjectDAOTest {

//	@Autowired
//	RoleDAO roles;
//	
//	@Autowired
//	UserDAO users;
//
//	@Autowired
//	ProjectDAO projects;
	
	@Autowired 
	RoleRepository roles;
	
	@Autowired
	UserRepository users;
	
	@Autowired
	ProjectRepository projects;
	
	User user1;
	User user2;
	
	@Before
	public void init() {
		UserRole userRole = new UserRole("ROLE_USER");
		userRole = roles.save(userRole);
		
		user1 = new User("tester@localhost", "tester", "secret");
		user1.getRoles().add(userRole);
		users.save(user1);
		
		user2 = new User("tester2@localhost", "tester2", "secret2");
		user2.getRoles().add(userRole);
		users.save(user2);
	}
	
	@Test
	public void testProjectWithUsers() throws CTSPersistenceException {
		boolean added = false;
		Project project = new Project("project");
		
		assertNotNull(project.getKey());
		UUID key = project.getKey();
		
		projects.save(project);
		assertEquals(1, projects.findAll().size());
		
		project = projects.findByKey(key);
		added = project.addUser(users.findByEmail("tester2@localhost"), UserAcl.Status.ACCEPTED);
		projects.save(project);
		
		assertTrue(added);
		assertEquals(1, projects.findAll().size());		
		assertEquals(1, projects.findByKey(key).getUserAclsBy(UserAcl.Status.ACCEPTED).size());
		
		project = projects.findByKey(key);
		added = project.addUser(users.findByUserId(user1.getUserId()));
		projects.save(project);

		assertTrue(added);
		assertEquals(1, projects.findAll().size());		
		assertEquals(2, projects.findByKey(key).getUserAcls().size());
		
		
	}

	@Test
	public void testTwoProjectsOneUser() throws CTSPersistenceException {
		User user = users.findByEmail("tester@localhost");
		
		Project project1 = new Project("project1");
		UUID key1 = project1.getKey();
		project1.addUser(user, Status.AWAITING);
		projects.save(project1);

		Project project2 = new Project("project2");
		UUID key2 = project2.getKey();
		project2.addUser(user, Status.ACCEPTED);
		projects.save(project2);

		project1 = projects.findByKey(key1);
		project2 = projects.findByKey(key2);
		
		assertEquals(1, project1.getUserAclsBy(Status.AWAITING).size());
		assertEquals(0, project1.getUserAclsBy(Status.ACCEPTED).size());
		
		assertEquals(0, project2.getUserAclsBy(Status.AWAITING).size());
		assertEquals(1, project2.getUserAclsBy(Status.ACCEPTED).size());
		
	}
	
	@Test
	public void testAcceptProjectUser() throws CTSPersistenceException {
		User user1 = users.findByEmail("tester@localhost");
		User user2 = users.findByEmail("tester2@localhost");
		
		Project project = new Project("project1");
		project.addUser(user1, Status.ACCEPTED);
		project.addUser(user2);
		projects.save(project);

		project = projects.findByKey(project.getKey());
		assertEquals(1, project.getUserAclsBy(Status.ACCEPTED).size());
		assertTrue(project.hasUserAccess(user1.getUserId()));
		
		UserAcl acl = project.getUserAcl(user2);
		acl.setStatus(Status.ACCEPTED);
		projects.save(project);
		
		
		project = projects.findByKey(project.getKey());
		assertEquals(2, project.getUserAclsBy(Status.ACCEPTED).size());
		
	}	
	
	@Test
	public void testRemoveProjectUser() throws CTSPersistenceException {
		User user1 = users.findByEmail("tester@localhost");
		User user2 = users.findByEmail("tester2@localhost");
		
		Project project = new Project("project1");
		project.addUser(user1, Status.ACCEPTED);
		project.addUser(user2);
		projects.save(project);

		project = projects.findByKey(project.getKey());
		assertEquals(1, project.getUserAclsBy(Status.ACCEPTED).size());
		
		project.removeUser(user2);
		projects.save(project);
		
		project = projects.findByKey(project.getKey());
		assertEquals(1, project.getUserAcls().size());
		
	}		
	
	
	@Test
	public void testProjectByUser() throws CTSPersistenceException {
		User user = users.findByEmail("tester@localhost");
		
		Project project1 = new Project("project1");
		project1.addUser(user, Status.AWAITING);
		projects.save(project1);

		Project project2 = new Project("project2");
		project2.addUser(user, Status.ACCEPTED);
		projects.save(project2);

		Project project3= new Project("project3");
		projects.save(project3);
		

		List<Project> results = projects.getByUser(user.getUserId());
		assertEquals(2, results.size());
		
		assertNotNull(projects.getUserAcl(project2.getKey(), user.getUserId()));
	}
	
	@Test
	public void testRemove() throws CTSPersistenceException {
		User user = users.findByEmail("tester@localhost");
		
		Project project1 = new Project("project1");
		UUID key1 = project1.getKey();
		project1.addUser(user, Status.AWAITING);
		project1 = projects.save(project1);

		Project project2 = new Project("project2");
		UUID key2 = project2.getKey();
		project2.addUser(user, Status.ACCEPTED);
		project2 = projects.save(project2);
		
		assertEquals(2, projects.count());
		
		projects.delete(project1.getId());
		
		assertEquals(1, projects.findAll().size());
	}
}

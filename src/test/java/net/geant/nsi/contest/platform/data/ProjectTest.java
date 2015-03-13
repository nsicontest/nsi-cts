package net.geant.nsi.contest.platform.data;

import java.util.List;
import java.util.Set;

import net.geant.nsi.contest.platform.data.UserAcl.Status;

import org.junit.Test;
import static org.junit.Assert.*;


public class ProjectTest {

	@Test
	public void testNoUserAccess() {
		Project project = new Project("test");
		User user = new User("tester@localhost");
		
		project.addUser(user);
		
		assertFalse(project.hasUserAccess(user.getUserId()));
	}
	
	@Test
	public void testHasUserAccess() {
		Project project = new Project("test");
		User user = new User("tester@localhost");
		
		project.addUser(user, Status.ACCEPTED);
		
		assertTrue(project.hasUserAccess(user.getUserId()));
	}
	
	@Test
	public void testGetUserAcl() {
		Project project = new Project("test");
		User user = new User("tester@localhost");
		
		project.addUser(user);
		
		UserAcl acl = project.getUserAcl(user);
		assertNotNull(acl);
		assertEquals(Status.AWAITING, acl.getStatus());
	}

	@Test
	public void testGetUsersByStatus() {
		Project project = new Project("test");
		User user = new User("tester@localhost");
		User user2 = new User("tester2@localhost");
		User user3 = new User("tester3@localhost");
		
		project.addUser(user);
		project.addUser(user2, Status.ACCEPTED);
		project.addUser(user3);
		
		Set<UserAcl> userAcls = project.getUserAclsBy(Status.ACCEPTED);
		assertEquals(1, userAcls.size());
		
		userAcls = project.getUserAclsBy(Status.AWAITING);
		assertEquals(2, userAcls.size());		
	}

	@Test
	public void testChangeUserStatus() {
		Project project = new Project("test");
		User user = new User("tester@localhost");
		User user2 = new User("tester2@localhost");
		User user3 = new User("tester3@localhost");
		
		project.addUser(user);
		project.addUser(user2, Status.ACCEPTED);
		project.addUser(user3);
		
		Set<UserAcl> userAcls = project.getUserAclsBy(Status.ACCEPTED);
		assertEquals(1, userAcls.size());
		
		userAcls = project.getUserAclsBy(Status.AWAITING);
		assertEquals(2, userAcls.size());		

		UserAcl aclToChange = project.getUserAcl(user3);
		aclToChange.setStatus(Status.ACCEPTED);
		userAcls = project.getUserAclsBy(Status.AWAITING);
		assertEquals(1, userAcls.size());		
		assertEquals(3, project.getUserAcls().size());
		
	}
	
}


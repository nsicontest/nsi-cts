package net.geant.nsi.contest.platform.persistence;

import java.util.ArrayList;
import java.util.List;

import net.geant.nsi.contest.platform.config.PersistenceConfig;
import net.geant.nsi.contest.platform.data.LogEntry;
import net.geant.nsi.contest.platform.data.NSIInstance;
import net.geant.nsi.contest.platform.data.Project;
import net.geant.nsi.contest.platform.data.TestCase;
import net.geant.nsi.contest.platform.data.TestCaseTemplate;

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
@ContextConfiguration(classes={PersistenceConfig.class})
@Transactional()
@TransactionConfiguration(defaultRollback=true)
public class TestCaseRepositoryTest {

	@Autowired
	TestCaseRepository testcases;
	
	TestCase testCase11;
	TestCase testCase21;
	TestCase testCase22;
	
	@Before
	public void init() {
		TestCaseTemplate template = new TestCaseTemplate();

		Project project1 = new Project("testProject");
		
		testCase11 = new TestCase(project1);
		net.geant.nsi.contest.platform.data.Test test1 = testCase11.createTest(template);
		project1.addTestCase(testCase11);
		test1.addInstance(new NSIInstance("nsi111"));
		test1.addInstance(new NSIInstance("nsi112"));
		LogEntry entry1 = new LogEntry(test1, test1.getInstance("nsi11"), "msg1");
		LogEntry entry2 = new LogEntry(test1, test1.getInstance("nsi12"), "msg2");
		LogEntry entry3 = new LogEntry(test1, test1.getInstance("nsi11"), "msg3");
		test1.addLog(entry1);
		test1.addLog(entry2);
		test1.addLog(entry3);
				
		testcases.save(testCase11);

		Project project2 = new Project("testProject2");
		testCase21 = new TestCase(project2);
		net.geant.nsi.contest.platform.data.Test test21 = testCase21.createTest(template);
		project2.addTestCase(testCase21);
		testCase22 = new TestCase(project2);
		net.geant.nsi.contest.platform.data.Test test22 = testCase22.createTest(template);
		project2.addTestCase(testCase22);
		
		test21.addInstance(new NSIInstance("nsi211"));
		test21.addInstance(new NSIInstance("nsi212"));
		test21.addInstance(new NSIInstance("nsi213"));
		test22.addInstance(new NSIInstance("nsi221"));
		test22.addInstance(new NSIInstance("nsi222"));
		
		test21.addLog(new LogEntry(test21.getInstance("nsi211"), "msg4"));
		test21.addLog(new LogEntry(test21.getInstance("nsi211"), "msg5"));
		test21.addLog(new LogEntry(test21.getInstance("nsi212"), "msg6"));
		test22.addLog(new LogEntry(test22.getInstance("nsi221"), "msg7"));
		
		testcases.save(testCase21);
	
	}
	
	@Test
	public void testStore() {
		
		assertNotNull(testCase11.getId());
		assertNotNull(testCase11.getCreatedAt());
		
		assertEquals(2, testcases.findOne(testCase11.getId()).getTests().get(0).getInstances().size());
		assertEquals(3, testcases.findOne(testCase11.getId()).getTests().get(0).getLogs().size());		
		
		assertNotNull(testCase21.getId());

		assertEquals(3, testcases.count());
	}
	
	@Test
	public void testFindByTestCaseId() {
		assertEquals(testCase21, testcases.findByTestCaseId(testCase21.getTestCaseId()));
	}
	
	
}

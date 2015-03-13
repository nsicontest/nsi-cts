package net.geant.nsi.contest.platform.core;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.geant.nsi.contest.platform.config.CoreConfig;
import net.geant.nsi.contest.platform.config.PersistenceConfig;
import net.geant.nsi.contest.platform.core.tasks.Task;
import net.geant.nsi.contest.platform.core.tasks.TaskManager;
import net.geant.nsi.contest.platform.core.tasks.TaskManagerImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {CoreConfig.class, PersistenceConfig.class})
public class TaskManagerImplTest {

	private class CountTask implements Runnable {
		int count = 0;
		@Override
		public void run() {
			count++;
			System.out.println(count);
		}
		public final int getCount() { return count;	}
	}
	
	private class WaitTask implements Runnable {
		long waitTime = 0;
		public WaitTask(long waitTime) { this.waitTime = waitTime; }
		
		@Override
		public void run() {
			System.out.println(this + ": waiting " + waitTime + " ms");
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(this + ": finished");
		}		
	}
	
	@Autowired
	TaskManagerImpl taskManager;
	
	@Before
	public void init() {
		
	}
	
	@After
	public void destroy() {
		taskManager.destroy();
	}
	
	@Test
	public void testGetDefaultTasks() {
		assertEquals(1, taskManager.getScheduledTasks().size());
		assertEquals(0, taskManager.getRunningTasks().size());
	}
	
	@Test
	public void testScheduleTask() throws InterruptedException {
		Task task = taskManager.schedule(new CountTask(), new CronTrigger("*/1 * * * * *"), null);
		assertEquals(2, taskManager.getScheduledTasks().size());
		Thread.sleep(6000);
		CountTask ct = (CountTask) task.getTask();
		assertTrue(ct.getCount() >= 5);
		taskManager.cancel(task);
		assertEquals(1, taskManager.getScheduledTasks().size());
	}

	@Test
	public void testRescheduleTask() throws InterruptedException {
		Task task = taskManager.schedule(new CountTask(), new CronTrigger("*/1 * * * * *"), null);
		assertEquals(2, taskManager.getScheduledTasks().size());
		Thread.sleep(6000);
		CountTask ct = (CountTask) task.getTask();
		assertTrue(ct.getCount() >= 6);
		
		task = taskManager.schedule(task, new CronTrigger("*/3 * * * * *"));
		Thread.sleep(6000);
		assertTrue(ct.getCount() < 10);
		assertEquals(2, taskManager.getScheduledTasks().size());
		
		taskManager.cancel(task);
		assertEquals(1, taskManager.getScheduledTasks().size());		
	}
	
	@Test
	public void testQueueTask() throws InterruptedException {
		// reschedule default queue process task
		Task queueProcessTask = taskManager.getScheduledTasks().iterator().next();
		taskManager.schedule(queueProcessTask, new CronTrigger("*/1 * * * * *"));
		
		Task task = taskManager.queue(new CountTask(), null);
		Thread.sleep(3000);
		assertEquals(1, ((CountTask)task.getTask()).getCount());
	}
	
	@Test
	public void testQueueGroupTasks() throws InterruptedException {
		// reschedule default queue process task
		Task queueProcessTask = taskManager.getScheduledTasks().iterator().next();
		taskManager.schedule(queueProcessTask, new CronTrigger("*/1 * * * * *"), null);
		
		Task t1 = taskManager.queue(new WaitTask(5000), "test");
		Thread.sleep(1000);
		assertEquals(0, taskManager.countQueuedTasks());
		assertEquals(1, taskManager.countRunningTasks());
		
		Task t2 = taskManager.queue(new WaitTask(1000), "test");
		assertEquals(1, taskManager.countRunningTasks());
		assertEquals(1, taskManager.countQueuedTasks());
		
		Thread.sleep(7000);
		
	}
	
}

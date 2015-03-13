package net.geant.nsi.contest.platform.core.tasks;

import java.util.Collection;
import java.util.List;

import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Component;

@Component
public interface TaskManager {
	/**
	 * Create and schedule user task
	 * 
	 * @param task
	 * @param trigger
	 * @param description
	 * @return
	 */
	Task schedule(Runnable task, Trigger trigger, String description);
	
	/**
	 * 
	 * Reschedule existing task
	 * 
	 * @param task
	 * @param trigger
	 * @return
	 */
	Task schedule(Task task, Trigger trigger);
	
	/**
	 * Execute user task immediately without any dependencies
	 * 
	 * @param task
	 * @param description
	 * @return
	 */
	Task queue(Runnable task, String description);
	
	/**
	 * Queue task to be executed sequentially within provided group
	 * 
	 * @param task
	 * @param group
	 * @param description
	 * @return
	 */
	Task queue(Runnable task, String group, String description);
	
	/**
	 * Remove task. Task will be stopped if needed
	 * 
	 * @param task
	 */
	void cancel(Task task);
	void cancel(long taskId);
	
	
	Collection<ScheduledTask> getScheduledTasks();
	Collection<QueuedTask> getRunningTasks();
	Collection<QueuedTask> getQueuedTasks();
	Collection<QueuedTask> getQueuedTasks(String group);
	
	long countScheduledTasks();
	long countActiveTasks();
	long countRunningTasks();
	long countQueuedTasks();
	long countQueuedTasks(String group);
}

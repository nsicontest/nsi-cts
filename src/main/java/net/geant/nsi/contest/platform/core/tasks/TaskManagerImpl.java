package net.geant.nsi.contest.platform.core.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

@Service
public class TaskManagerImpl implements TaskManager, QueuedTaskListener, TaskExecutorTrigger {
	private final static Logger log = Logger.getLogger(TaskManagerImpl.class);
			
	ThreadPoolTaskScheduler taskScheduler;
	
	ThreadPoolTaskExecutor taskExecutor;

	Map<Long, Task> handledTasks = new HashMap<Long, Task>();
	
	Map<Long, ScheduledFuture<?>> scheduledTaskHandlers = new HashMap<Long, ScheduledFuture<?>>();
	Map<Long, ScheduledTask> scheduledTasks = new HashMap<Long, ScheduledTask>();
	
	AtomicLong sequencer = new AtomicLong();
	
	Map<String, QueuedTask> runningTasks = new HashMap<String, QueuedTask>();

	Queue<QueuedTask> queuedTasks = new LinkedList<QueuedTask>();
	
	public TaskManagerImpl() {
		if(taskScheduler == null) {
			taskScheduler = new ThreadPoolTaskScheduler();
			taskScheduler.initialize();
		}
		if(taskExecutor == null) {
			taskExecutor = new ThreadPoolTaskExecutor();
			taskExecutor.initialize();
		}
		this.schedule(new QueueProcessingExecutorTask(this), new CronTrigger("*/5 * * * * *"), "Default Queue Processor");
	}
	
	public TaskManagerImpl(ThreadPoolTaskScheduler taskScheduler, ThreadPoolTaskExecutor taskExecutor, Trigger queueExecutorTrigger) {
		if(taskScheduler != null)
			this.taskScheduler = taskScheduler;
		
		if(taskExecutor != null)
			this.taskExecutor = taskExecutor;
		
		if(queueExecutorTrigger == null)
			queueExecutorTrigger = new CronTrigger("*/5 * * * * *");
			
		this.schedule(new QueueProcessingExecutorTask(this), queueExecutorTrigger, "Default Queue Processor");
	}

	@Override
	public synchronized Task schedule(Runnable t, Trigger trigger, String description) {
		long id = sequencer.getAndIncrement();
		try {
			ScheduledTask task = new ScheduledTask(id, t, description);
			ScheduledFuture<?> result = taskScheduler.schedule(task, trigger);
			if(result == null) {
				log.warn("Trigger " + trigger + " for task " + task + " is not going to fire.");
				return null;
			}
			
			handledTasks.put(id, task);
			
			scheduledTasks.put(id, task);
			scheduledTaskHandlers.put(id, result);
			
			return task;
		} catch(TaskRejectedException ex) {
			log.error("Unable to schedule task id=" + id + ":" + t);
			return null;
		}
	}
	
	@Override
	public synchronized Task schedule(Task task, Trigger trigger) {
		if(task == null  || task.getTask() == null)
			throw new IllegalArgumentException("Invalid task object.");
		if(trigger == null)
			throw new IllegalArgumentException("Trigger is null.");
		
		ScheduledFuture<?> sf = scheduledTaskHandlers.get(task.getId());
		if(sf == null)
			log.warn("Task id " + task.getId() + " is not scheduled. Scheduling as a new task");
		else 
			cancel(task);
		
		return schedule(task.getTask(), trigger, task.getDescription());
	}

	@Override
	public synchronized long executeQueuedTasks() {
		long count = 0;
		for(QueuedTask task : queuedTasks) {
			boolean result = execute(task);
			if(result)
				count ++;
		}
		return count;
	}
	
	private boolean execute(QueuedTask task) {
		if(task == null)
			return false;
		
		Future<?> handler = null;
		try {
			if(task instanceof GroupQueuedTask) 
				if(runningTasks.containsKey(task.getIdentifier()))
					return false;
			
			handler = taskExecutor.submit(task);
			runningTasks.put(task.getIdentifier(), task);
			queuedTasks.remove(task);
			return true;
		} catch(Exception ex) {
			return false;
		}
	}
		
	@Override
	public synchronized Task queue(Runnable task, String description) {
		return queue(task, null, description);
	}

	@Override
	public synchronized Task queue(Runnable task, String group, String description) {		
		long id = sequencer.getAndIncrement();
		QueuedTask newTask = null;
		
		if (group == null)
			newTask = new QueuedTask(id, task, description, this);
		else
			newTask = new GroupQueuedTask(id, group, task, description, this);
		
		handledTasks.put(id, newTask);
		queuedTasks.add(newTask);
		
		return newTask;
	}
	
	@Override
	public synchronized void onSuccess(QueuedTask task) {
		if(task == null)
			return;
		
		runningTasks.remove(task.getIdentifier());
		handledTasks.remove(task.getId());
	}

	@Override
	public synchronized void onFailure(QueuedTask task, Throwable t) {
		log.error("Task " + task + " reports an issue.", t);
		
		if(task == null)
			return;
		
		runningTasks.remove(task.getIdentifier());
		handledTasks.remove(task.getId());
	}

	@Override
	public synchronized void cancel(Task task) {
		if(task == null)
			return;
		
		if(task instanceof QueuedTask) {
			if(runningTasks.containsValue(task)) {
				runningTasks.remove(((QueuedTask) task).getIdentifier());
			}
			
			queuedTasks.remove(task);
		} else if(task instanceof ScheduledTask) {
			ScheduledFuture<?> t = scheduledTaskHandlers.remove(task.getId());
			if(t != null) {
				t.cancel(true);
				scheduledTasks.remove(task.getId());
			}
		}
		handledTasks.remove(task.getId());
	}
	
	@Override
	public synchronized void cancel(long taskId) {
		Task task = handledTasks.get(taskId);
		if(task != null)
			cancel(task);
	}
	
	@Override
	public final Collection<ScheduledTask> getScheduledTasks() {
		return Collections.unmodifiableCollection(scheduledTasks.values());
	}

	@Override
	public Collection<QueuedTask> getRunningTasks() {
		return Collections.unmodifiableCollection(runningTasks.values());
	}

	@Override
	public Collection<QueuedTask> getQueuedTasks() {
		return Collections.unmodifiableCollection(queuedTasks);
	}

	@Override
	public Collection<QueuedTask> getQueuedTasks(String group) {
		if(group == null)
			return getQueuedTasks();
			
		Collection<QueuedTask> result = new ArrayList<QueuedTask>();
		for(QueuedTask t : queuedTasks)
			if(t instanceof GroupQueuedTask && ((GroupQueuedTask)t).getGroup().equals(group))
				result.add(t);
		
		return Collections.unmodifiableCollection(result);
	}

	@Override
	public long countScheduledTasks() {
		return scheduledTasks.size();
	}

	@Override
	public long countActiveTasks() {
		return runningTasks.size() + scheduledTasks.size();
	}

	@Override
	public long countRunningTasks() {
		return runningTasks.size();
	}

	@Override
	public long countQueuedTasks() {
		return queuedTasks.size();
	}

	@Override
	public long countQueuedTasks(String group) {
		long count = 0;
		for(Task t : queuedTasks)
			if(t instanceof GroupQueuedTask)
				count++;
		return count;
	}
	
	public void destroy() {
		taskScheduler.destroy();
		taskExecutor.destroy();
		
		runningTasks.clear();
		scheduledTaskHandlers.clear();
		scheduledTasks.clear();			
	}
	

	
	
	

}

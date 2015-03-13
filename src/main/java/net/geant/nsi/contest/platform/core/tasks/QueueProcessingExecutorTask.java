package net.geant.nsi.contest.platform.core.tasks;


public class QueueProcessingExecutorTask implements Runnable  {
	
	TaskExecutorTrigger taskExecutorTrigger;
	
	public QueueProcessingExecutorTask(TaskExecutorTrigger taskExecutorTrigger) {
		if(taskExecutorTrigger == null)
			throw new IllegalArgumentException("Task Manager is null.");
		
		this.taskExecutorTrigger = taskExecutorTrigger;
	}
	
	@Override
	public void run() {
		if(taskExecutorTrigger != null)
			taskExecutorTrigger.executeQueuedTasks();
	}
	
	
}

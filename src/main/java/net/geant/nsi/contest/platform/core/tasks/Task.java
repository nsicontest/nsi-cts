package net.geant.nsi.contest.platform.core.tasks;

import java.util.Calendar;
import java.util.Date;

public abstract class Task implements Runnable {
	Long id;
	Runnable task;
	Date createdAt;
	String description;
	
	protected Task(Long id, Runnable task, String description) {
		this.id = id;
		this.task = task;
		this.description = description;
		this.createdAt = Calendar.getInstance().getTime(); 
	}

	public final Long getId() {
		return id;
	}

	public final Runnable getTask() {
		return task;
	}

	public final Date getCreatedAt() {
		return createdAt;
	}

	public final String getDescription() {
		return description;
	}
	
	public final String getType() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	public void run() {
		if(task != null)
			task.run();
	}

}

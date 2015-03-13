package net.geant.nsi.contest.platform.core.tasks;

import java.util.Calendar;
import java.util.Date;

public class QueuedTask extends Task {
	QueuedTaskListener listener;
	Date startedAt;
	
	
	protected QueuedTask(Long id, Runnable task, String description, QueuedTaskListener listener) {
		super(id, task, description);
		
		if(listener == null)
			throw new IllegalArgumentException("listener is null");
		
		this.listener = listener;
	}
	
	public String getIdentifier() {
		return "Task-" + getId();
	}

	@Override
	public void run() {
		try {
			startedAt = Calendar.getInstance().getTime();
			super.run();
			if(listener != null)
				listener.onSuccess(this);
		} catch(Exception ex) {
			if(listener != null)
				listener.onFailure(this, ex);
		}
	}

	public final Date getStartedAt() {
		return startedAt;
	}
	
	
}

package net.geant.nsi.contest.platform.core.tasks;

public class ScheduledTask extends Task {
	protected ScheduledTask(Long id, Runnable task, String description) {
		super(id, task, description);
	}
}

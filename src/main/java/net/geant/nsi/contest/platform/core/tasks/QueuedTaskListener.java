package net.geant.nsi.contest.platform.core.tasks;

public interface QueuedTaskListener {
	void onSuccess(QueuedTask task);
	void onFailure(QueuedTask task, Throwable t);
}

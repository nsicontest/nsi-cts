package net.geant.nsi.contest.platform.core.tasks;

public class GroupQueuedTask extends QueuedTask {
	String group;
	
	protected GroupQueuedTask(Long id, String group, Runnable task, String description, QueuedTaskListener listener) {
		super(id, task, description, listener);
		
		if(group == null)
			throw new IllegalArgumentException("group is null");
		
		this.group = group;
	}

	public final String getGroup() {
		return group;
	}

	@Override
	public String getIdentifier() {
		return "GroupTask-" + getGroup();
	}

}

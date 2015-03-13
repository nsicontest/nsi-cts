package net.geant.nsi.contest.platform.web.data;

import java.util.UUID;

import net.geant.nsi.contest.platform.data.UserAcl.Status;

public class UserAcl {
	UUID userId;
	UUID projectKey;
	Status status;
	
	public UserAcl(UUID projectKey, UUID userId, Status status) {
		this.projectKey = projectKey;
		this.userId = userId;
		this.status = status;
	}

	public final UUID getUserId() {
		return userId;
	}

	public final void setUserId(UUID userId) {
		this.userId = userId;
	}

	public final UUID getProjectKey() {
		return projectKey;
	}

	public final void setProjectKey(UUID projectKey) {
		this.projectKey = projectKey;
	}

	public final Status getStatus() {
		return status;
	}

	public final void setStatus(Status status) {
		this.status = status;
	}
	
	
	
}

package net.geant.nsi.contest.platform.web.data;

import java.util.UUID;

import net.geant.nsi.contest.platform.data.AgentType;

public class Project {

	private UUID key;
	private String name;
	
	private String networkId;
	
//	private String url;
//	private AgentType type;
	
	private long usersCount;
	
	public Project(UUID key, String name) {
		this.key = key;
		this.name = name;
	}
	
	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

//	public String getUrl() {
//		return url;
//	}
//
//	public void setUrl(String url) {
//		this.url = url;
//	}
//
//	public AgentType getType() {
//		return type;
//	}
//
//	public void setType(AgentType type) {
//		this.type = type;
//	}

	public final String getNetworkId() {
		return networkId;
	}

	public final void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	public long getUsersCount() {
		return usersCount;
	}

	public void setUsersCount(long usersCount) {
		this.usersCount = usersCount;
	}
	
	
}

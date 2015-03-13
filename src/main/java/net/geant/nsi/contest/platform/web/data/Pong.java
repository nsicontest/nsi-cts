package net.geant.nsi.contest.platform.web.data;

import java.util.Date;

public class Pong {
	private String name;
	private Date timestamp;
	
	public Pong(String name, Date timestamp) {
		super();
		this.name = name;
		this.timestamp = timestamp;
	}
	
	
	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	public final Date getTimestamp() {
		return timestamp;
	}
	public final void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}

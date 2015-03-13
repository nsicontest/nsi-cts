package net.geant.nsi.contest.platform.web.data;

public class Role {
	String name;
	boolean isAdmin = false;
	public Role(String name, boolean isAdmin) {
		this.name = name;
		this.isAdmin = isAdmin;
	}
	public String getName() {
		return name;
	}
	public boolean isAdmin() {
		return isAdmin;
	}
}

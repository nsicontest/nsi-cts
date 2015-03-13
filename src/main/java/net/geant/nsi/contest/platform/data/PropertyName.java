package net.geant.nsi.contest.platform.data;

public enum PropertyName {
	
	INFO("info"), VERSION("version");
	
	private final String name;
	PropertyName(String name) {
		this.name = name;
	}
	
	public String getName() { return name; }
}

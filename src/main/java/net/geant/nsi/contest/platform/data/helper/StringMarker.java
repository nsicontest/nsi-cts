package net.geant.nsi.contest.platform.data.helper;

public abstract class StringMarker {
	String name;
	
	public StringMarker(String name) {
		if(name == null)
			throw new IllegalArgumentException("marker name cannot be null");
		
		this.name = name;
	}
	
	
	public final String getName() {
		return name;
	}

	public abstract String getValue();

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StringMarker other = (StringMarker) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}

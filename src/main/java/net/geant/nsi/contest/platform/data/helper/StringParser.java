package net.geant.nsi.contest.platform.data.helper;

import java.util.HashSet;
import java.util.Set;

public class StringParser {
	
	Set<StringMarker> markers = new HashSet<StringMarker>();
	
	public String parse(String text) {
		if(text == null) return null;
		
		String result = text;
		for(StringMarker marker : markers)
			result = result.replaceAll("\\{" + marker.getName() + "\\}", marker.getValue());
		
		return result;
	}
	
	public void register(StringMarker marker) {
		if(marker != null)
			markers.add(marker);
	}
	
}

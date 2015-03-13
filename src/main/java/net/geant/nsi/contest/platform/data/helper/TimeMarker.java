package net.geant.nsi.contest.platform.data.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeMarker extends StringMarker {

	public TimeMarker(String name) {
		super(name);
	}

	@Override
	public String getValue() {
		return new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
	}

}

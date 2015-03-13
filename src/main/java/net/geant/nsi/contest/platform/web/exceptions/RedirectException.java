package net.geant.nsi.contest.platform.web.exceptions;

import java.util.List;

import net.geant.nsi.contest.platform.core.exceptions.CTSRuntimeException;
import net.geant.nsi.contest.platform.web.data.Alert;

public class RedirectException extends CTSRuntimeException {
	String redirect;
	List<Alert> alerts;
	
	
	public final String getRedirect() {
		return redirect;
	}

	public final void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public final List<Alert> getAlerts() {
		return alerts;
	}

	public final void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}

	public RedirectException(String redirect, List<Alert> alerts) {
		super();
		this.redirect = redirect;
		this.alerts = alerts;
	}
	
}

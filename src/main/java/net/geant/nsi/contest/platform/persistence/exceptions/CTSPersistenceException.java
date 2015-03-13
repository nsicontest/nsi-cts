package net.geant.nsi.contest.platform.persistence.exceptions;

import net.geant.nsi.contest.platform.core.exceptions.CTSException;

public class CTSPersistenceException extends CTSException {

	public CTSPersistenceException() {
	}

	public CTSPersistenceException(String arg0) {
		super(arg0);
	}

	public CTSPersistenceException(Throwable arg0) {
		super(arg0);
	}

	public CTSPersistenceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}

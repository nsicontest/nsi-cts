package net.geant.nsi.contest.platform.web.exceptions;

import net.geant.nsi.contest.platform.core.exceptions.CTSException;

public class BadRequestException extends CTSException {

	public BadRequestException() {
		super();
	}

	public BadRequestException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public BadRequestException(String arg0) {
		super(arg0);
	}

	public BadRequestException(Throwable arg0) {
		super(arg0);
	}

}

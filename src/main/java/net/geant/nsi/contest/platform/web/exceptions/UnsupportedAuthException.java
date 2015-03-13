package net.geant.nsi.contest.platform.web.exceptions;

import net.geant.nsi.contest.platform.core.exceptions.AuthException;

public class UnsupportedAuthException extends AuthException {

	public UnsupportedAuthException() {
		super();
	}

	public UnsupportedAuthException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public UnsupportedAuthException(String arg0) {
		super(arg0);
	}

	public UnsupportedAuthException(Throwable arg0) {
		super(arg0);
	}

}

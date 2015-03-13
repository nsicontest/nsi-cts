package net.geant.nsi.contest.platform.core.exceptions;

public class BadCredentialsException extends AuthException {

	public BadCredentialsException() {
	}

	public BadCredentialsException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public BadCredentialsException(String arg0) {
		super(arg0);
	}

	public BadCredentialsException(Throwable arg0) {
		super(arg0);
	}

}

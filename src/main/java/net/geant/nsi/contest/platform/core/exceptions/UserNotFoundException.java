package net.geant.nsi.contest.platform.core.exceptions;

public class UserNotFoundException extends AuthException {

	public UserNotFoundException() {
	}

	public UserNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public UserNotFoundException(String arg0) {
		super(arg0);
	}

	public UserNotFoundException(Throwable arg0) {
		super(arg0);
	}

}

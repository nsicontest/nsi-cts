package net.geant.nsi.contest.platform.web.data;

public class ErrorInfo {
	private String code;
	private String message;

	public ErrorInfo(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	
	public final String getMessage() {
		return message;
	}

	public final void setMessage(String message) {
		this.message = message;
	}

	public final String getCode() {
		return code;
	}

	public final void setCode(String errorCode) {
		this.code = errorCode;
	}

}

package net.geant.nsi.contest.platform.web.data;

public class Alert {

	public enum AlertType {
		failure("failure"),
		warning("warning"),
		success("success"),
		info("info");
		
		private String value;
		
		AlertType(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
	
	private AlertType type = AlertType.info;
	private String title;
	private String message;
	
	public Alert(String title, String message) {
		this.title = title;
		this.message = message;
	}

	public Alert(AlertType type, String title, String message) {
		this(title, message);
		if(type != null)
			this.type=type;
	}

	public AlertType getType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}
	
	public static Alert success(String message) {
		return new Alert(AlertType.success, "Success!", message);
	}
	
	public static Alert success(String title, String message) {
		return new Alert(AlertType.success, title, message);
	}
	
	public static Alert failure(String message) {
		return new Alert(AlertType.failure, "Failure!", message);
	}
	
	public static Alert failure(String title, String message) {
		return new Alert(AlertType.failure, title, message);
	}
	
	public static Alert warning(String message) {
		return new Alert(AlertType.warning, "Warning!", message);
	}
	
	public static Alert warning(String title, String message) {
		return new Alert(AlertType.warning, title, message);
	}
	
	public static Alert info(String message) {
		return new Alert(AlertType.info, "Info", message);
	}
	
	public static Alert info(String title, String message) {
		return new Alert(AlertType.info, title, message);
	}	
	
}

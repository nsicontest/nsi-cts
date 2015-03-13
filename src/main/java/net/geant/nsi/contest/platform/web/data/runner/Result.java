package net.geant.nsi.contest.platform.web.data.runner;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Result {
    private boolean success;
    
    private String receivedEvent;

	public final boolean isSuccess() {
		return success;
	}

	public final void setSuccess(boolean success) {
		this.success = success;
	}

	public final String getReceivedEvent() {
		return receivedEvent;
	}

	public final void setReceivedEvent(String receivedEvent) {
		this.receivedEvent = receivedEvent;
	}
    
    
}

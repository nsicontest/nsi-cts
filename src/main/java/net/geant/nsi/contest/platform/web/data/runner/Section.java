package net.geant.nsi.contest.platform.web.data.runner;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="section")
@XmlAccessorType(XmlAccessType.FIELD)
public class Section {
	@XmlAttribute
	String id;
	
	@XmlAttribute
	int interval;
	
	@XmlElement(name="operation")
	List<Operation> operations;

	public final String getId() {
		return id;
	}

	public final void setId(String id) {
		this.id = id;
	}

	public final int getInterval() {
		return interval;
	}

	public final void setInterval(int interval) {
		this.interval = interval;
	}

	public final List<Operation> getOperations() {
		return operations;
	}

	public final void setOperations(List<Operation> operations) {
		this.operations = operations;
	}
}

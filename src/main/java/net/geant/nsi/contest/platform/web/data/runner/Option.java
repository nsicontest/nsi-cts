package net.geant.nsi.contest.platform.web.data.runner;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="option")
@XmlAccessorType(XmlAccessType.FIELD)
public class Option {
	@XmlAttribute(required=true)
	private String name;
	
	@XmlAttribute(required=true)
	private String value;

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getValue() {
		return value;
	}

	public final void setValue(String value) {
		this.value = value;
	}
}

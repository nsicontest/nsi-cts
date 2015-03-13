package net.geant.nsi.contest.platform.web.data.runner;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration {
	
	@XmlElement
	List<Option> options;

	public final List<Option> getOptions() {
		return options;
	}

	public final void setOptions(List<Option> options) {
		this.options = options;
	}
}

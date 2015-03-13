package net.geant.nsi.contest.platform.web.data.runner;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="scenario")
@XmlAccessorType(XmlAccessType.FIELD)
public class Scenario {
	@XmlAttribute(required=false)
	private String id;
	
	@XmlElement
	Section section;
	
	@XmlElement
	private Configuration configuration;

	public final String getId() {
		return id;
	}

	public final void setId(String id) {
		this.id = id;
	}

	public final Section getSection() {
		return section;
	}

	public final void setSection(Section section) {
		this.section = section;
	}

	public final Configuration getConfiguration() {
		return configuration;
	}

	public final void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}

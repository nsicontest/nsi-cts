package net.geant.nsi.contest.platform.data;

import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames={"templateId"})})
public class TestCaseTemplate {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	UUID templateId;
	
	String name;
	
	private AgentType type;
	
	boolean certification;
	
	@Lob 
	@Basic(fetch=FetchType.LAZY)
	String template;
	
	public TestCaseTemplate() {
		templateId = UUID.randomUUID();
	}

	public TestCaseTemplate(String name, AgentType type) {
		this();
		this.name = name;
		this.type = type;
	}
	
	public TestCaseTemplate(String name, AgentType type, boolean certification, String template) {
		this(name, type);
		this.certification = certification;
		this.template = template;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public UUID getTemplateId() {
		return templateId;
	}

	public void setTemplateId(UUID templateId) {
		this.templateId = templateId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public final AgentType getType() {
		return type;
	}

	public final void setType(AgentType type) {
		this.type = type;
	}

	public final boolean isCertification() {
		return certification;
	}

	public final void setCertification(boolean certification) {
		this.certification = certification;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((templateId == null) ? 0 : templateId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestCaseTemplate other = (TestCaseTemplate) obj;
		if (templateId == null) {
			if (other.templateId != null)
				return false;
		} else if (!templateId.equals(other.templateId))
			return false;
		return true;
	}
	
	
	
}

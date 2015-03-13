package net.geant.nsi.contest.platform.web.data.convert;

import net.geant.nsi.contest.platform.data.TestCaseTemplate;
import net.geant.nsi.contest.platform.web.data.forms.TemplateForm;

public class TemplateConverter extends AbstractConvertType<TestCaseTemplate, TemplateForm> {

	public TemplateConverter(Class<TestCaseTemplate> type) {
		super(type);
	}

	@Override
	public TemplateForm convert(TestCaseTemplate template) {
		return new TemplateForm(template.getTemplateId(), template.getName(), template.getType(), template.isCertification());
	}

	@Override
	public TemplateForm convertDetailed(TestCaseTemplate template) {
		return new TemplateForm(template.getTemplateId(), template.getName(), template.getType(), template.isCertification(), template.getTemplate());
	}

}

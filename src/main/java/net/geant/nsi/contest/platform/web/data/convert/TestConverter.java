package net.geant.nsi.contest.platform.web.data.convert;

import net.geant.nsi.contest.platform.web.data.Test;
import net.geant.nsi.contest.platform.web.data.forms.TemplateForm;

public class TestConverter extends AbstractConvertType<net.geant.nsi.contest.platform.data.Test, Test> {

	public TestConverter(Class<net.geant.nsi.contest.platform.data.Test> type) {
		super(type);
	}

	@Override
	public Test convert(net.geant.nsi.contest.platform.data.Test test) {
		Test t = new Test(test.getTestId(), (TemplateForm)converter.convert(test.getTemplate()));
		t.setCreatedAt(test.getCreatedAt());
		t.setStatus(test.getStatus());
		t.setResultStatus(test.getResultStatus());
		t.setErrorMessage(test.getErrorMessage());
		return t;
	}

	@Override
	public Test convertDetailed(net.geant.nsi.contest.platform.data.Test test) {
		return convert(test);
	}


}

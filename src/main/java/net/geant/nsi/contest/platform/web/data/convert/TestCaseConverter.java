package net.geant.nsi.contest.platform.web.data.convert;

import net.geant.nsi.contest.platform.web.data.TestCase;


public class TestCaseConverter extends AbstractConvertType<net.geant.nsi.contest.platform.data.TestCase, TestCase> {

	public TestCaseConverter(Class<net.geant.nsi.contest.platform.data.TestCase> type) {
		super(type);
	}

	@Override
	public TestCase convert(
			net.geant.nsi.contest.platform.data.TestCase testCase) {
		TestCase t = new TestCase(testCase.getTestCaseId(), 
				testCase.getCreatedAt(), 
				testCase.getStatus(), testCase.getResultStatus());
		t.setCertification(testCase.getCertification());
		t.setTestsCount(testCase.getTests().size());
		return t;
	}

	@Override
	public TestCase convertDetailed(
			net.geant.nsi.contest.platform.data.TestCase testCase) {
		//Change this if you need more details
		return convert(testCase);
	}


}

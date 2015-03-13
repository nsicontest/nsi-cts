package net.geant.nsi.contest.platform.core;

import net.geant.nsi.contest.platform.core.exceptions.TestException;
import net.geant.nsi.contest.platform.data.Test;
import net.geant.nsi.contest.platform.data.TestCase;

public interface TestCaseListener {
	void update(TestCase testCase);
	void update(Test test);
	void update(Test test, TestException testException);
}

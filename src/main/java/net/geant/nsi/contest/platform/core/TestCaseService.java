package net.geant.nsi.contest.platform.core;

import java.util.List;
import java.util.UUID;

import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.data.AgentType;
import net.geant.nsi.contest.platform.data.Project;
import net.geant.nsi.contest.platform.data.Test;
import net.geant.nsi.contest.platform.data.TestCase;
import net.geant.nsi.contest.platform.data.TestCaseTemplate;

public interface TestCaseService {
	TestCase createFor(Project project, List<TestCaseTemplate> templates) throws ResourceNotFoundException;
	TestCase createCertificatedFor(Project project, AgentType type) throws ResourceNotFoundException;
	void perform(UUID testCaseId) throws ResourceNotFoundException;
	List<TestCase> getAll(Project project) throws ResourceNotFoundException;
	TestCase findBy(UUID testCaseId) throws ResourceNotFoundException;
	Test findTest(UUID testId) throws ResourceNotFoundException;
	List<Test> getTestsFor(UUID testCaseId) throws ResourceNotFoundException;
	
}

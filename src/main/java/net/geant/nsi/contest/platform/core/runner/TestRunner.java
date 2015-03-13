package net.geant.nsi.contest.platform.core.runner;

import java.util.UUID;

import net.geant.nsi.contest.platform.core.runner.data.JobInfo;
import net.geant.nsi.contest.platform.core.runner.exceptions.JobRunnerException;

public interface TestRunner {
	JobInfo start(UUID testId, String template) throws JobRunnerException;
	JobInfo get(UUID jobId) throws JobRunnerException;
}

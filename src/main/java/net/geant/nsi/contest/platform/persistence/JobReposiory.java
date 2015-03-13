package net.geant.nsi.contest.platform.persistence;

import java.util.List;
import java.util.UUID;

import net.geant.nsi.contest.platform.data.Job;
import net.geant.nsi.contest.platform.data.Test;
import net.geant.nsi.contest.platform.data.TestCase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobReposiory extends JpaRepository<Job, Long> {
	Job findByJobId(UUID jobId);
	Job findByTest(Test test);
	List<Job> findByTestCase(TestCase testCase);
}

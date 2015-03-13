package net.geant.nsi.contest.platform.persistence;

import java.util.List;
import java.util.UUID;

import net.geant.nsi.contest.platform.data.Project;
import net.geant.nsi.contest.platform.data.TestCase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
	TestCase findByTestCaseId(UUID testCaseId);
	List<TestCase> findByProject(Project project);
}

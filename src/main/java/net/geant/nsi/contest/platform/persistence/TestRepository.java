package net.geant.nsi.contest.platform.persistence;

import java.util.List;
import java.util.UUID;

import net.geant.nsi.contest.platform.data.Test;
import net.geant.nsi.contest.platform.data.TestCase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
	List<Test> findByTestCase(TestCase testCase);
	Test findByTestId(UUID testId);
}

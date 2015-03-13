package net.geant.nsi.contest.platform.persistence;

import java.util.List;
import java.util.UUID;

import net.geant.nsi.contest.platform.data.TestCaseTemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseTemplateRepository extends JpaRepository<TestCaseTemplate, Long> {
	TestCaseTemplate findByTemplateId(UUID templateId);
	List<TestCaseTemplate> findBy(List<UUID> templateIds);
}

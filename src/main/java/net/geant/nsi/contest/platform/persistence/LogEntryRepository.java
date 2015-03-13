package net.geant.nsi.contest.platform.persistence;

import java.util.List;

import net.geant.nsi.contest.platform.data.LogEntry;
import net.geant.nsi.contest.platform.data.Test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
	List<LogEntry> findByTest(Test test);
}

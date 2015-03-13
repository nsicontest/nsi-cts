package net.geant.nsi.contest.platform.persistence;

import java.util.UUID;

import net.geant.nsi.contest.platform.data.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
	User findByUserId(UUID userId);
}

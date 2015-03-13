package net.geant.nsi.contest.platform.persistence;

import net.geant.nsi.contest.platform.data.UserRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<UserRole, Long> {
	UserRole findByName(String name);
}

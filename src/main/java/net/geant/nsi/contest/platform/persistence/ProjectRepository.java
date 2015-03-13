package net.geant.nsi.contest.platform.persistence;

import java.util.List;
import java.util.UUID;

import net.geant.nsi.contest.platform.data.Project;
import net.geant.nsi.contest.platform.data.User;
import net.geant.nsi.contest.platform.data.UserAcl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
	Project findByKey(UUID key);
	
	@Query("from Project p join p.userAcls acl join acl.user u where u.userId=?1")
	List<Project> getByUser(UUID userId);
	
	@Query("from UserAcl acl join acl.project p join acl.user u where u.userId=?2 and p.key=?1")
	UserAcl getUserAcl(UUID key, UUID userId);
	
	@Query("from UserAcl acl join acl.project p where p.key=?1")
	List<UserAcl> getUserAcls(UUID key);
	
	@Query("from Project p join p.userAcls acl join acl.user u where u=?1")
	List<Project> getByUser(User user);
}

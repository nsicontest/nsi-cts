package net.geant.nsi.contest.platform.persistence;

import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import net.geant.nsi.contest.platform.data.Project;
import net.geant.nsi.contest.platform.data.User;
import net.geant.nsi.contest.platform.data.UserAcl;
import net.geant.nsi.contest.platform.data.UserRole;
import net.geant.nsi.contest.platform.persistence.exceptions.CTSPersistenceException;

@Service
public class ProjectDAO extends GenericDAO<Project> {
	
	public ProjectDAO() {
		setClass(Project.class);
	}
	
	public Project findByKey(UUID key) throws CTSPersistenceException {
		if(key == null)
			throw new IllegalArgumentException("Email is empty.");
		
		try {
			Query query = em.createQuery("from " + clazz.getName() + " project where project.key=:key");
			query.setParameter("key", key);
			return (Project)query.getSingleResult();
		} catch(NoResultException ex) {
			return null;
		} catch(NonUniqueResultException ex) {
			throw new CTSPersistenceException("Ambiguous number of projects '" + key + "'.");
		} catch(Exception ex) {
			throw new CTSPersistenceException("Unable to query project '" + key + "'. ", ex);
		}
	}
	
	public Project findByKey(String key) throws CTSPersistenceException {
		return findByKey((key != null ? UUID.fromString(key) : null));
	}
	
	@SuppressWarnings("unchecked")
	public List<Project> getByUser(UUID userId) throws CTSPersistenceException {
		if(userId == null)
			throw new IllegalArgumentException("User id is null");
		
		try {
			Query query = em.createQuery("select p from " + clazz.getName() + " p join p.userAcls acl join acl.user u where u.userId=:userId");
			query.setParameter("userId", userId);
			return query.getResultList();
		} catch (Exception ex) {
			throw new CTSPersistenceException("Unable to get projects for user id=" + userId, ex);
		}
	}

	public UserAcl getUserAcl(UUID key, UUID userId) throws CTSPersistenceException {
		if(userId == null) 
			throw new IllegalArgumentException("User id is null");
		if(key == null)
			throw new IllegalArgumentException("Project key is null");
		
		try {
			Query query = em.createQuery("select acl from " + UserAcl.class.getName() + " acl join acl.project p join acl.user u where u.userId=:userId and p.key=:key");
			query.setParameter("userId", userId);
			query.setParameter("key", key);
			return (UserAcl)query.getSingleResult();
		} catch(NoResultException ex) {
			return null;
		} catch(NonUniqueResultException ex) {
			throw new CTSPersistenceException("Ambiguous number of acls for project '" + key + "' and user '"+userId+"'.", ex);
		} catch (Exception ex) {
			throw new CTSPersistenceException("Unable to get acl for user id=" + userId + " in project " + key, ex);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public List<UserAcl> getUserAcls(UUID key) throws CTSPersistenceException {
		if(key == null)
			throw new IllegalArgumentException("Project key is null");
		
		try {
			Query query = em.createQuery("select acl from " + UserAcl.class.getName() + " acl join acl.project p where p.key=:key");
			query.setParameter("key", key);
			return (List<UserAcl>)query.getResultList();
		} catch (Exception ex) {
			throw new CTSPersistenceException("Unable to get acls for project " + key, ex);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public final List<Project> getByUser(User user) throws CTSPersistenceException {
		if(user == null)
			throw new IllegalArgumentException("User is null");
		
		try {
			Query query = em.createQuery("select p from " + clazz.getName() + " p join p.userAcls acl join acl.user u where u=:user");
			query.setParameter("user", user);
			return query.getResultList();
		} catch (Exception ex) {
			throw new CTSPersistenceException("Unable to get projects for user id=" + user.getUserId(), ex);
		}		
	}

}

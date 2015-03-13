package net.geant.nsi.contest.platform.persistence;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.geant.nsi.contest.platform.data.UserRole;
import net.geant.nsi.contest.platform.persistence.exceptions.CTSPersistenceException;

@Service
public class RoleDAO extends GenericDAO<UserRole> {
	
	public RoleDAO() {
		setClass(UserRole.class);
	}
	
	@Transactional(readOnly=true)
	public UserRole findByName(String name) throws CTSPersistenceException {
		if(name == null)
			throw new IllegalArgumentException("Role name is empty.");
		
		try {
			Query query = em.createQuery("from " + clazz.getName() + " role where role.name=:name");
			query.setParameter("name", name);
			return (UserRole)query.getSingleResult();
		} catch(NoResultException ex) {
			return null;
		} catch(NonUniqueResultException ex) {
			throw new CTSPersistenceException("Ambiguous number of roles '" + name + "'.");
		} catch(Exception ex) {
			throw new CTSPersistenceException("Unable to query for role '" + name + "'. ", ex);
		}
	}
	
}

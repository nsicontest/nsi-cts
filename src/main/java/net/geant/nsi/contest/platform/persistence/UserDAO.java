package net.geant.nsi.contest.platform.persistence;


import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import net.geant.nsi.contest.platform.data.User;
import net.geant.nsi.contest.platform.persistence.exceptions.CTSPersistenceException;

import org.springframework.stereotype.Service;


@Service
public class UserDAO extends GenericDAO<User> {

	public UserDAO() {
		setClass(User.class);
	}

	public User findByEmail(String email) throws CTSPersistenceException {
		if(email == null)
			throw new IllegalArgumentException("Email is empty.");
		try {
			Query query = em.createQuery("from " + clazz.getName() + " user where user.email=:email");
			query.setParameter("email", email);
			return (User)query.getSingleResult();
		} catch(NoResultException ex) {
			return null;
		} catch(NonUniqueResultException ex) {
			throw new CTSPersistenceException("Ambiguous number of users '" + email + "'.");
		} catch(Exception ex) {
			throw new CTSPersistenceException("Unable to check for user '" + email + "'. ", ex);
		}
	}
	
	public User findByUserId(UUID userId) throws CTSPersistenceException {
		if(userId == null)
			throw new IllegalArgumentException("UserId is empty.");
		try {
			Query query = em.createQuery("from " + clazz.getName() + " user where user.userId=:userId");
			query.setParameter("userId", userId);
			return (User)query.getSingleResult();
		} catch(NoResultException ex) {
			return null;
		} catch(NonUniqueResultException ex) {
			throw new CTSPersistenceException("Ambiguous number of users with userId='" + userId + "'.");
		} catch(Exception ex) {
			throw new CTSPersistenceException("Unable to check for user with userId='" + userId + "'. ", ex);
		}
	}
	
}

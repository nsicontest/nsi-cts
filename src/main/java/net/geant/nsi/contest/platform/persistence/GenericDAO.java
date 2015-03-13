package net.geant.nsi.contest.platform.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class GenericDAO<T> {
	
	Class<T> clazz;
	
	@PersistenceContext
	protected EntityManager em;

	protected EntityManager getEntityManager() {
		return em;
	}

	protected void setClass(Class<T> cls) {
		clazz = cls;
	}

	public T save(T entity) {
		return em.merge(entity);
//		T result = em.merge(entity);
//		em.flush();
//		return result;
	}
	
	public T get(Long id) {
		if(id == null)
			throw new IllegalArgumentException("Id is null");
		return em.find(clazz, id);
	}
	
	public void delete(Long id) {
		if(id == null)
			throw new IllegalArgumentException("Id is null");

		T entity = get(id);
		if(entity != null) {
			em.remove(entity);
			em.flush();
		}
	}
		
	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		return em.createQuery("from " + clazz.getName()).getResultList();
	}
	
	public long count() {
		return (Long)em.createQuery("select count(en.id) from " + clazz.getName() + " en").getSingleResult();
	}
}

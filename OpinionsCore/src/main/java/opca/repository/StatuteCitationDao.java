package opca.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import opca.model.StatuteCitation;
import opca.model.StatuteKey;

@Transactional
@Repository
public class StatuteCitationDao implements IStatuteCitationDao {
	@PersistenceContext	
	private EntityManager entityManager;	
	@Override
	public StatuteCitation getStatuteCitationById(StatuteKey statuteKey) {
		return entityManager.find(StatuteCitation.class, statuteKey);
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<StatuteCitation> getAllStatuteCitations() {
		String hql = "FROM StatuteCitation as atcl ORDER BY atcl.statuteCitationId";
		return (List<StatuteCitation>) entityManager.createQuery(hql).getResultList();
	}	
	@Override
	public void addStatuteCitation(StatuteCitation statuteCitation) {
		entityManager.persist(statuteCitation);
	}
	@Override
	public StatuteCitation mergeStatuteCitation(StatuteCitation statuteCitation) {
		return entityManager.merge(statuteCitation);
	}
	@Override
	public void deleteStatuteCitation(StatuteKey statuteKey) {
		entityManager.remove(getStatuteCitationById(statuteKey));
	}
	@Override
	public boolean statuteCitationExists(String title, String category) {
		String hql = "FROM StatuteCitation as atcl WHERE atcl.title = ? and atcl.category = ?";
		int count = entityManager.createQuery(hql).setParameter(1, title)
		              .setParameter(2, category).getResultList().size();
		return count > 0 ? true : false;
	}
} 

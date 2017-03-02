package persistence.controllers;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import persistence.controllers.exceptions.NonexistentEntityException;
import persistence.controllers.exceptions.RollbackFailureException;
import persistence.entities.Newsfeed;

/**
 *
 * @author Erika Bourque
 */
@Named
@RequestScoped
public class NewsfeedJpaController implements Serializable {

    @Resource
    private UserTransaction utx;

    @PersistenceContext
    private EntityManager em;

    public NewsfeedJpaController() {
    }

    public void create(Newsfeed newsfeed) throws RollbackFailureException, Exception {
        try {
            utx.begin();
            em.persist(newsfeed);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        }
    }

    public void edit(Newsfeed newsfeed) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            newsfeed = em.merge(newsfeed);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = newsfeed.getId();
                if (findNewsfeed(id) == null) {
                    throw new NonexistentEntityException("The newsfeed with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Newsfeed newsfeed;
            try {
                newsfeed = em.getReference(Newsfeed.class, id);
                newsfeed.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The newsfeed with id " + id + " no longer exists.", enfe);
            }
            em.remove(newsfeed);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        }
    }

    public List<Newsfeed> findNewsfeedEntities() {
        return findNewsfeedEntities(true, -1, -1);
    }

    public List<Newsfeed> findNewsfeedEntities(int maxResults, int firstResult) {
        return findNewsfeedEntities(false, maxResults, firstResult);
    }

    private List<Newsfeed> findNewsfeedEntities(boolean all, int maxResults, int firstResult) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Newsfeed.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public Newsfeed findNewsfeed(Integer id) {
        return em.find(Newsfeed.class, id);
    }

    public int getNewsfeedCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Newsfeed> rt = cq.from(Newsfeed.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}

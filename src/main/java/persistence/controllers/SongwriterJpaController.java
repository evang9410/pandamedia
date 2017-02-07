/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import persistence.Track;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import persistence.Songwriter;
import persistence.controllers.exceptions.IllegalOrphanException;
import persistence.controllers.exceptions.NonexistentEntityException;
import persistence.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Evang
 */
public class SongwriterJpaController implements Serializable {

    public SongwriterJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Songwriter songwriter) throws RollbackFailureException, Exception {
        if (songwriter.getTrackCollection() == null) {
            songwriter.setTrackCollection(new ArrayList<Track>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Track> attachedTrackCollection = new ArrayList<Track>();
            for (Track trackCollectionTrackToAttach : songwriter.getTrackCollection()) {
                trackCollectionTrackToAttach = em.getReference(trackCollectionTrackToAttach.getClass(), trackCollectionTrackToAttach.getId());
                attachedTrackCollection.add(trackCollectionTrackToAttach);
            }
            songwriter.setTrackCollection(attachedTrackCollection);
            em.persist(songwriter);
            for (Track trackCollectionTrack : songwriter.getTrackCollection()) {
                Songwriter oldSongwriterIdOfTrackCollectionTrack = trackCollectionTrack.getSongwriterId();
                trackCollectionTrack.setSongwriterId(songwriter);
                trackCollectionTrack = em.merge(trackCollectionTrack);
                if (oldSongwriterIdOfTrackCollectionTrack != null) {
                    oldSongwriterIdOfTrackCollectionTrack.getTrackCollection().remove(trackCollectionTrack);
                    oldSongwriterIdOfTrackCollectionTrack = em.merge(oldSongwriterIdOfTrackCollectionTrack);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Songwriter songwriter) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Songwriter persistentSongwriter = em.find(Songwriter.class, songwriter.getId());
            Collection<Track> trackCollectionOld = persistentSongwriter.getTrackCollection();
            Collection<Track> trackCollectionNew = songwriter.getTrackCollection();
            List<String> illegalOrphanMessages = null;
            for (Track trackCollectionOldTrack : trackCollectionOld) {
                if (!trackCollectionNew.contains(trackCollectionOldTrack)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Track " + trackCollectionOldTrack + " since its songwriterId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Track> attachedTrackCollectionNew = new ArrayList<Track>();
            for (Track trackCollectionNewTrackToAttach : trackCollectionNew) {
                trackCollectionNewTrackToAttach = em.getReference(trackCollectionNewTrackToAttach.getClass(), trackCollectionNewTrackToAttach.getId());
                attachedTrackCollectionNew.add(trackCollectionNewTrackToAttach);
            }
            trackCollectionNew = attachedTrackCollectionNew;
            songwriter.setTrackCollection(trackCollectionNew);
            songwriter = em.merge(songwriter);
            for (Track trackCollectionNewTrack : trackCollectionNew) {
                if (!trackCollectionOld.contains(trackCollectionNewTrack)) {
                    Songwriter oldSongwriterIdOfTrackCollectionNewTrack = trackCollectionNewTrack.getSongwriterId();
                    trackCollectionNewTrack.setSongwriterId(songwriter);
                    trackCollectionNewTrack = em.merge(trackCollectionNewTrack);
                    if (oldSongwriterIdOfTrackCollectionNewTrack != null && !oldSongwriterIdOfTrackCollectionNewTrack.equals(songwriter)) {
                        oldSongwriterIdOfTrackCollectionNewTrack.getTrackCollection().remove(trackCollectionNewTrack);
                        oldSongwriterIdOfTrackCollectionNewTrack = em.merge(oldSongwriterIdOfTrackCollectionNewTrack);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = songwriter.getId();
                if (findSongwriter(id) == null) {
                    throw new NonexistentEntityException("The songwriter with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Songwriter songwriter;
            try {
                songwriter = em.getReference(Songwriter.class, id);
                songwriter.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The songwriter with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Track> trackCollectionOrphanCheck = songwriter.getTrackCollection();
            for (Track trackCollectionOrphanCheckTrack : trackCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Songwriter (" + songwriter + ") cannot be destroyed since the Track " + trackCollectionOrphanCheckTrack + " in its trackCollection field has a non-nullable songwriterId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(songwriter);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Songwriter> findSongwriterEntities() {
        return findSongwriterEntities(true, -1, -1);
    }

    public List<Songwriter> findSongwriterEntities(int maxResults, int firstResult) {
        return findSongwriterEntities(false, maxResults, firstResult);
    }

    private List<Songwriter> findSongwriterEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Songwriter.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Songwriter findSongwriter(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Songwriter.class, id);
        } finally {
            em.close();
        }
    }

    public int getSongwriterCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Songwriter> rt = cq.from(Songwriter.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

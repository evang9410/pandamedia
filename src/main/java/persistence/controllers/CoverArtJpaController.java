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
import persistence.entities.Track;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import persistence.controllers.exceptions.IllegalOrphanException;
import persistence.controllers.exceptions.NonexistentEntityException;
import persistence.controllers.exceptions.RollbackFailureException;
import persistence.entities.CoverArt;

/**
 *
 * @author Evang
 */
public class CoverArtJpaController implements Serializable {

    public CoverArtJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CoverArt coverArt) throws RollbackFailureException, Exception {
        if (coverArt.getTrackList() == null) {
            coverArt.setTrackList(new ArrayList<Track>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Track> attachedTrackList = new ArrayList<Track>();
            for (Track trackListTrackToAttach : coverArt.getTrackList()) {
                trackListTrackToAttach = em.getReference(trackListTrackToAttach.getClass(), trackListTrackToAttach.getId());
                attachedTrackList.add(trackListTrackToAttach);
            }
            coverArt.setTrackList(attachedTrackList);
            em.persist(coverArt);
            for (Track trackListTrack : coverArt.getTrackList()) {
                CoverArt oldCoverArtIdOfTrackListTrack = trackListTrack.getCoverArtId();
                trackListTrack.setCoverArtId(coverArt);
                trackListTrack = em.merge(trackListTrack);
                if (oldCoverArtIdOfTrackListTrack != null) {
                    oldCoverArtIdOfTrackListTrack.getTrackList().remove(trackListTrack);
                    oldCoverArtIdOfTrackListTrack = em.merge(oldCoverArtIdOfTrackListTrack);
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

    public void edit(CoverArt coverArt) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            CoverArt persistentCoverArt = em.find(CoverArt.class, coverArt.getId());
            List<Track> trackListOld = persistentCoverArt.getTrackList();
            List<Track> trackListNew = coverArt.getTrackList();
            List<String> illegalOrphanMessages = null;
            for (Track trackListOldTrack : trackListOld) {
                if (!trackListNew.contains(trackListOldTrack)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Track " + trackListOldTrack + " since its coverArtId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Track> attachedTrackListNew = new ArrayList<Track>();
            for (Track trackListNewTrackToAttach : trackListNew) {
                trackListNewTrackToAttach = em.getReference(trackListNewTrackToAttach.getClass(), trackListNewTrackToAttach.getId());
                attachedTrackListNew.add(trackListNewTrackToAttach);
            }
            trackListNew = attachedTrackListNew;
            coverArt.setTrackList(trackListNew);
            coverArt = em.merge(coverArt);
            for (Track trackListNewTrack : trackListNew) {
                if (!trackListOld.contains(trackListNewTrack)) {
                    CoverArt oldCoverArtIdOfTrackListNewTrack = trackListNewTrack.getCoverArtId();
                    trackListNewTrack.setCoverArtId(coverArt);
                    trackListNewTrack = em.merge(trackListNewTrack);
                    if (oldCoverArtIdOfTrackListNewTrack != null && !oldCoverArtIdOfTrackListNewTrack.equals(coverArt)) {
                        oldCoverArtIdOfTrackListNewTrack.getTrackList().remove(trackListNewTrack);
                        oldCoverArtIdOfTrackListNewTrack = em.merge(oldCoverArtIdOfTrackListNewTrack);
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
                Integer id = coverArt.getId();
                if (findCoverArt(id) == null) {
                    throw new NonexistentEntityException("The coverArt with id " + id + " no longer exists.");
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
            CoverArt coverArt;
            try {
                coverArt = em.getReference(CoverArt.class, id);
                coverArt.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The coverArt with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Track> trackListOrphanCheck = coverArt.getTrackList();
            for (Track trackListOrphanCheckTrack : trackListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This CoverArt (" + coverArt + ") cannot be destroyed since the Track " + trackListOrphanCheckTrack + " in its trackList field has a non-nullable coverArtId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(coverArt);
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

    public List<CoverArt> findCoverArtEntities() {
        return findCoverArtEntities(true, -1, -1);
    }

    public List<CoverArt> findCoverArtEntities(int maxResults, int firstResult) {
        return findCoverArtEntities(false, maxResults, firstResult);
    }

    private List<CoverArt> findCoverArtEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CoverArt.class));
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

    public CoverArt findCoverArt(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CoverArt.class, id);
        } finally {
            em.close();
        }
    }

    public int getCoverArtCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CoverArt> rt = cq.from(CoverArt.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

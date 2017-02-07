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
import persistence.Album;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import persistence.RecordingLabel;
import persistence.controllers.exceptions.IllegalOrphanException;
import persistence.controllers.exceptions.NonexistentEntityException;
import persistence.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Evang
 */
public class RecordingLabelJpaController implements Serializable {

    public RecordingLabelJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RecordingLabel recordingLabel) throws RollbackFailureException, Exception {
        if (recordingLabel.getAlbumCollection() == null) {
            recordingLabel.setAlbumCollection(new ArrayList<Album>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Album> attachedAlbumCollection = new ArrayList<Album>();
            for (Album albumCollectionAlbumToAttach : recordingLabel.getAlbumCollection()) {
                albumCollectionAlbumToAttach = em.getReference(albumCollectionAlbumToAttach.getClass(), albumCollectionAlbumToAttach.getId());
                attachedAlbumCollection.add(albumCollectionAlbumToAttach);
            }
            recordingLabel.setAlbumCollection(attachedAlbumCollection);
            em.persist(recordingLabel);
            for (Album albumCollectionAlbum : recordingLabel.getAlbumCollection()) {
                RecordingLabel oldRecordingLabelIdOfAlbumCollectionAlbum = albumCollectionAlbum.getRecordingLabelId();
                albumCollectionAlbum.setRecordingLabelId(recordingLabel);
                albumCollectionAlbum = em.merge(albumCollectionAlbum);
                if (oldRecordingLabelIdOfAlbumCollectionAlbum != null) {
                    oldRecordingLabelIdOfAlbumCollectionAlbum.getAlbumCollection().remove(albumCollectionAlbum);
                    oldRecordingLabelIdOfAlbumCollectionAlbum = em.merge(oldRecordingLabelIdOfAlbumCollectionAlbum);
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

    public void edit(RecordingLabel recordingLabel) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            RecordingLabel persistentRecordingLabel = em.find(RecordingLabel.class, recordingLabel.getId());
            Collection<Album> albumCollectionOld = persistentRecordingLabel.getAlbumCollection();
            Collection<Album> albumCollectionNew = recordingLabel.getAlbumCollection();
            List<String> illegalOrphanMessages = null;
            for (Album albumCollectionOldAlbum : albumCollectionOld) {
                if (!albumCollectionNew.contains(albumCollectionOldAlbum)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Album " + albumCollectionOldAlbum + " since its recordingLabelId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Album> attachedAlbumCollectionNew = new ArrayList<Album>();
            for (Album albumCollectionNewAlbumToAttach : albumCollectionNew) {
                albumCollectionNewAlbumToAttach = em.getReference(albumCollectionNewAlbumToAttach.getClass(), albumCollectionNewAlbumToAttach.getId());
                attachedAlbumCollectionNew.add(albumCollectionNewAlbumToAttach);
            }
            albumCollectionNew = attachedAlbumCollectionNew;
            recordingLabel.setAlbumCollection(albumCollectionNew);
            recordingLabel = em.merge(recordingLabel);
            for (Album albumCollectionNewAlbum : albumCollectionNew) {
                if (!albumCollectionOld.contains(albumCollectionNewAlbum)) {
                    RecordingLabel oldRecordingLabelIdOfAlbumCollectionNewAlbum = albumCollectionNewAlbum.getRecordingLabelId();
                    albumCollectionNewAlbum.setRecordingLabelId(recordingLabel);
                    albumCollectionNewAlbum = em.merge(albumCollectionNewAlbum);
                    if (oldRecordingLabelIdOfAlbumCollectionNewAlbum != null && !oldRecordingLabelIdOfAlbumCollectionNewAlbum.equals(recordingLabel)) {
                        oldRecordingLabelIdOfAlbumCollectionNewAlbum.getAlbumCollection().remove(albumCollectionNewAlbum);
                        oldRecordingLabelIdOfAlbumCollectionNewAlbum = em.merge(oldRecordingLabelIdOfAlbumCollectionNewAlbum);
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
                Integer id = recordingLabel.getId();
                if (findRecordingLabel(id) == null) {
                    throw new NonexistentEntityException("The recordingLabel with id " + id + " no longer exists.");
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
            RecordingLabel recordingLabel;
            try {
                recordingLabel = em.getReference(RecordingLabel.class, id);
                recordingLabel.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The recordingLabel with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Album> albumCollectionOrphanCheck = recordingLabel.getAlbumCollection();
            for (Album albumCollectionOrphanCheckAlbum : albumCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This RecordingLabel (" + recordingLabel + ") cannot be destroyed since the Album " + albumCollectionOrphanCheckAlbum + " in its albumCollection field has a non-nullable recordingLabelId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(recordingLabel);
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

    public List<RecordingLabel> findRecordingLabelEntities() {
        return findRecordingLabelEntities(true, -1, -1);
    }

    public List<RecordingLabel> findRecordingLabelEntities(int maxResults, int firstResult) {
        return findRecordingLabelEntities(false, maxResults, firstResult);
    }

    private List<RecordingLabel> findRecordingLabelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RecordingLabel.class));
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

    public RecordingLabel findRecordingLabel(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RecordingLabel.class, id);
        } finally {
            em.close();
        }
    }

    public int getRecordingLabelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RecordingLabel> rt = cq.from(RecordingLabel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

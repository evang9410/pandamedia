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
import persistence.entities.Album;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import persistence.controllers.exceptions.IllegalOrphanException;
import persistence.controllers.exceptions.NonexistentEntityException;
import persistence.controllers.exceptions.RollbackFailureException;
import persistence.entities.RecordingLabel;

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
        if (recordingLabel.getAlbumList() == null) {
            recordingLabel.setAlbumList(new ArrayList<Album>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Album> attachedAlbumList = new ArrayList<Album>();
            for (Album albumListAlbumToAttach : recordingLabel.getAlbumList()) {
                albumListAlbumToAttach = em.getReference(albumListAlbumToAttach.getClass(), albumListAlbumToAttach.getId());
                attachedAlbumList.add(albumListAlbumToAttach);
            }
            recordingLabel.setAlbumList(attachedAlbumList);
            em.persist(recordingLabel);
            for (Album albumListAlbum : recordingLabel.getAlbumList()) {
                RecordingLabel oldRecordingLabelIdOfAlbumListAlbum = albumListAlbum.getRecordingLabelId();
                albumListAlbum.setRecordingLabelId(recordingLabel);
                albumListAlbum = em.merge(albumListAlbum);
                if (oldRecordingLabelIdOfAlbumListAlbum != null) {
                    oldRecordingLabelIdOfAlbumListAlbum.getAlbumList().remove(albumListAlbum);
                    oldRecordingLabelIdOfAlbumListAlbum = em.merge(oldRecordingLabelIdOfAlbumListAlbum);
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
            List<Album> albumListOld = persistentRecordingLabel.getAlbumList();
            List<Album> albumListNew = recordingLabel.getAlbumList();
            List<String> illegalOrphanMessages = null;
            for (Album albumListOldAlbum : albumListOld) {
                if (!albumListNew.contains(albumListOldAlbum)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Album " + albumListOldAlbum + " since its recordingLabelId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Album> attachedAlbumListNew = new ArrayList<Album>();
            for (Album albumListNewAlbumToAttach : albumListNew) {
                albumListNewAlbumToAttach = em.getReference(albumListNewAlbumToAttach.getClass(), albumListNewAlbumToAttach.getId());
                attachedAlbumListNew.add(albumListNewAlbumToAttach);
            }
            albumListNew = attachedAlbumListNew;
            recordingLabel.setAlbumList(albumListNew);
            recordingLabel = em.merge(recordingLabel);
            for (Album albumListNewAlbum : albumListNew) {
                if (!albumListOld.contains(albumListNewAlbum)) {
                    RecordingLabel oldRecordingLabelIdOfAlbumListNewAlbum = albumListNewAlbum.getRecordingLabelId();
                    albumListNewAlbum.setRecordingLabelId(recordingLabel);
                    albumListNewAlbum = em.merge(albumListNewAlbum);
                    if (oldRecordingLabelIdOfAlbumListNewAlbum != null && !oldRecordingLabelIdOfAlbumListNewAlbum.equals(recordingLabel)) {
                        oldRecordingLabelIdOfAlbumListNewAlbum.getAlbumList().remove(albumListNewAlbum);
                        oldRecordingLabelIdOfAlbumListNewAlbum = em.merge(oldRecordingLabelIdOfAlbumListNewAlbum);
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
            List<Album> albumListOrphanCheck = recordingLabel.getAlbumList();
            for (Album albumListOrphanCheckAlbum : albumListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This RecordingLabel (" + recordingLabel + ") cannot be destroyed since the Album " + albumListOrphanCheckAlbum + " in its albumList field has a non-nullable recordingLabelId field.");
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

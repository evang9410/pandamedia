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
import persistence.Artist;
import persistence.Genre;
import persistence.RecordingLabel;
import persistence.Track;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import persistence.Album;
import persistence.controllers.exceptions.IllegalOrphanException;
import persistence.controllers.exceptions.NonexistentEntityException;
import persistence.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Evang
 */
@Named
@SessionScoped
public class AlbumJpaController implements Serializable {

    //   public AlbumJpaController(){} // default constructor.
    @Resource
    private UserTransaction utx;
    @PersistenceContext
    private EntityManager em;

    public void create(Album album) throws RollbackFailureException, Exception {
        if (album.getTrackCollection() == null) {
            album.setTrackCollection(new ArrayList<Track>());
        }
        try {
            utx.begin();
            Artist artistId = album.getArtistId();
            if (artistId != null) {
                artistId = em.getReference(artistId.getClass(), artistId.getId());
                album.setArtistId(artistId);
            }
            Genre genreId = album.getGenreId();
            if (genreId != null) {
                genreId = em.getReference(genreId.getClass(), genreId.getId());
                album.setGenreId(genreId);
            }
            RecordingLabel recordingLabelId = album.getRecordingLabelId();
            if (recordingLabelId != null) {
                recordingLabelId = em.getReference(recordingLabelId.getClass(), recordingLabelId.getId());
                album.setRecordingLabelId(recordingLabelId);
            }
            Collection<Track> attachedTrackCollection = new ArrayList<Track>();
            for (Track trackCollectionTrackToAttach : album.getTrackCollection()) {
                trackCollectionTrackToAttach = em.getReference(trackCollectionTrackToAttach.getClass(), trackCollectionTrackToAttach.getId());
                attachedTrackCollection.add(trackCollectionTrackToAttach);
            }
            album.setTrackCollection(attachedTrackCollection);
            em.persist(album);
            if (artistId != null) {
                artistId.getAlbumCollection().add(album);
                artistId = em.merge(artistId);
            }
            if (genreId != null) {
                genreId.getAlbumCollection().add(album);
                genreId = em.merge(genreId);
            }
            if (recordingLabelId != null) {
                recordingLabelId.getAlbumCollection().add(album);
                recordingLabelId = em.merge(recordingLabelId);
            }
            for (Track trackCollectionTrack : album.getTrackCollection()) {
                Album oldAlbumIdOfTrackCollectionTrack = trackCollectionTrack.getAlbumId();
                trackCollectionTrack.setAlbumId(album);
                trackCollectionTrack = em.merge(trackCollectionTrack);
                if (oldAlbumIdOfTrackCollectionTrack != null) {
                    oldAlbumIdOfTrackCollectionTrack.getTrackCollection().remove(trackCollectionTrack);
                    oldAlbumIdOfTrackCollectionTrack = em.merge(oldAlbumIdOfTrackCollectionTrack);
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

    public void edit(Album album) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {

        try {
            utx.begin();
            Album persistentAlbum = em.find(Album.class, album.getId());
            Artist artistIdOld = persistentAlbum.getArtistId();
            Artist artistIdNew = album.getArtistId();
            Genre genreIdOld = persistentAlbum.getGenreId();
            Genre genreIdNew = album.getGenreId();
            RecordingLabel recordingLabelIdOld = persistentAlbum.getRecordingLabelId();
            RecordingLabel recordingLabelIdNew = album.getRecordingLabelId();
            Collection<Track> trackCollectionOld = persistentAlbum.getTrackCollection();
            Collection<Track> trackCollectionNew = album.getTrackCollection();
            List<String> illegalOrphanMessages = null;
            for (Track trackCollectionOldTrack : trackCollectionOld) {
                if (!trackCollectionNew.contains(trackCollectionOldTrack)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Track " + trackCollectionOldTrack + " since its albumId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (artistIdNew != null) {
                artistIdNew = em.getReference(artistIdNew.getClass(), artistIdNew.getId());
                album.setArtistId(artistIdNew);
            }
            if (genreIdNew != null) {
                genreIdNew = em.getReference(genreIdNew.getClass(), genreIdNew.getId());
                album.setGenreId(genreIdNew);
            }
            if (recordingLabelIdNew != null) {
                recordingLabelIdNew = em.getReference(recordingLabelIdNew.getClass(), recordingLabelIdNew.getId());
                album.setRecordingLabelId(recordingLabelIdNew);
            }
            Collection<Track> attachedTrackCollectionNew = new ArrayList<Track>();
            for (Track trackCollectionNewTrackToAttach : trackCollectionNew) {
                trackCollectionNewTrackToAttach = em.getReference(trackCollectionNewTrackToAttach.getClass(), trackCollectionNewTrackToAttach.getId());
                attachedTrackCollectionNew.add(trackCollectionNewTrackToAttach);
            }
            trackCollectionNew = attachedTrackCollectionNew;
            album.setTrackCollection(trackCollectionNew);
            album = em.merge(album);
            if (artistIdOld != null && !artistIdOld.equals(artistIdNew)) {
                artistIdOld.getAlbumCollection().remove(album);
                artistIdOld = em.merge(artistIdOld);
            }
            if (artistIdNew != null && !artistIdNew.equals(artistIdOld)) {
                artistIdNew.getAlbumCollection().add(album);
                artistIdNew = em.merge(artistIdNew);
            }
            if (genreIdOld != null && !genreIdOld.equals(genreIdNew)) {
                genreIdOld.getAlbumCollection().remove(album);
                genreIdOld = em.merge(genreIdOld);
            }
            if (genreIdNew != null && !genreIdNew.equals(genreIdOld)) {
                genreIdNew.getAlbumCollection().add(album);
                genreIdNew = em.merge(genreIdNew);
            }
            if (recordingLabelIdOld != null && !recordingLabelIdOld.equals(recordingLabelIdNew)) {
                recordingLabelIdOld.getAlbumCollection().remove(album);
                recordingLabelIdOld = em.merge(recordingLabelIdOld);
            }
            if (recordingLabelIdNew != null && !recordingLabelIdNew.equals(recordingLabelIdOld)) {
                recordingLabelIdNew.getAlbumCollection().add(album);
                recordingLabelIdNew = em.merge(recordingLabelIdNew);
            }
            for (Track trackCollectionNewTrack : trackCollectionNew) {
                if (!trackCollectionOld.contains(trackCollectionNewTrack)) {
                    Album oldAlbumIdOfTrackCollectionNewTrack = trackCollectionNewTrack.getAlbumId();
                    trackCollectionNewTrack.setAlbumId(album);
                    trackCollectionNewTrack = em.merge(trackCollectionNewTrack);
                    if (oldAlbumIdOfTrackCollectionNewTrack != null && !oldAlbumIdOfTrackCollectionNewTrack.equals(album)) {
                        oldAlbumIdOfTrackCollectionNewTrack.getTrackCollection().remove(trackCollectionNewTrack);
                        oldAlbumIdOfTrackCollectionNewTrack = em.merge(oldAlbumIdOfTrackCollectionNewTrack);
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
                Integer id = album.getId();
                if (findAlbum(id) == null) {
                    throw new NonexistentEntityException("The album with id " + id + " no longer exists.");
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
        try {
            utx.begin();
            Album album;
            try {
                album = em.getReference(Album.class, id);
                album.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The album with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Track> trackCollectionOrphanCheck = album.getTrackCollection();
            for (Track trackCollectionOrphanCheckTrack : trackCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Album (" + album + ") cannot be destroyed since the Track " + trackCollectionOrphanCheckTrack + " in its trackCollection field has a non-nullable albumId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Artist artistId = album.getArtistId();
            if (artistId != null) {
                artistId.getAlbumCollection().remove(album);
                artistId = em.merge(artistId);
            }
            Genre genreId = album.getGenreId();
            if (genreId != null) {
                genreId.getAlbumCollection().remove(album);
                genreId = em.merge(genreId);
            }
            RecordingLabel recordingLabelId = album.getRecordingLabelId();
            if (recordingLabelId != null) {
                recordingLabelId.getAlbumCollection().remove(album);
                recordingLabelId = em.merge(recordingLabelId);
            }
            em.remove(album);
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

    public List<Album> getAll() {
        return findAlbumEntities(true, -1, -1);
    }

    public List<Album> findAlbumEntities(int maxResults, int firstResult) {
        return findAlbumEntities(false, maxResults, firstResult);
    }

    private List<Album> findAlbumEntities(boolean all, int maxResults, int firstResult) {
        
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Album.class));
            Query q = em.createQuery(cq);

            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            System.out.println("list length = " + q.getResultList().size());
            return q.getResultList();
    }

    public Album findAlbum(Integer id) {
            return em.find(Album.class, id);
        
    }

    public int getAlbumCount() {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Album> rt = cq.from(Album.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        
    }
}

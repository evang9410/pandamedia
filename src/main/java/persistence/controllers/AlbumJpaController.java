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
import persistence.entities.Artist;
import persistence.entities.Genre;
import persistence.entities.RecordingLabel;
import persistence.entities.Track;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import persistence.controllers.exceptions.IllegalOrphanException;
import persistence.controllers.exceptions.NonexistentEntityException;
import persistence.controllers.exceptions.RollbackFailureException;
import persistence.entities.Album;

/**
 *
 * @author Evang
 */
@Named
@RequestScoped
public class AlbumJpaController implements Serializable {

    @Resource
    private UserTransaction utx;
    @PersistenceContext
    private EntityManager em;

    public void create(Album album) throws RollbackFailureException, Exception {
        if (album.getTrackList() == null) {
            album.setTrackList(new ArrayList<Track>());
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
            List<Track> attachedTrackList = new ArrayList<Track>();
            for (Track trackListTrackToAttach : album.getTrackList()) {
                trackListTrackToAttach = em.getReference(trackListTrackToAttach.getClass(), trackListTrackToAttach.getId());
                attachedTrackList.add(trackListTrackToAttach);
            }
            album.setTrackList(attachedTrackList);
            em.persist(album);
            if (artistId != null) {
                artistId.getAlbumList().add(album);
                artistId = em.merge(artistId);
            }
            if (genreId != null) {
                genreId.getAlbumList().add(album);
                genreId = em.merge(genreId);
            }
            if (recordingLabelId != null) {
                recordingLabelId.getAlbumList().add(album);
                recordingLabelId = em.merge(recordingLabelId);
            }
            for (Track trackListTrack : album.getTrackList()) {
                Album oldAlbumIdOfTrackListTrack = trackListTrack.getAlbumId();
                trackListTrack.setAlbumId(album);
                trackListTrack = em.merge(trackListTrack);
                if (oldAlbumIdOfTrackListTrack != null) {
                    oldAlbumIdOfTrackListTrack.getTrackList().remove(trackListTrack);
                    oldAlbumIdOfTrackListTrack = em.merge(oldAlbumIdOfTrackListTrack);
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
            List<Track> trackListOld = persistentAlbum.getTrackList();
            List<Track> trackListNew = album.getTrackList();
            List<String> illegalOrphanMessages = null;
            for (Track trackListOldTrack : trackListOld) {
                if (!trackListNew.contains(trackListOldTrack)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Track " + trackListOldTrack + " since its albumId field is not nullable.");
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
            List<Track> attachedTrackListNew = new ArrayList<Track>();
            for (Track trackListNewTrackToAttach : trackListNew) {
                trackListNewTrackToAttach = em.getReference(trackListNewTrackToAttach.getClass(), trackListNewTrackToAttach.getId());
                attachedTrackListNew.add(trackListNewTrackToAttach);
            }
            trackListNew = attachedTrackListNew;
            album.setTrackList(trackListNew);
            album = em.merge(album);
            if (artistIdOld != null && !artistIdOld.equals(artistIdNew)) {
                artistIdOld.getAlbumList().remove(album);
                artistIdOld = em.merge(artistIdOld);
            }
            if (artistIdNew != null && !artistIdNew.equals(artistIdOld)) {
                artistIdNew.getAlbumList().add(album);
                artistIdNew = em.merge(artistIdNew);
            }
            if (genreIdOld != null && !genreIdOld.equals(genreIdNew)) {
                genreIdOld.getAlbumList().remove(album);
                genreIdOld = em.merge(genreIdOld);
            }
            if (genreIdNew != null && !genreIdNew.equals(genreIdOld)) {
                genreIdNew.getAlbumList().add(album);
                genreIdNew = em.merge(genreIdNew);
            }
            if (recordingLabelIdOld != null && !recordingLabelIdOld.equals(recordingLabelIdNew)) {
                recordingLabelIdOld.getAlbumList().remove(album);
                recordingLabelIdOld = em.merge(recordingLabelIdOld);
            }
            if (recordingLabelIdNew != null && !recordingLabelIdNew.equals(recordingLabelIdOld)) {
                recordingLabelIdNew.getAlbumList().add(album);
                recordingLabelIdNew = em.merge(recordingLabelIdNew);
            }
            for (Track trackListNewTrack : trackListNew) {
                if (!trackListOld.contains(trackListNewTrack)) {
                    Album oldAlbumIdOfTrackListNewTrack = trackListNewTrack.getAlbumId();
                    trackListNewTrack.setAlbumId(album);
                    trackListNewTrack = em.merge(trackListNewTrack);
                    if (oldAlbumIdOfTrackListNewTrack != null && !oldAlbumIdOfTrackListNewTrack.equals(album)) {
                        oldAlbumIdOfTrackListNewTrack.getTrackList().remove(trackListNewTrack);
                        oldAlbumIdOfTrackListNewTrack = em.merge(oldAlbumIdOfTrackListNewTrack);
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
            List<Track> trackListOrphanCheck = album.getTrackList();
            for (Track trackListOrphanCheckTrack : trackListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Album (" + album + ") cannot be destroyed since the Track " + trackListOrphanCheckTrack + " in its trackList field has a non-nullable albumId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Artist artistId = album.getArtistId();
            if (artistId != null) {
                artistId.getAlbumList().remove(album);
                artistId = em.merge(artistId);
            }
            Genre genreId = album.getGenreId();
            if (genreId != null) {
                genreId.getAlbumList().remove(album);
                genreId = em.merge(genreId);
            }
            RecordingLabel recordingLabelId = album.getRecordingLabelId();
            if (recordingLabelId != null) {
                recordingLabelId.getAlbumList().remove(album);
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
        }
    }

    public List<Album> findAlbumEntities() {
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

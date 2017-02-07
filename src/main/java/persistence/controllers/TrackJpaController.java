/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.controllers;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import persistence.Album;
import persistence.Artist;
import persistence.Songwriter;
import persistence.Genre;
import persistence.Track;
import persistence.controllers.exceptions.NonexistentEntityException;
import persistence.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Evang
 */
public class TrackJpaController implements Serializable {

    public TrackJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Track track) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Album albumId = track.getAlbumId();
            if (albumId != null) {
                albumId = em.getReference(albumId.getClass(), albumId.getId());
                track.setAlbumId(albumId);
            }
            Artist artistId = track.getArtistId();
            if (artistId != null) {
                artistId = em.getReference(artistId.getClass(), artistId.getId());
                track.setArtistId(artistId);
            }
            Songwriter songwriterId = track.getSongwriterId();
            if (songwriterId != null) {
                songwriterId = em.getReference(songwriterId.getClass(), songwriterId.getId());
                track.setSongwriterId(songwriterId);
            }
            Genre genreId = track.getGenreId();
            if (genreId != null) {
                genreId = em.getReference(genreId.getClass(), genreId.getId());
                track.setGenreId(genreId);
            }
            em.persist(track);
            if (albumId != null) {
                albumId.getTrackCollection().add(track);
                albumId = em.merge(albumId);
            }
            if (artistId != null) {
                artistId.getTrackCollection().add(track);
                artistId = em.merge(artistId);
            }
            if (songwriterId != null) {
                songwriterId.getTrackCollection().add(track);
                songwriterId = em.merge(songwriterId);
            }
            if (genreId != null) {
                genreId.getTrackCollection().add(track);
                genreId = em.merge(genreId);
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

    public void edit(Track track) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Track persistentTrack = em.find(Track.class, track.getId());
            Album albumIdOld = persistentTrack.getAlbumId();
            Album albumIdNew = track.getAlbumId();
            Artist artistIdOld = persistentTrack.getArtistId();
            Artist artistIdNew = track.getArtistId();
            Songwriter songwriterIdOld = persistentTrack.getSongwriterId();
            Songwriter songwriterIdNew = track.getSongwriterId();
            Genre genreIdOld = persistentTrack.getGenreId();
            Genre genreIdNew = track.getGenreId();
            if (albumIdNew != null) {
                albumIdNew = em.getReference(albumIdNew.getClass(), albumIdNew.getId());
                track.setAlbumId(albumIdNew);
            }
            if (artistIdNew != null) {
                artistIdNew = em.getReference(artistIdNew.getClass(), artistIdNew.getId());
                track.setArtistId(artistIdNew);
            }
            if (songwriterIdNew != null) {
                songwriterIdNew = em.getReference(songwriterIdNew.getClass(), songwriterIdNew.getId());
                track.setSongwriterId(songwriterIdNew);
            }
            if (genreIdNew != null) {
                genreIdNew = em.getReference(genreIdNew.getClass(), genreIdNew.getId());
                track.setGenreId(genreIdNew);
            }
            track = em.merge(track);
            if (albumIdOld != null && !albumIdOld.equals(albumIdNew)) {
                albumIdOld.getTrackCollection().remove(track);
                albumIdOld = em.merge(albumIdOld);
            }
            if (albumIdNew != null && !albumIdNew.equals(albumIdOld)) {
                albumIdNew.getTrackCollection().add(track);
                albumIdNew = em.merge(albumIdNew);
            }
            if (artistIdOld != null && !artistIdOld.equals(artistIdNew)) {
                artistIdOld.getTrackCollection().remove(track);
                artistIdOld = em.merge(artistIdOld);
            }
            if (artistIdNew != null && !artistIdNew.equals(artistIdOld)) {
                artistIdNew.getTrackCollection().add(track);
                artistIdNew = em.merge(artistIdNew);
            }
            if (songwriterIdOld != null && !songwriterIdOld.equals(songwriterIdNew)) {
                songwriterIdOld.getTrackCollection().remove(track);
                songwriterIdOld = em.merge(songwriterIdOld);
            }
            if (songwriterIdNew != null && !songwriterIdNew.equals(songwriterIdOld)) {
                songwriterIdNew.getTrackCollection().add(track);
                songwriterIdNew = em.merge(songwriterIdNew);
            }
            if (genreIdOld != null && !genreIdOld.equals(genreIdNew)) {
                genreIdOld.getTrackCollection().remove(track);
                genreIdOld = em.merge(genreIdOld);
            }
            if (genreIdNew != null && !genreIdNew.equals(genreIdOld)) {
                genreIdNew.getTrackCollection().add(track);
                genreIdNew = em.merge(genreIdNew);
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
                Integer id = track.getId();
                if (findTrack(id) == null) {
                    throw new NonexistentEntityException("The track with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Track track;
            try {
                track = em.getReference(Track.class, id);
                track.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The track with id " + id + " no longer exists.", enfe);
            }
            Album albumId = track.getAlbumId();
            if (albumId != null) {
                albumId.getTrackCollection().remove(track);
                albumId = em.merge(albumId);
            }
            Artist artistId = track.getArtistId();
            if (artistId != null) {
                artistId.getTrackCollection().remove(track);
                artistId = em.merge(artistId);
            }
            Songwriter songwriterId = track.getSongwriterId();
            if (songwriterId != null) {
                songwriterId.getTrackCollection().remove(track);
                songwriterId = em.merge(songwriterId);
            }
            Genre genreId = track.getGenreId();
            if (genreId != null) {
                genreId.getTrackCollection().remove(track);
                genreId = em.merge(genreId);
            }
            em.remove(track);
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

    public List<Track> findTrackEntities() {
        return findTrackEntities(true, -1, -1);
    }

    public List<Track> findTrackEntities(int maxResults, int firstResult) {
        return findTrackEntities(false, maxResults, firstResult);
    }

    private List<Track> findTrackEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Track.class));
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

    public Track findTrack(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Track.class, id);
        } finally {
            em.close();
        }
    }

    public int getTrackCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Track> rt = cq.from(Track.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

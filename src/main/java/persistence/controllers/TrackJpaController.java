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
import persistence.entities.Artist;
import persistence.entities.Songwriter;
import persistence.entities.Genre;
import persistence.entities.CoverArt;
import persistence.entities.Review;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import persistence.controllers.exceptions.IllegalOrphanException;
import persistence.controllers.exceptions.NonexistentEntityException;
import persistence.controllers.exceptions.RollbackFailureException;
import persistence.entities.Track;

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
        if (track.getReviewList() == null) {
            track.setReviewList(new ArrayList<Review>());
        }
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
            CoverArt coverArtId = track.getCoverArtId();
            if (coverArtId != null) {
                coverArtId = em.getReference(coverArtId.getClass(), coverArtId.getId());
                track.setCoverArtId(coverArtId);
            }
            List<Review> attachedReviewList = new ArrayList<Review>();
            for (Review reviewListReviewToAttach : track.getReviewList()) {
                reviewListReviewToAttach = em.getReference(reviewListReviewToAttach.getClass(), reviewListReviewToAttach.getId());
                attachedReviewList.add(reviewListReviewToAttach);
            }
            track.setReviewList(attachedReviewList);
            em.persist(track);
            if (albumId != null) {
                albumId.getTrackList().add(track);
                albumId = em.merge(albumId);
            }
            if (artistId != null) {
                artistId.getTrackList().add(track);
                artistId = em.merge(artistId);
            }
            if (songwriterId != null) {
                songwriterId.getTrackList().add(track);
                songwriterId = em.merge(songwriterId);
            }
            if (genreId != null) {
                genreId.getTrackList().add(track);
                genreId = em.merge(genreId);
            }
            if (coverArtId != null) {
                coverArtId.getTrackList().add(track);
                coverArtId = em.merge(coverArtId);
            }
            for (Review reviewListReview : track.getReviewList()) {
                Track oldTrackIdOfReviewListReview = reviewListReview.getTrackId();
                reviewListReview.setTrackId(track);
                reviewListReview = em.merge(reviewListReview);
                if (oldTrackIdOfReviewListReview != null) {
                    oldTrackIdOfReviewListReview.getReviewList().remove(reviewListReview);
                    oldTrackIdOfReviewListReview = em.merge(oldTrackIdOfReviewListReview);
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

    public void edit(Track track) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
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
            CoverArt coverArtIdOld = persistentTrack.getCoverArtId();
            CoverArt coverArtIdNew = track.getCoverArtId();
            List<Review> reviewListOld = persistentTrack.getReviewList();
            List<Review> reviewListNew = track.getReviewList();
            List<String> illegalOrphanMessages = null;
            for (Review reviewListOldReview : reviewListOld) {
                if (!reviewListNew.contains(reviewListOldReview)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Review " + reviewListOldReview + " since its trackId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
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
            if (coverArtIdNew != null) {
                coverArtIdNew = em.getReference(coverArtIdNew.getClass(), coverArtIdNew.getId());
                track.setCoverArtId(coverArtIdNew);
            }
            List<Review> attachedReviewListNew = new ArrayList<Review>();
            for (Review reviewListNewReviewToAttach : reviewListNew) {
                reviewListNewReviewToAttach = em.getReference(reviewListNewReviewToAttach.getClass(), reviewListNewReviewToAttach.getId());
                attachedReviewListNew.add(reviewListNewReviewToAttach);
            }
            reviewListNew = attachedReviewListNew;
            track.setReviewList(reviewListNew);
            track = em.merge(track);
            if (albumIdOld != null && !albumIdOld.equals(albumIdNew)) {
                albumIdOld.getTrackList().remove(track);
                albumIdOld = em.merge(albumIdOld);
            }
            if (albumIdNew != null && !albumIdNew.equals(albumIdOld)) {
                albumIdNew.getTrackList().add(track);
                albumIdNew = em.merge(albumIdNew);
            }
            if (artistIdOld != null && !artistIdOld.equals(artistIdNew)) {
                artistIdOld.getTrackList().remove(track);
                artistIdOld = em.merge(artistIdOld);
            }
            if (artistIdNew != null && !artistIdNew.equals(artistIdOld)) {
                artistIdNew.getTrackList().add(track);
                artistIdNew = em.merge(artistIdNew);
            }
            if (songwriterIdOld != null && !songwriterIdOld.equals(songwriterIdNew)) {
                songwriterIdOld.getTrackList().remove(track);
                songwriterIdOld = em.merge(songwriterIdOld);
            }
            if (songwriterIdNew != null && !songwriterIdNew.equals(songwriterIdOld)) {
                songwriterIdNew.getTrackList().add(track);
                songwriterIdNew = em.merge(songwriterIdNew);
            }
            if (genreIdOld != null && !genreIdOld.equals(genreIdNew)) {
                genreIdOld.getTrackList().remove(track);
                genreIdOld = em.merge(genreIdOld);
            }
            if (genreIdNew != null && !genreIdNew.equals(genreIdOld)) {
                genreIdNew.getTrackList().add(track);
                genreIdNew = em.merge(genreIdNew);
            }
            if (coverArtIdOld != null && !coverArtIdOld.equals(coverArtIdNew)) {
                coverArtIdOld.getTrackList().remove(track);
                coverArtIdOld = em.merge(coverArtIdOld);
            }
            if (coverArtIdNew != null && !coverArtIdNew.equals(coverArtIdOld)) {
                coverArtIdNew.getTrackList().add(track);
                coverArtIdNew = em.merge(coverArtIdNew);
            }
            for (Review reviewListNewReview : reviewListNew) {
                if (!reviewListOld.contains(reviewListNewReview)) {
                    Track oldTrackIdOfReviewListNewReview = reviewListNewReview.getTrackId();
                    reviewListNewReview.setTrackId(track);
                    reviewListNewReview = em.merge(reviewListNewReview);
                    if (oldTrackIdOfReviewListNewReview != null && !oldTrackIdOfReviewListNewReview.equals(track)) {
                        oldTrackIdOfReviewListNewReview.getReviewList().remove(reviewListNewReview);
                        oldTrackIdOfReviewListNewReview = em.merge(oldTrackIdOfReviewListNewReview);
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

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
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
            List<String> illegalOrphanMessages = null;
            List<Review> reviewListOrphanCheck = track.getReviewList();
            for (Review reviewListOrphanCheckReview : reviewListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Track (" + track + ") cannot be destroyed since the Review " + reviewListOrphanCheckReview + " in its reviewList field has a non-nullable trackId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Album albumId = track.getAlbumId();
            if (albumId != null) {
                albumId.getTrackList().remove(track);
                albumId = em.merge(albumId);
            }
            Artist artistId = track.getArtistId();
            if (artistId != null) {
                artistId.getTrackList().remove(track);
                artistId = em.merge(artistId);
            }
            Songwriter songwriterId = track.getSongwriterId();
            if (songwriterId != null) {
                songwriterId.getTrackList().remove(track);
                songwriterId = em.merge(songwriterId);
            }
            Genre genreId = track.getGenreId();
            if (genreId != null) {
                genreId.getTrackList().remove(track);
                genreId = em.merge(genreId);
            }
            CoverArt coverArtId = track.getCoverArtId();
            if (coverArtId != null) {
                coverArtId.getTrackList().remove(track);
                coverArtId = em.merge(coverArtId);
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

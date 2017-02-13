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
import persistence.entities.Artist;
import persistence.entities.Track;

/**
 *
 * @author Evang
 */
public class ArtistJpaController implements Serializable {

    public ArtistJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Artist artist) throws RollbackFailureException, Exception {
        if (artist.getAlbumList() == null) {
            artist.setAlbumList(new ArrayList<Album>());
        }
        if (artist.getTrackList() == null) {
            artist.setTrackList(new ArrayList<Track>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Album> attachedAlbumList = new ArrayList<Album>();
            for (Album albumListAlbumToAttach : artist.getAlbumList()) {
                albumListAlbumToAttach = em.getReference(albumListAlbumToAttach.getClass(), albumListAlbumToAttach.getId());
                attachedAlbumList.add(albumListAlbumToAttach);
            }
            artist.setAlbumList(attachedAlbumList);
            List<Track> attachedTrackList = new ArrayList<Track>();
            for (Track trackListTrackToAttach : artist.getTrackList()) {
                trackListTrackToAttach = em.getReference(trackListTrackToAttach.getClass(), trackListTrackToAttach.getId());
                attachedTrackList.add(trackListTrackToAttach);
            }
            artist.setTrackList(attachedTrackList);
            em.persist(artist);
            for (Album albumListAlbum : artist.getAlbumList()) {
                Artist oldArtistIdOfAlbumListAlbum = albumListAlbum.getArtistId();
                albumListAlbum.setArtistId(artist);
                albumListAlbum = em.merge(albumListAlbum);
                if (oldArtistIdOfAlbumListAlbum != null) {
                    oldArtistIdOfAlbumListAlbum.getAlbumList().remove(albumListAlbum);
                    oldArtistIdOfAlbumListAlbum = em.merge(oldArtistIdOfAlbumListAlbum);
                }
            }
            for (Track trackListTrack : artist.getTrackList()) {
                Artist oldArtistIdOfTrackListTrack = trackListTrack.getArtistId();
                trackListTrack.setArtistId(artist);
                trackListTrack = em.merge(trackListTrack);
                if (oldArtistIdOfTrackListTrack != null) {
                    oldArtistIdOfTrackListTrack.getTrackList().remove(trackListTrack);
                    oldArtistIdOfTrackListTrack = em.merge(oldArtistIdOfTrackListTrack);
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

    public void edit(Artist artist) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Artist persistentArtist = em.find(Artist.class, artist.getId());
            List<Album> albumListOld = persistentArtist.getAlbumList();
            List<Album> albumListNew = artist.getAlbumList();
            List<Track> trackListOld = persistentArtist.getTrackList();
            List<Track> trackListNew = artist.getTrackList();
            List<String> illegalOrphanMessages = null;
            for (Album albumListOldAlbum : albumListOld) {
                if (!albumListNew.contains(albumListOldAlbum)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Album " + albumListOldAlbum + " since its artistId field is not nullable.");
                }
            }
            for (Track trackListOldTrack : trackListOld) {
                if (!trackListNew.contains(trackListOldTrack)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Track " + trackListOldTrack + " since its artistId field is not nullable.");
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
            artist.setAlbumList(albumListNew);
            List<Track> attachedTrackListNew = new ArrayList<Track>();
            for (Track trackListNewTrackToAttach : trackListNew) {
                trackListNewTrackToAttach = em.getReference(trackListNewTrackToAttach.getClass(), trackListNewTrackToAttach.getId());
                attachedTrackListNew.add(trackListNewTrackToAttach);
            }
            trackListNew = attachedTrackListNew;
            artist.setTrackList(trackListNew);
            artist = em.merge(artist);
            for (Album albumListNewAlbum : albumListNew) {
                if (!albumListOld.contains(albumListNewAlbum)) {
                    Artist oldArtistIdOfAlbumListNewAlbum = albumListNewAlbum.getArtistId();
                    albumListNewAlbum.setArtistId(artist);
                    albumListNewAlbum = em.merge(albumListNewAlbum);
                    if (oldArtistIdOfAlbumListNewAlbum != null && !oldArtistIdOfAlbumListNewAlbum.equals(artist)) {
                        oldArtistIdOfAlbumListNewAlbum.getAlbumList().remove(albumListNewAlbum);
                        oldArtistIdOfAlbumListNewAlbum = em.merge(oldArtistIdOfAlbumListNewAlbum);
                    }
                }
            }
            for (Track trackListNewTrack : trackListNew) {
                if (!trackListOld.contains(trackListNewTrack)) {
                    Artist oldArtistIdOfTrackListNewTrack = trackListNewTrack.getArtistId();
                    trackListNewTrack.setArtistId(artist);
                    trackListNewTrack = em.merge(trackListNewTrack);
                    if (oldArtistIdOfTrackListNewTrack != null && !oldArtistIdOfTrackListNewTrack.equals(artist)) {
                        oldArtistIdOfTrackListNewTrack.getTrackList().remove(trackListNewTrack);
                        oldArtistIdOfTrackListNewTrack = em.merge(oldArtistIdOfTrackListNewTrack);
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
                Integer id = artist.getId();
                if (findArtist(id) == null) {
                    throw new NonexistentEntityException("The artist with id " + id + " no longer exists.");
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
            Artist artist;
            try {
                artist = em.getReference(Artist.class, id);
                artist.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The artist with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Album> albumListOrphanCheck = artist.getAlbumList();
            for (Album albumListOrphanCheckAlbum : albumListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Artist (" + artist + ") cannot be destroyed since the Album " + albumListOrphanCheckAlbum + " in its albumList field has a non-nullable artistId field.");
            }
            List<Track> trackListOrphanCheck = artist.getTrackList();
            for (Track trackListOrphanCheckTrack : trackListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Artist (" + artist + ") cannot be destroyed since the Track " + trackListOrphanCheckTrack + " in its trackList field has a non-nullable artistId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(artist);
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

    public List<Artist> findArtistEntities() {
        return findArtistEntities(true, -1, -1);
    }

    public List<Artist> findArtistEntities(int maxResults, int firstResult) {
        return findArtistEntities(false, maxResults, firstResult);
    }

    private List<Artist> findArtistEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Artist.class));
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

    public Artist findArtist(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Artist.class, id);
        } finally {
            em.close();
        }
    }

    public int getArtistCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Artist> rt = cq.from(Artist.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

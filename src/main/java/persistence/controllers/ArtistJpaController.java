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
import persistence.Artist;
import persistence.Track;
import persistence.controllers.exceptions.IllegalOrphanException;
import persistence.controllers.exceptions.NonexistentEntityException;
import persistence.controllers.exceptions.RollbackFailureException;

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
        if (artist.getAlbumCollection() == null) {
            artist.setAlbumCollection(new ArrayList<Album>());
        }
        if (artist.getTrackCollection() == null) {
            artist.setTrackCollection(new ArrayList<Track>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Album> attachedAlbumCollection = new ArrayList<Album>();
            for (Album albumCollectionAlbumToAttach : artist.getAlbumCollection()) {
                albumCollectionAlbumToAttach = em.getReference(albumCollectionAlbumToAttach.getClass(), albumCollectionAlbumToAttach.getId());
                attachedAlbumCollection.add(albumCollectionAlbumToAttach);
            }
            artist.setAlbumCollection(attachedAlbumCollection);
            Collection<Track> attachedTrackCollection = new ArrayList<Track>();
            for (Track trackCollectionTrackToAttach : artist.getTrackCollection()) {
                trackCollectionTrackToAttach = em.getReference(trackCollectionTrackToAttach.getClass(), trackCollectionTrackToAttach.getId());
                attachedTrackCollection.add(trackCollectionTrackToAttach);
            }
            artist.setTrackCollection(attachedTrackCollection);
            em.persist(artist);
            for (Album albumCollectionAlbum : artist.getAlbumCollection()) {
                Artist oldArtistIdOfAlbumCollectionAlbum = albumCollectionAlbum.getArtistId();
                albumCollectionAlbum.setArtistId(artist);
                albumCollectionAlbum = em.merge(albumCollectionAlbum);
                if (oldArtistIdOfAlbumCollectionAlbum != null) {
                    oldArtistIdOfAlbumCollectionAlbum.getAlbumCollection().remove(albumCollectionAlbum);
                    oldArtistIdOfAlbumCollectionAlbum = em.merge(oldArtistIdOfAlbumCollectionAlbum);
                }
            }
            for (Track trackCollectionTrack : artist.getTrackCollection()) {
                Artist oldArtistIdOfTrackCollectionTrack = trackCollectionTrack.getArtistId();
                trackCollectionTrack.setArtistId(artist);
                trackCollectionTrack = em.merge(trackCollectionTrack);
                if (oldArtistIdOfTrackCollectionTrack != null) {
                    oldArtistIdOfTrackCollectionTrack.getTrackCollection().remove(trackCollectionTrack);
                    oldArtistIdOfTrackCollectionTrack = em.merge(oldArtistIdOfTrackCollectionTrack);
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
            Collection<Album> albumCollectionOld = persistentArtist.getAlbumCollection();
            Collection<Album> albumCollectionNew = artist.getAlbumCollection();
            Collection<Track> trackCollectionOld = persistentArtist.getTrackCollection();
            Collection<Track> trackCollectionNew = artist.getTrackCollection();
            List<String> illegalOrphanMessages = null;
            for (Album albumCollectionOldAlbum : albumCollectionOld) {
                if (!albumCollectionNew.contains(albumCollectionOldAlbum)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Album " + albumCollectionOldAlbum + " since its artistId field is not nullable.");
                }
            }
            for (Track trackCollectionOldTrack : trackCollectionOld) {
                if (!trackCollectionNew.contains(trackCollectionOldTrack)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Track " + trackCollectionOldTrack + " since its artistId field is not nullable.");
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
            artist.setAlbumCollection(albumCollectionNew);
            Collection<Track> attachedTrackCollectionNew = new ArrayList<Track>();
            for (Track trackCollectionNewTrackToAttach : trackCollectionNew) {
                trackCollectionNewTrackToAttach = em.getReference(trackCollectionNewTrackToAttach.getClass(), trackCollectionNewTrackToAttach.getId());
                attachedTrackCollectionNew.add(trackCollectionNewTrackToAttach);
            }
            trackCollectionNew = attachedTrackCollectionNew;
            artist.setTrackCollection(trackCollectionNew);
            artist = em.merge(artist);
            for (Album albumCollectionNewAlbum : albumCollectionNew) {
                if (!albumCollectionOld.contains(albumCollectionNewAlbum)) {
                    Artist oldArtistIdOfAlbumCollectionNewAlbum = albumCollectionNewAlbum.getArtistId();
                    albumCollectionNewAlbum.setArtistId(artist);
                    albumCollectionNewAlbum = em.merge(albumCollectionNewAlbum);
                    if (oldArtistIdOfAlbumCollectionNewAlbum != null && !oldArtistIdOfAlbumCollectionNewAlbum.equals(artist)) {
                        oldArtistIdOfAlbumCollectionNewAlbum.getAlbumCollection().remove(albumCollectionNewAlbum);
                        oldArtistIdOfAlbumCollectionNewAlbum = em.merge(oldArtistIdOfAlbumCollectionNewAlbum);
                    }
                }
            }
            for (Track trackCollectionNewTrack : trackCollectionNew) {
                if (!trackCollectionOld.contains(trackCollectionNewTrack)) {
                    Artist oldArtistIdOfTrackCollectionNewTrack = trackCollectionNewTrack.getArtistId();
                    trackCollectionNewTrack.setArtistId(artist);
                    trackCollectionNewTrack = em.merge(trackCollectionNewTrack);
                    if (oldArtistIdOfTrackCollectionNewTrack != null && !oldArtistIdOfTrackCollectionNewTrack.equals(artist)) {
                        oldArtistIdOfTrackCollectionNewTrack.getTrackCollection().remove(trackCollectionNewTrack);
                        oldArtistIdOfTrackCollectionNewTrack = em.merge(oldArtistIdOfTrackCollectionNewTrack);
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
            Collection<Album> albumCollectionOrphanCheck = artist.getAlbumCollection();
            for (Album albumCollectionOrphanCheckAlbum : albumCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Artist (" + artist + ") cannot be destroyed since the Album " + albumCollectionOrphanCheckAlbum + " in its albumCollection field has a non-nullable artistId field.");
            }
            Collection<Track> trackCollectionOrphanCheck = artist.getTrackCollection();
            for (Track trackCollectionOrphanCheckTrack : trackCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Artist (" + artist + ") cannot be destroyed since the Track " + trackCollectionOrphanCheckTrack + " in its trackCollection field has a non-nullable artistId field.");
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

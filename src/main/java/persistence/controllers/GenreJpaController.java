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
import persistence.Genre;
import persistence.Track;
import persistence.controllers.exceptions.IllegalOrphanException;
import persistence.controllers.exceptions.NonexistentEntityException;
import persistence.controllers.exceptions.RollbackFailureException;

/**
 *
 * @author Evang
 */
public class GenreJpaController implements Serializable {

    public GenreJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Genre genre) throws RollbackFailureException, Exception {
        if (genre.getAlbumCollection() == null) {
            genre.setAlbumCollection(new ArrayList<Album>());
        }
        if (genre.getTrackCollection() == null) {
            genre.setTrackCollection(new ArrayList<Track>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Album> attachedAlbumCollection = new ArrayList<Album>();
            for (Album albumCollectionAlbumToAttach : genre.getAlbumCollection()) {
                albumCollectionAlbumToAttach = em.getReference(albumCollectionAlbumToAttach.getClass(), albumCollectionAlbumToAttach.getId());
                attachedAlbumCollection.add(albumCollectionAlbumToAttach);
            }
            genre.setAlbumCollection(attachedAlbumCollection);
            Collection<Track> attachedTrackCollection = new ArrayList<Track>();
            for (Track trackCollectionTrackToAttach : genre.getTrackCollection()) {
                trackCollectionTrackToAttach = em.getReference(trackCollectionTrackToAttach.getClass(), trackCollectionTrackToAttach.getId());
                attachedTrackCollection.add(trackCollectionTrackToAttach);
            }
            genre.setTrackCollection(attachedTrackCollection);
            em.persist(genre);
            for (Album albumCollectionAlbum : genre.getAlbumCollection()) {
                Genre oldGenreIdOfAlbumCollectionAlbum = albumCollectionAlbum.getGenreId();
                albumCollectionAlbum.setGenreId(genre);
                albumCollectionAlbum = em.merge(albumCollectionAlbum);
                if (oldGenreIdOfAlbumCollectionAlbum != null) {
                    oldGenreIdOfAlbumCollectionAlbum.getAlbumCollection().remove(albumCollectionAlbum);
                    oldGenreIdOfAlbumCollectionAlbum = em.merge(oldGenreIdOfAlbumCollectionAlbum);
                }
            }
            for (Track trackCollectionTrack : genre.getTrackCollection()) {
                Genre oldGenreIdOfTrackCollectionTrack = trackCollectionTrack.getGenreId();
                trackCollectionTrack.setGenreId(genre);
                trackCollectionTrack = em.merge(trackCollectionTrack);
                if (oldGenreIdOfTrackCollectionTrack != null) {
                    oldGenreIdOfTrackCollectionTrack.getTrackCollection().remove(trackCollectionTrack);
                    oldGenreIdOfTrackCollectionTrack = em.merge(oldGenreIdOfTrackCollectionTrack);
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

    public void edit(Genre genre) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Genre persistentGenre = em.find(Genre.class, genre.getId());
            Collection<Album> albumCollectionOld = persistentGenre.getAlbumCollection();
            Collection<Album> albumCollectionNew = genre.getAlbumCollection();
            Collection<Track> trackCollectionOld = persistentGenre.getTrackCollection();
            Collection<Track> trackCollectionNew = genre.getTrackCollection();
            List<String> illegalOrphanMessages = null;
            for (Album albumCollectionOldAlbum : albumCollectionOld) {
                if (!albumCollectionNew.contains(albumCollectionOldAlbum)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Album " + albumCollectionOldAlbum + " since its genreId field is not nullable.");
                }
            }
            for (Track trackCollectionOldTrack : trackCollectionOld) {
                if (!trackCollectionNew.contains(trackCollectionOldTrack)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Track " + trackCollectionOldTrack + " since its genreId field is not nullable.");
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
            genre.setAlbumCollection(albumCollectionNew);
            Collection<Track> attachedTrackCollectionNew = new ArrayList<Track>();
            for (Track trackCollectionNewTrackToAttach : trackCollectionNew) {
                trackCollectionNewTrackToAttach = em.getReference(trackCollectionNewTrackToAttach.getClass(), trackCollectionNewTrackToAttach.getId());
                attachedTrackCollectionNew.add(trackCollectionNewTrackToAttach);
            }
            trackCollectionNew = attachedTrackCollectionNew;
            genre.setTrackCollection(trackCollectionNew);
            genre = em.merge(genre);
            for (Album albumCollectionNewAlbum : albumCollectionNew) {
                if (!albumCollectionOld.contains(albumCollectionNewAlbum)) {
                    Genre oldGenreIdOfAlbumCollectionNewAlbum = albumCollectionNewAlbum.getGenreId();
                    albumCollectionNewAlbum.setGenreId(genre);
                    albumCollectionNewAlbum = em.merge(albumCollectionNewAlbum);
                    if (oldGenreIdOfAlbumCollectionNewAlbum != null && !oldGenreIdOfAlbumCollectionNewAlbum.equals(genre)) {
                        oldGenreIdOfAlbumCollectionNewAlbum.getAlbumCollection().remove(albumCollectionNewAlbum);
                        oldGenreIdOfAlbumCollectionNewAlbum = em.merge(oldGenreIdOfAlbumCollectionNewAlbum);
                    }
                }
            }
            for (Track trackCollectionNewTrack : trackCollectionNew) {
                if (!trackCollectionOld.contains(trackCollectionNewTrack)) {
                    Genre oldGenreIdOfTrackCollectionNewTrack = trackCollectionNewTrack.getGenreId();
                    trackCollectionNewTrack.setGenreId(genre);
                    trackCollectionNewTrack = em.merge(trackCollectionNewTrack);
                    if (oldGenreIdOfTrackCollectionNewTrack != null && !oldGenreIdOfTrackCollectionNewTrack.equals(genre)) {
                        oldGenreIdOfTrackCollectionNewTrack.getTrackCollection().remove(trackCollectionNewTrack);
                        oldGenreIdOfTrackCollectionNewTrack = em.merge(oldGenreIdOfTrackCollectionNewTrack);
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
                Integer id = genre.getId();
                if (findGenre(id) == null) {
                    throw new NonexistentEntityException("The genre with id " + id + " no longer exists.");
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
            Genre genre;
            try {
                genre = em.getReference(Genre.class, id);
                genre.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The genre with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Album> albumCollectionOrphanCheck = genre.getAlbumCollection();
            for (Album albumCollectionOrphanCheckAlbum : albumCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Genre (" + genre + ") cannot be destroyed since the Album " + albumCollectionOrphanCheckAlbum + " in its albumCollection field has a non-nullable genreId field.");
            }
            Collection<Track> trackCollectionOrphanCheck = genre.getTrackCollection();
            for (Track trackCollectionOrphanCheckTrack : trackCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Genre (" + genre + ") cannot be destroyed since the Track " + trackCollectionOrphanCheckTrack + " in its trackCollection field has a non-nullable genreId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(genre);
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

    public List<Genre> findGenreEntities() {
        return findGenreEntities(true, -1, -1);
    }

    public List<Genre> findGenreEntities(int maxResults, int firstResult) {
        return findGenreEntities(false, maxResults, firstResult);
    }

    private List<Genre> findGenreEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Genre.class));
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

    public Genre findGenre(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Genre.class, id);
        } finally {
            em.close();
        }
    }

    public int getGenreCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Genre> rt = cq.from(Genre.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

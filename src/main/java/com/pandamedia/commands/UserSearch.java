package com.pandamedia.commands;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import persistence.entities.Album;
import persistence.entities.Artist;
import persistence.entities.Track;

/**
 *
 * @author Pierre Azelart
 */
@Named("Search")
//Doesnt update the search result list when changing type in dropdown
//If reducing scope is needed, could use ajax instead
@SessionScoped
public class UserSearch implements Serializable {

    //Error string
    private String notFound;
    //Necessary Lists for different types
    private List<Track> trackResultsList;
    private List<Album> albumResultsList;
    private List<Artist> artistResultsList;
    //Stores the last type researched by user
    private String typeSearched;
    private String parameters;

    @Inject
    private SearchDropdown sd;

    @PersistenceContext
    private EntityManager em;

    /**
     * Clears lists if not empty (Because SessionScoped)
     *
     *
     */
    private void reset() {
        if (trackResultsList != null && !trackResultsList.isEmpty()) {
            trackResultsList.clear();
        }
        if (albumResultsList != null && !albumResultsList.isEmpty()) {
            albumResultsList.clear();
        }
        if (artistResultsList != null && !artistResultsList.isEmpty()) {
            artistResultsList.clear();
        }
        notFound = "";
    }

    /**
     * Returns true if given query does not return empty or null else returns
     * false and sets 404 as error message
     *
     * @param query
     * @return
     */
    private boolean errorCheck(TypedQuery query) {
        if (query.getResultList() != null && !query.getResultList().isEmpty()) {
            return true;
        } else {
            //Displays error
            notFound = "404"; //if improved from 404, needs bundle
            return false;
        }
    }

    private boolean isResultSingle(TypedQuery query) {
        return query.getResultList().size() == 1;
    }

    /**
     * Called when search button is pressed. Executes the correct search
     * function depending on selected type
     *
     * @return
     */
    public String executeSearch() {
        //If nothing entered, does not execute
        if (!parameters.isEmpty()) {
            String str = sd.getType();
            typeSearched = str;
            reset();
            switch (str) {
                case "tracks":
                    searchTracks();
                    break;
                case "albums":
                    searchAlbums();
                    break;
                case "artists":
                    searchArtists();
                    break;
                case "date":
                    searchDate();
                    break;
            }
            return "search";
        }
        return null;
    }

    private Track searchTracks() {
        //Creates query that returns a list of tracks with a name relevant to "parameters"
        String q = "SELECT t FROM Track t WHERE t.title LIKE :var";
        TypedQuery<Track> query = em.createQuery(q, Track.class);
        query.setParameter("var", "%" + parameters + "%");

        if (errorCheck(query)) {
            trackResultsList = query.getResultList();
            if (isResultSingle(query)) {
                return trackResultsList.get(0);
            }
        }
        return null;
    }

    private Album searchAlbums() {
        //Creates query that returns a list of albums with a name relevant to "parameters"
        String q = "SELECT a FROM Album a WHERE a.title LIKE :var";
        TypedQuery<Album> query = em.createQuery(q, Album.class);
        query.setParameter("var", "%" + parameters + "%");
        if (errorCheck(query)) {
            albumResultsList = query.getResultList();
            if (isResultSingle(query)) {
                return albumResultsList.get(0);
            }
        }
        return null;

    }

    private Artist searchArtists() {
        //Creates query that returns a list of artists with a name relevant to "parameters"
        String q = "SELECT a FROM Artist a WHERE a.name LIKE :var";
        TypedQuery<Artist> query = em.createQuery(q, Artist.class);
        query.setParameter("var", "%" + parameters + "%");
        if (errorCheck(query)) {
            artistResultsList = query.getResultList();
            if (isResultSingle(query)) {
                return artistResultsList.get(0);
            }
        }
        return null;
    }

    private void searchDate() {
        //Creates query that returns a list of tracks with a release date relevant to "parameters"
    }

    /*Getters and Setters*/
    public void setParameters(String key) {
        this.parameters = key;
    }

    public String getParameters() {
        return this.parameters;
    }

    public List getTrackResultsList() {
        return trackResultsList;
    }

    public void setTrackResultsList(List trackResultsList) {
        this.trackResultsList = trackResultsList;
    }

    public List getAlbumResultsList() {
        return albumResultsList;
    }

    public void setAlbumResultsList(List albumResultsList) {
        this.albumResultsList = albumResultsList;
    }

    public List getArtistResultsList() {
        return artistResultsList;
    }

    public void setArtistResultsList(List artistResultsList) {
        this.artistResultsList = artistResultsList;
    }

    public String getNotFound() {
        return notFound;
    }

    public void setNotFound(String notFound) {
        this.notFound = notFound;
    }

    public String getTypeSearched() {
        return typeSearched;
    }

    public void setTypeSearched(String typeSearched) {
        this.typeSearched = typeSearched;
    }
}

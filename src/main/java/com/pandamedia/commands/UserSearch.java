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
    private searchDropdown sd;

    @PersistenceContext
    private EntityManager em;

    /**
     * Clears lists if not empty (Because SessionScoped)
     * 
     * 
     */
    private void reset(){
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
    
    private boolean errorCheck(TypedQuery query){
        if (query.getResultList() != null && !query.getResultList().isEmpty()) {
            return true;
        }
        else{
            //Displays error
            notFound = "404"; //if improved from 404, needs bundle
            return false;
        }
    }
    
    /*private String isResultSingle(){
    
        if(resultsList != null){
            if(resultsList.size() > 1){
                return //string url result page
            }
        }
        return null;
    }
    */
    
    public String executeSearch() {
        //If nothing entered, does not execute
        if(!parameters.isEmpty()){
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
            return "searchpage";
            }
        return null;
    }
  
    public void searchTracks() {
        //Creates query that returns a list of tracks with a name relevant to "parameters"
        String q = "SELECT t FROM Track t WHERE t.title LIKE :var";
        TypedQuery<Track> query = em.createQuery(q, Track.class);
        query.setParameter("var", "%" + parameters + "%");
        
        if (errorCheck(query)) {
            trackResultsList = query.getResultList();
        }
    }

    public void searchAlbums() {
        //Creates query that returns a list of albums with a name relevant to "parameters"
        String q = "SELECT a FROM Album a WHERE a.title LIKE :var";
        TypedQuery<Album> query = em.createQuery(q, Album.class);
        query.setParameter("var", "%" + parameters + "%");
        if (errorCheck(query)) {
            albumResultsList = query.getResultList();
        }
        
    }

    public void searchArtists() {
        //Creates query that returns a list of artists with a name relevant to "parameters"
        String q = "SELECT a FROM Artist a WHERE a.name LIKE :var";
        TypedQuery<Artist> query = em.createQuery(q, Artist.class);
        query.setParameter("var", "%" + parameters + "%");
        if (errorCheck(query)) {
            artistResultsList = query.getResultList();
        }
    }

    public void searchDate() {
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

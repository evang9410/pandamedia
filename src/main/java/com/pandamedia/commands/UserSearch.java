package com.pandamedia.commands;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.RequestScoped;
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
@RequestScoped
public class UserSearch implements Serializable{
    private List<Track> trackResultsList;
    private List albumResultsList;
    private List artistResultsList;
    
    private String parameters;
    
    @Inject
    private searchDropdown sd;
    
    @PersistenceContext
    private EntityManager em;

    public String executeSearch(){
        String str = sd.getType();
        switch(str) {
            case "tracks": searchTracks();
                break;
            case "albums": searchAlbums();
                break;
            case "artists": searchArtists();
                break;
            case "date": searchDate();
                break;
        }
        return "searchpage";
    }
    
    //Default Search Criteria    
    public void searchTracks(){
        //Creates query that returns a list of tracks with a name relevant to "parameters"
        String q = "SELECT t FROM Track t WHERE t.title LIKE :var";
        TypedQuery<Track> query =  em.createQuery(q, Track.class);
        query.setParameter("var", "%" + parameters + "%");
        trackResultsList = query.getResultList();
    }
    
    
    public void searchAlbums(){
        //Creates query that returns a list of albums with a name relevant to "parameters"
        String q = "SELECT a FROM Album a WHERE a.title LIKE :var";
        TypedQuery<Album> query =  em.createQuery(q, Album.class);
        query.setParameter("var", "%" + parameters + "%");
        albumResultsList = query.getResultList();
    }

    public void searchArtists(){
        //Creates query that returns a list of artists with a name relevant to "parameters"
        String q = "SELECT a FROM Artist a WHERE a.name LIKE :var";
        TypedQuery<Artist> query =  em.createQuery(q, Artist.class);
        query.setParameter("var", "%" + parameters + "%");
        artistResultsList = query.getResultList();
    }
    
    public void searchDate(){
        //Creates query that returns a list of tracks with a release date relevant to "parameters"
        
        /**
         * 
         * 
         * 
         */

    }
    
    /*
    public String isResultSingle(){
    
        if(resultsList != null){
            if(resultsList.size() > 1){
                return //string url result page
            }
        }
        return null;
    }
    */
    public void setParameters(String key){
        this.parameters = key;
    }
    
    public String getParameters(){
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

    
    
}

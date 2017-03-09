/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import persistence.controllers.TrackJpaController;
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
    private List resultsList;
    private String parameters;
    
    @Inject
    private TrackJpaController tjpac;
    
    @PersistenceContext
    private EntityManager em;
    

    
    
    /**
    public ArrayList searchAll(String str){
        //JPA queries
        //set resultsList
        //returns it
              
        return resultsList;
     * @return }**/
    
    //Default Search Criteria    
    public List<Track> searchTracks(){
        //Creates query that returns a list of tracks with a name relevant to "parameters"
        String q = "SELECT t FROM Track t WHERE t.name LIKE '%:p'";
        TypedQuery<Track> query =  em.createQuery(q, Track.class);
        query.setParameter("p", parameters);
        resultsList = query.getResultList();
        return resultsList;
    }
    
    
    public List<Album> searchAlbums(){
        //Creates query that returns a list of albums with a name relevant to "parameters"
        String q = "SELECT a FROM Album a WHERE a.name LIKE '%:p'";
        TypedQuery<Album> query =  em.createQuery(q, Album.class);
        query.setParameter("p", parameters);
        resultsList = query.getResultList();
        return resultsList;
    }

    public List<Artist> searchArtists(){
        //Creates query that returns a list of artists with a name relevant to "parameters"
        String q = "SELECT a FROM Artist a WHERE a.name LIKE '%:p'";
        TypedQuery<Artist> query =  em.createQuery(q, Artist.class);
        query.setParameter("p", parameters);
        resultsList = query.getResultList();
        return resultsList;
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

    public List getResultsList() {
        return resultsList;
    }

    public void setResultsList(List resultsList) {
        this.resultsList = resultsList;
    }
    
    
}

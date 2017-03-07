/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import java.io.Serializable;
import java.util.ArrayList;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import persistence.controllers.TrackJpaController;

/**
 *
 * @author Pierre Azelart
 */
@Named("Search")
@RequestScoped
public class UserSearch implements Serializable{
    private ArrayList resultsList;
    @Inject
    private TrackJpaController tjpac;
//    private EntityManager em;
    
    private String parameters;
    
    
    /**
    public ArrayList searchAll(String str){
        //JPA queries
        //set resultsList
        //returns it
              
        return resultsList;
     * @return }**/
    
    //Default Search Criteria    
    public ArrayList searchTracks(){
        
        
        return resultsList;
    }
    
    
    public ArrayList searchAlbums(){
        return resultsList;
    }

    public ArrayList searchArtists(){
        return resultsList;
    }
  
    public void setParameters(String key){
        this.parameters = key;
    }
    
    public String getParameters(){
        return this.parameters;
    }
}

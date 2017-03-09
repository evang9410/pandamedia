
package com.pandamedia.beans;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import persistence.controllers.ArtistJpaController;
import persistence.entities.Artist;

/**
 *
 * @author Naasir 
 */
@Named("artistBacking")
@SessionScoped
public class ArtistBackingBean implements Serializable {
    @Inject
    private ArtistJpaController artistController;
    private Artist artist;
    @PersistenceContext
    private EntityManager em;
    
    
    public Artist getArtist(){
        if(artist == null){
            artist = new Artist();
        }
        return artist;
    }
    
    /**
     * Finds the Artist from its id.
     * @param id
     * @return 
     */
    public Artist findArtistById(int id){
        artist = artistController.findArtist(id); 
        return artist;
    }
    
    public List<Artist> getAll()
    {
        return artistController.findArtistEntities();
    }
    
}

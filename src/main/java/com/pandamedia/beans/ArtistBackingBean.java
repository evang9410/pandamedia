
package com.pandamedia.beans;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import persistence.controllers.ArtistJpaController;
import persistence.entities.Artist;

/**
 * This class will be used as the artist backing bean. It is used as a means
 * of getting artists.
 * @author Naasir Jusab
 */
@Named("artistBacking")
@SessionScoped
public class ArtistBackingBean implements Serializable {
    private static final Logger LOG = Logger.getLogger("ArtistBackingBean.class");
    @Inject
    private ArtistJpaController artistController;
    private Artist artist;
    @PersistenceContext
    private EntityManager em;
    
    /**
     * This method will return an artist if it exists already. Otherwise, it will
     * return a new artist object.
     * @return artist
     */
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
    
    /**
     * This method will return all the artists in the database.
     * @return 
     */
    public List<Artist> getAll()
    {
        return artistController.findArtistEntities();
    }
    
    public void setArtist(Artist artist)
    {
        LOG.info("New artist id: " + artist.getId());
        this.artist = artist;
    }
}

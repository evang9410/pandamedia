/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backingbeans;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import persistence.controllers.AlbumJpaController;
import persistence.entities.Album;

/**
 *
 * @author Evang
 */
@Named("albumBacking")
@RequestScoped
public class AlbumBackingBean implements Serializable{
    @Inject
    private AlbumJpaController albumController;
    private Album album;
    @PersistenceContext
    private EntityManager em;
    
    
    public Album getAlbum(){
        if(album == null){
            album = new Album();
        }
        return album;
    }
    
    /**
     * Finds the album from its id.
     * @param id
     * @return 
     */
    public Album findAlbumById(int id){
        album = albumController.findAlbum(id); // questionable, do I set just the album variable and return void?
        // the purpose is to hold onto the ablum object so I don't have to keep htting the db everytime I want to use it.
        return album;
    }
    
    /**
     * returns a list of the latest (5) released albums/recently added to the database.
     * @return 
     */
    public List<Album> getLatestAlbums(){
        String q = "SELECT a FROM Album a ORDER BY a.releaseDate DESC";
        TypedQuery<Album> query =  em.createQuery(q, Album.class).setMaxResults(5);
        return query.getResultList();
    }
}

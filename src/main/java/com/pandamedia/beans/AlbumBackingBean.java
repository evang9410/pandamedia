    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandamedia.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import persistence.controllers.AlbumJpaController;
import persistence.entities.Album;
import persistence.entities.CoverArt;
import persistence.entities.Genre;

/**
 *
 * @author Evang
 */
@Named("albumBacking")
@SessionScoped
public class AlbumBackingBean implements Serializable{
    
    @Inject
    private AlbumJpaController albumController;
    private Album album;
    @PersistenceContext
    private EntityManager em;
    private String genreString;
    private List<Album> genrelist;
    
    public Album getAlbum(){
        if(album == null){
            album = new Album();
        }
        return album;
    }
    
    public AlbumBackingBean(){
        genrelist = new ArrayList();
    }

    public List<Album> getGenrelist() {
        return genrelist;
    }

    public void setGenrelist(List<Album> genrelist) {
        this.genrelist = genrelist;
    }
    
    

    public String getGenreString() {
        return genreString;
    }

    public void setGenreString(String genreString) {
        this.genreString = genreString;
    }
    
    
    
    /**
     * Finds the album from its id.
     * @param id
     * @return 
     */
    public Album findAlbumById(){
        //album = albumController.findAlbum(id); // questionable, do I set just the album variable and return void?
        // the purpose is to hold onto the ablum object so I don't have to keep htting the db everytime I want to use it.
        FacesContext context = FacesContext.getCurrentInstance();
        String album_id = context.getExternalContext().getRequestParameterMap()
                .get("albumId");
        int id = Integer.parseInt(album_id);
        album = albumController.findAlbum(id);
        return albumController.findAlbum(id);
        
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
    /**
     * Searches the database with the genre key term returns a list of albums.
     * @return 
     */
    public List<Album> getAlbumFromGenre(){
        if(genreString == null){
            return null;
        }
        int genre_id = getGenreId(genreString);
        String q = "SELECT a FROM Album a WHERE a.genreId.id = :genre_id";
        TypedQuery<Album> query = em.createQuery(q, Album.class).setMaxResults(5);
        query.setParameter("genre_id", genre_id);
        return query.getResultList();
    }
    
    private int getGenreId(String genre){
        String q = "SELECT g FROM Genre g WHERE g.name = :name";
        TypedQuery<Genre> query = em.createQuery(q, Genre.class);
        query.setParameter("name", genre);
        return query.getResultList().get(0).getId();//this should be query.getSingleResult, however, since we have like 5 genres with the same name with the 
        // test data, we get a list and get the first result, test data should have been sanitized.
    }
    
}

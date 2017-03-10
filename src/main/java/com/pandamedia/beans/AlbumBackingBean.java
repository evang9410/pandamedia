
package com.pandamedia.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
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
 * @author Evan Glicakis
 */
@Named("albumBacking")
@SessionScoped
public class AlbumBackingBean implements Serializable{
    
    @Inject
    private AlbumJpaController albumController;
    private Album album;
    private List<Album> albums;
    private List<Album> filteredAlbums;
    @PersistenceContext
    private EntityManager em;
    private String genreString;
    private List<Album> genrelist;
    private int albumid;
    
    
    
    public AlbumBackingBean(){
        genrelist = new ArrayList();
    }
    
    @PostConstruct
    public void init()
    {
        this.albums = albumController.findAlbumEntities();     
    }
    
    public List<Album> getAlbums()
    {
        return albums;
    }
    
    public void setAlbums(List<Album> albums)
    {
        this.albums = albums;
    }
    public Album getAlbum(){
        if(album == null){
            album = new Album();
        }
        return album;
    }

    public int getAlbumid() {
        return albumid;
    }

    public void setAlbumid(int albumid) {
        this.albumid = albumid;
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
     * sets the album variable and returns the string of the url to the album page
     * @param a
     * @return 
     */
    public String albumPage(Album a){
        this.album = a;
        return "album";
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
    
     public String addItem(Integer id) throws Exception
    {
        album = albumController.findAlbum(id);
        if(album.getRemovalStatus() != 0)
        {
            short i = 0;
            album.setRemovalStatus(i);
            album.setRemovalDate(null);

            albumController.edit(album);           
        }
        
        return null; 
    }
    
    public String removeItem(Integer id) throws Exception
    {       
        album = albumController.findAlbum(id);
        if(album.getRemovalStatus() != 1)
        {
            short i = 1;
            album.setRemovalStatus(i);
            album.setRemovalDate(Calendar.getInstance().getTime());

            albumController.edit(album);     
        }
        
        return null; 
    }
    
    public String loadEditForIndex(Integer id)
    {
        this.album = albumController.findAlbum(id);
        return "AlbumFunctionality/editAlbum.xhtml";
    }
    
    public String edit() throws Exception
    {
        albumController.edit(album);
        return "welcome_manager";
    }
       
    
    public void setFilteredAlbums(List<Album> filteredAlbums)
    {
        this.filteredAlbums = filteredAlbums;
    }
    
    public List<Album> getFilteredAlbums()
    {
        return this.filteredAlbums;
    }
    
    public List<Album> getAll()
    {
        return albumController.findAlbumEntities();
    }
    
}

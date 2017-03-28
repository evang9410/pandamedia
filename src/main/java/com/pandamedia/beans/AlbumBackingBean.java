package com.pandamedia.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import persistence.controllers.AlbumJpaController;
import persistence.entities.Album;
import persistence.entities.Album_;
import persistence.entities.Artist;
import persistence.entities.Genre;
import persistence.entities.InvoiceAlbum_;
import persistence.entities.InvoiceTrack_;
import persistence.entities.Invoice_;

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
    @PersistenceContext
    private EntityManager em;
    private String genreString;
    private List<Album> genrelist;
    private int albumid;
    
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
    
    public void setAlbum(Album album)
    {
        this.album = album;
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
     * sets the album variable and returns the string of the url to the album page
     * @param a
     * @return 
     */
    public String albumPage(Album a){
        this.album = a;
        System.out.println("" + a.getId() +"\n" + a.getTitle() +"\n" + a.getArtistId().getName());
        return "album";
    }
    /**
     * Gets the top selling albums of the current week.
     * @return 
     */
    public List<Album> getPopularAlbums(){
        Date startDate = new Date(); //get current date
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        System.out.println("endDate = "+ cal.getTime());
        System.out.println("startDate= "+startDate);
        Date endDate = cal.getTime();
        // Query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Album> albumRoot = query.from(Album.class);
        Join invoiceAlbumJoin = albumRoot.join(Album_.invoiceAlbumList);
        Join invoiceJoin = invoiceAlbumJoin.join(InvoiceTrack_.invoice);
        query.multiselect(cb.sum(invoiceAlbumJoin.get(InvoiceAlbum_.finalPrice)), albumRoot);
        query.groupBy(albumRoot);

        // Where clause
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.between(invoiceJoin.get(Invoice_.saleDate).as(Date.class), endDate, startDate));
        predicates.add(cb.equal(invoiceJoin.get(Invoice_.removalStatus), 0));
        predicates.add(cb.equal(invoiceAlbumJoin.get(InvoiceTrack_.removalStatus), 0));
        predicates.add(cb.equal(albumRoot.get(Album_.removalStatus), 0));
        query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

        // Order by clause
        query.orderBy(cb.asc(cb.sum(invoiceAlbumJoin.get(InvoiceAlbum_.finalPrice))));
        
        List<Album> albums = new ArrayList();
        TypedQuery<Object[]> typedQuery = em.createQuery(query).setMaxResults(6);
        List<Object[]> l = typedQuery.getResultList();
        for(Object[] o: l){
            System.out.println(((Album)o[1]).getTitle());
            albums.add((Album)o[1]);//retrieve the album id from the multiselect and cast the object, from id to album object.
        }
        return albums;
        
    }
    
    /**
     * Finds the album from its id.
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
        System.out.println(genreString);
        if(genreString == null){
            return null; 
        }
        int genre_id = getGenreId(genreString);
        String q = "SELECT a FROM Album a WHERE a.genreId.id = :genre_id";
        TypedQuery<Album> query = em.createQuery(q, Album.class).setMaxResults(5);
        query.setParameter("genre_id", genre_id);
        return query.getResultList();
    }
    /**
     * Gets the suggested albums for the album page, matching similar albums 
     * based on the genre of the current album.
     * @param genre
     * @return 
     */
    public List<Album> getSuggestedAlbums(String genre){
        genreString = genre;
        List<Album> list = getAlbumFromGenre();
        // if the list contains a reference to the current album object, remove it
        // from the suggested list.
        if(list.contains(album)){
            list.remove(album);
        }
        return list;
    }
    
    public List<Album> albumsFromArtist(Artist a){
        if(a != null){
            String q = "SELECT a FROM Album a WHERE a.artistId.id = :artist_id";
            TypedQuery<Album> query = em.createQuery(q, Album.class);
            query.setParameter("artist_id", a.getId());
            return query.getResultList();
        }
        return null;
    }
    
    private int getGenreId(String genre){
        String q = "SELECT g FROM Genre g WHERE g.name = :name";
        TypedQuery<Genre> query = em.createQuery(q, Genre.class);
        query.setParameter("name", genre);
//        return query.getResultList().get(0).getId();//this should be query.getSingleResult, however, since we have like 5 genres with the same name with the 
        // test data, we get a list and get the first result, test data should have been sanitized.
        return query.getSingleResult().getId();
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
    
}

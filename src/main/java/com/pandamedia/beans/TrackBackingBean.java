package com.pandamedia.beans;

import java.io.Serializable;
import persistence.controllers.TrackJpaController;
import persistence.entities.Track;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import persistence.controllers.TrackJpaController;
import persistence.entities.Album;
import persistence.entities.Review;
import persistence.entities.Track;

/**
 *
 * @author Naasir, Evan
 */
@Named("trackBacking")
@SessionScoped
public class TrackBackingBean implements Serializable{
    @Inject
    private TrackJpaController trackController;
    @PersistenceContext
    private EntityManager em;
    private Track track;
    private String genre_string;
    private boolean isTrackSales = true;
    
    public Track getTrack(){
        if(track == null){
            track = new Track();
        }
        return track;
    }
    
    public void setTrack(Track track){
        this.track = track;
    }

    public String trackPage(Track t){
        track = t;
        return "track";
    }
    /**
     * used to render the tracks on sale if there are any tracks on sale
     * @return 
     */
    public boolean isIsTrackSales() {
        return isTrackSales;
    }

    public void setIsTrackSales(boolean isTrackSales) {
        this.isTrackSales = isTrackSales;
    }
    

    /**
     * Finds the Track from its id.
     * @param id
     * @return 
     */
    public Track findTrackById(int id){
        track = trackController.findTrack(id); 
        return track;
    }
    
    public String addItem(Integer id) throws Exception
    {
        track = trackController.findTrack(id);
        if(track.getRemovalStatus() != 0)
        {
            short i = 0;
            track.setRemovalStatus(i);
            track.setRemovalDate(null);

            trackController.edit(track);

            
        }
        
        return null; 
    }
    
    public String removeItem(Integer id) throws Exception
    {
        track = trackController.findTrack(id);
        if(track.getRemovalStatus() != 1)
        {
            short i = 1;
            track.setRemovalStatus(i);
            track.setRemovalDate(Calendar.getInstance().getTime());

            trackController.edit(track);
        }
        
        return null; 
    }
    /**
     * iterates through the reviews for a track and returns a list of the approved
     * ratings.
     * @return 
     */
    public List<Review> getApprovedReviews(){
        int track_id = track.getId();
        String q = "SELECT r FROM Review r WHERE r.trackId.id = :trackId AND r.approvalStatus = :approval ORDER BY r.dateEntered DESC";
        TypedQuery<Review> query = em.createQuery(q, Review.class);
        query.setParameter("trackId", track_id);
        query.setParameter("approval", 1);
        return query.getResultList();
    }
    /**
     * this method is used to display the stars next to a review on the track page
     * it takes in the review rating, creates a List of elements that JSF will then iterate
     * through with <ui:repeat> this is the safer way of doing it rather than using
     * <c:forEach>
     * @param rating
     * @return 
     */
    public List getStarsList(int rating){
        List l = new ArrayList();
        for(int i = 0; i <= rating; i++){
            l.add(i);
        }
        return l;
    }
    /**
     * Returns a list of tracks that are on sale.
     * checks the database for tracks that have a sale price that is not 0.
     * @return 
     */
    public List<Track> getSaleTracks(){
        String q = "SELECT t FROM Track t WHERE t.salePrice != 0";
        TypedQuery<Track> query = em.createQuery(q, Track.class);
        if(query.getResultList().isEmpty()){
            isTrackSales = false; // there are no tracks on sale, render false.
        }
        return query.getResultList();
    }
     
}

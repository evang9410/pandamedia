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
import persistence.controllers.TrackJpaController;
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
    
    public List<Review> getApprovedReviews(){
        List<Review> approvedReviews = new ArrayList();
        short cond = 1;
        for(Review r : track.getReviewList()){
            if(r.getApprovalStatus() == cond){
                approvedReviews.add(r);
            }
        }
        System.out.println("approved reviews size: "  + approvedReviews.size());
        return approvedReviews;
    }
     
}

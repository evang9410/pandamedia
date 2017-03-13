
package com.pandamedia.beans;

import persistence.controllers.ReviewJpaController;
import persistence.entities.Review;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import persistence.entities.ShopUser;
import persistence.entities.Track;


/**
 *
 * @author Naasir, Evan
 */
@Named("reviewBacking")
@SessionScoped
public class ReviewBackingBean implements Serializable{
    @Inject
    private ReviewJpaController reviewController;
    private Review review;
    @PersistenceContext
    private EntityManager em;
    
    
    public Review getReview(){
        if(review == null){
            review = new Review();
        }
        return review;
    }
    
    /**
     * Finds the review from its id.
     * @param id
     * @return 
     */
    public Review findAlbumById(int id){
        review = reviewController.findReview(id); 
        return review;
    }
    
    public String removeItem(Integer id) throws Exception
    {
        
        review = reviewController.findReview(id);
        
        reviewController.destroy(review.getId());
        
        return null; 
    }
    
    public String approve(Integer id) throws Exception
    {
        review = reviewController.findReview(id);
        
        if(review.getApprovalStatus() != 1)
        {
            short i = 1;
            review.setApprovalStatus(i);

            reviewController.edit(review);
                    
        }
        
        return null;
    }
    
    public String disapprove(Integer id) throws Exception
    {
        review = reviewController.findReview(id);
        
        if(review.getApprovalStatus() != 0)
        {
            short i = 0;
            review.setApprovalStatus(i);

            reviewController.edit(review);
                    
        }
        
        return null;
    }
    /**
     * Action used by command button to submit the review, originally return void
     * to be used by ajax without reloading the page.
     * @param track
     * @param su 
     */
    public void submitReview(Track track, ShopUser su){
        Review r = new Review();
        short approvalStatus = 0; // review is not approved.
        r.setApprovalStatus(approvalStatus);
        Date currentDate = new Date(); // get current date.
        r.setDateEntered(currentDate);
        r.setReviewContent(this.review.getReviewContent());
        r.setRating(this.review.getRating());
        r.setTrackId(track);
        r.setUserId(su);
        
        System.out.println("id: " + r.getId() + "\n" +
        "approval status: " + r.getApprovalStatus() + "\n" +
        "curr date: " + r.getDateEntered() + "\n" +
        "content: " + r.getReviewContent() + "\n" +
        "rating: " + r.getRating() + "\n" +
        "submitted by: " + r.getUserId().getEmail() + "\n" +
        "submitted for: " + r.getTrackId().getTitle());
        
        try {
            reviewController.create(r);
        } catch (Exception ex) {
            Logger.getLogger(ReviewBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.review.setReviewContent(null);
        this.review.setRating(1);
    }
    
    
}

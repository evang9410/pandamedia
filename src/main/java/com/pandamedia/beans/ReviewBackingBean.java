
package com.pandamedia.beans;

import persistence.controllers.ReviewJpaController;
import persistence.entities.Review;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
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
        // set the review content and rating to their default state so the form can be cleared.
        this.review.setReviewContent(null);
        this.review.setRating(1);
    }

    /**
     * validation rule for review content field.
     * @param fc
     * @param c
     * @param obj 
     */
    public void validateReviewContent(FacesContext fc, UIComponent c, Object obj){
        if(obj == null){
            review.setReviewContent("Review message is empty");
            throw new ValidatorException(new FacesMessage("Review message is empty."));
        }
        String content = (String)obj;
        if(content.length() < 3 || content.length() > 2000){
            throw new ValidatorException(new FacesMessage("Your review must be between 3 and 2000 characters."));
        }
    }
    
    /**
     *
     * @param fc
     * @param c
     * @param obj
     */
    public void validateRating(FacesContext fc, UIComponent c, Object obj){
        if(obj == null){
            throw new ValidatorException(new FacesMessage("You must rate the track."));
        }
    }

    
}

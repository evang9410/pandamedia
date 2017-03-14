
package com.pandamedia.beans;

import persistence.controllers.ReviewJpaController;
import persistence.entities.Review;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;



/**
 *
 * @author Naasir
 */
@Named("reviewBacking")
@SessionScoped
public class ReviewBackingBean implements Serializable{
    @Inject
    private ReviewJpaController reviewController;
    private Review review;
    private List<Review> reviews;
    private List<Review> filteredReviews;
    @PersistenceContext
    private EntityManager em;
    
     @PostConstruct
    public void init()
    {
        this.reviews = reviewController.findReviewEntities();     
    }
    
    public List<Review> getReviews()
    {
        return reviews;
    }
    
    public void setReviews(List<Review> reviews)
    {
        this.reviews = reviews;
    }
    
    public void setFilteredReviews(List<Review> filteredReviews)
    {
        this.filteredReviews = filteredReviews;
    }
    
    public List<Review> getFilteredReviews()
    {
        return this.filteredReviews;
    }
    
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
    
    public List<Review> getAll()
    {
        return reviewController.findReviewEntities();
    }
    
    
}

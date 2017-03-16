
package com.pandamedia.beans;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import persistence.controllers.NewsfeedJpaController;
import persistence.entities.Newsfeed;

/**
 * This class will be used as the news feed backing bean. It is used as a means
 * of getting news feeds and querying them.
 * @author Naasir Jusab
 */
@Named("rssFeedBacking")
@SessionScoped
public class RSSFeedBackingBean implements Serializable{
    
    @Inject
    private NewsfeedJpaController newsFeedController;
    private Newsfeed newsFeed;
    @PersistenceContext
    private EntityManager em;
    
    /**
     * This method will return a news feed if it exists already. Otherwise, it 
     * will return a new news feed object.
     * @return news feed object
     */
    public Newsfeed getNewsFeed(){
        if(newsFeed == null){
            newsFeed = new Newsfeed();
        }
        return newsFeed;
    }
    
    /**
     * Finds the news feed from its id.
     * @param id of the news feed
     * @return news feed object
     */
    public Newsfeed findNewsFeedById(int id){
        newsFeed = newsFeedController.findNewsfeed(id); 
        return newsFeed;
    }
    
    /**
     * This method will return all the news feeds in the database so it can be 
     * displayed on the data table.
     * @return list of all the news feed
     */
    public List<Newsfeed> getAll()
    {
        return newsFeedController.findNewsfeedEntities();
    }
    
    /**
     * This method will save the news feed to the database and select
     * it so that the rss feed manager can change the news feed that is 
     * being displayed on the main page.
     * @return null should refresh the page
     */
    public String save()
    {
        try
        {
            newsFeedController.create(newsFeed);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        RSSFeedManager feedManager = new RSSFeedManager();
        feedManager.setUrl(newsFeed.getUrl());
        
        this.newsFeed = null;
        return null;
    }
    
    
    public String remove(Integer id)
    {
        try
        {
            newsFeedController.destroy(id);
        }
        
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        this.newsFeed = null;
        return null;
    }
    
    public String select(Integer id)
    {
        newsFeed = newsFeedController.findNewsfeed(id);
        RSSFeedManager feedManager = new RSSFeedManager();
        feedManager.setUrl(newsFeed.getUrl());
        
        this.newsFeed = null;
        return null;
    }
    
    
}

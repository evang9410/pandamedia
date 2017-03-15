
package com.pandamedia.beans;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import persistence.controllers.AdvertisementJpaController;
import persistence.entities.Advertisement;

/**
 * This class will be used as the banner ad backing bean. It is used as a means
 * of getting banner ads and querying them.
 * @author Naasir Jusab
 */
@Named("bannerAdBacking")
@SessionScoped
public class BannerAdBackingBean implements Serializable {
    
    @Inject
    private AdvertisementJpaController advertisementController;
    private Advertisement advertisement;
    @PersistenceContext
    private EntityManager em;
    
    /**
     * This method will return an ad if it exists already. Otherwise, it 
     * will return a new ad object.
     * @return ad object
     */
    public Advertisement getAdvertisement(){
        if(advertisement == null){
            advertisement = new Advertisement();
        }
        return advertisement;
    }
    
    /**
     * Finds the advertisement from its id.
     * @param id of the advertisement
     * @return advertisement object
     */
    public Advertisement findAdvertisementById(int id){
        advertisement = advertisementController.findAdvertisement(id);
        return advertisement;
    }
    
    /**
     * This method will return all the ads in the database so it can be 
     * displayed on the data table.
     * @return list of all the advertisements
     */
    public List<Advertisement> getAll()
    {
        return advertisementController.findAdvertisementEntities();
    }
    
    /**
     * This method will save the ad to the database and select it so that the 
     * manager can change the ad that is being displayed on the main page.
     * @return null should refresh the page
     */
    public String save()
    {
        try
        {
            advertisementController.create(advertisement);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        
        
        return null;
    }
    
    
    public String remove(Integer id)
    {
        try
        {
            advertisementController.destroy(id);
        }
        
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        
        return null;
    }
    
    public String select(Integer id)
    {
        advertisement = advertisementController.findAdvertisement(id);
        
        return null;
    }
    
}

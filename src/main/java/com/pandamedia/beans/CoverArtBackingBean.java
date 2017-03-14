
package com.pandamedia.beans;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import persistence.controllers.CoverArtJpaController;
import persistence.entities.CoverArt;


/**
 * This class will be used as the coverArt backing bean. It is used as a means
 * of getting cover arts.
 * @author Naasir 
 */
@Named("coverArtBacking")
@SessionScoped
public class CoverArtBackingBean implements Serializable {
    @Inject
    private CoverArtJpaController coverArtController;
    private CoverArt coverArt;
    @PersistenceContext
    private EntityManager em;
    
    
    public CoverArt getCoverArt(){
        if(coverArt == null){
            coverArt = new CoverArt();
        }
        return coverArt;
    }
    
    /**
     * Finds the CoverArt from its id.
     * @param id
     * @return 
     */
    public CoverArt findCoverArtById(int id){
        coverArt = coverArtController.findCoverArt(id); 
        return coverArt;
    }
    
    public List<CoverArt> getAll()
    {
        return coverArtController.findCoverArtEntities();
    }
    
}

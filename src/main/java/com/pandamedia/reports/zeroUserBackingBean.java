package com.pandamedia.reports;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import persistence.entities.ShopUser;

/**
 * Provides functionality to the Zero User Report page.
 * 
 * @author Erika Bourque
 */
@Named("zeroUser")
@RequestScoped
public class zeroUserBackingBean implements Serializable {
    public zeroUserBackingBean()
    {
        
    }
}

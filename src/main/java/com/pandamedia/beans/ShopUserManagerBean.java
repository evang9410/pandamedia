
package com.pandamedia.beans;

import persistence.controllers.ShopUserJpaController;
import persistence.entities.Invoice;
import persistence.entities.ShopUser;
import persistence.entities.Survey;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

/**
 *
 * @author Naasir
 */

@Named("userBacking")
@SessionScoped
public class ShopUserManagerBean implements Serializable{
    @Inject
    private ShopUserJpaController userController;
    private ShopUser user;
    @PersistenceContext
    private EntityManager em;
    
    
    public ShopUser getUser(){
        if(user == null){
            user = new ShopUser();
        }
        return user;
    }
    
    
    
    public String loadEdit(Integer id) throws Exception
    {
            return null;
    }
    
    

}
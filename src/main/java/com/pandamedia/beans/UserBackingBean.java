/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandamedia.beans;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import persistence.controllers.ShopUserJpaController;
import persistence.entities.ShopUser;

/**
 *
 * @author Evang
 */
@Named("user")
@SessionScoped
public class UserBackingBean implements Serializable{
    @Inject
    private ShopUserJpaController userController;
    @PersistenceContext
    private EntityManager em;   
    private ShopUser user;
    
    public ShopUser getUser(){
        if(user == null){
            user = new ShopUser();
        }
        return user;
    }
    /**
     * Temporary method to use a ShopUser object while testing, once login and
     * registration is done, we should be not using the method.
     * @param id 
     * @return 
     */
    public ShopUser findUser(int id){
        return userController.findShopUser(id);        
    }
}

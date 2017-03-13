
package com.pandamedia.beans;

import persistence.controllers.ShopUserJpaController;
import persistence.entities.Invoice;
import persistence.entities.ShopUser;
import persistence.entities.Survey;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
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
import persistence.entities.Album;

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
    private List<ShopUser> users;
    private List<ShopUser> filteredUsers;
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void init()
    {
        this.users = userController.findShopUserEntities();
    }
    
    public List<ShopUser> getUsers()
    {
        return users;
    }
    
    public void setUsers(List<ShopUser> users)
    {
        this.users = users;
    }
    
    public List<ShopUser> getFilteredUsers()
    {
        return filteredUsers;
    }
    
    public void setFilteredUsers(List<ShopUser> filteredUsers)
    {
        this.filteredUsers = filteredUsers;
    }
   
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
    
    public List<ShopUser> getAll()
    {
        return userController.findShopUserEntities();
    }
    
    public String loadEditForClients(Integer id)
    {
        this.user = userController.findShopUser(id);
        return "editClients.xhtml";
    }
    

}
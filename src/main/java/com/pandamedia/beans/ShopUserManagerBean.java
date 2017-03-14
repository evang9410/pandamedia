
package com.pandamedia.beans;

import persistence.controllers.ShopUserJpaController;
import persistence.entities.Invoice;
import persistence.entities.ShopUser;
import persistence.entities.Survey;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import persistence.entities.Invoice_;
import persistence.entities.ShopUser_;


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
        
    public List<ShopUser> getAll()
    {
        return userController.findShopUserEntities();
    }
    
    public String loadEditForClients(Integer id)
    {
        System.out.println(id);
        this.user = userController.findShopUser(id);
        return "ClientFunctionality/editClients.xhtml";
    }
    
    public String edit() throws Exception
    {
        userController.edit(user);
        return "welcome_clients";
    }
    
    public int getClientTotalPurchase(Integer id) 
    {
        // Query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<ShopUser> userRoot = query.from(ShopUser.class);
        Join invoiceJoin = userRoot.join(ShopUser_.invoiceList);
        query.multiselect(cb.sum(invoiceJoin.get(Invoice_.totalGrossValue)), userRoot);
        query.groupBy(userRoot.get(ShopUser_.id));

        // Where clause
        Predicate p1 = cb.equal(userRoot.get(ShopUser_.id), id);
        Predicate p2 = cb.equal(invoiceJoin.get(Invoice_.removalStatus), 0);
        query.where(cb.and(p1, p2));

        TypedQuery<Object[]> typedQuery = em.createQuery(query);
        return typedQuery.getFirstResult();
    }

}
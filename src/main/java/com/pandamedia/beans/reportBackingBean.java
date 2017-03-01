package com.pandamedia.beans;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import persistence.entities.Genre_;
import persistence.entities.Invoice;
import persistence.entities.Invoice_;
import persistence.entities.Province_;
import persistence.entities.ShopUser;
import persistence.entities.ShopUser_;

/**
 * This class provides common methods for the report pages.
 * 
 * @author Erika Bourque
 */
@Named
@RequestScoped
public class reportBackingBean implements Serializable{
    @PersistenceContext
    private EntityManager em;
    
    private Date startDate;
    private Date endDate;
    
    // TODO: should this be in a diff bean?
    public Date getDefaultEndDate()
    {               
        return Calendar.getInstance().getTime();
    }
    
    // TODO: should this be in a diff bean?
    public Date getDefaultStartDate()
    {
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_YEAR, -30);
        
        return start.getTime();
    }
    
    // TODO: should this be in a diff bean?
    public Date getStartDate()
    {
        return startDate;
    }
    
    // TODO: should this be in a diff bean?
    public Date getEndDate()
    {
        return endDate;
    }
    
    // TODO: should this be in a diff bean?
    public void setStartDate(Date date)
    {
        startDate = date;
    }
    
    // TODO: should this be in a diff bean?
    public void setEndDate(Date date)
    {
        endDate = date;
    }
    
    /**
     * This method returns a list of all the shop users that
     * did not make any purchases in the time frame specified.
     * 
     * @author  Erika Bourque
     * @return  The list of shop users
     */
    public List<Object[]> getZeroUsers()
    {        
        // Query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<ShopUser> userRoot = query.from(ShopUser.class);
        Join userProvince = userRoot.join(ShopUser_.provinceId);
        Join userGenre = userRoot.join(ShopUser_.lastGenreSearched);
//        Join userProvince = userRoot.join("provinceId");
//        Join userGenre = userRoot.join("lastGenreSearched");
        query.multiselect(userRoot.get(ShopUser_.id), userRoot.get(ShopUser_.title), userRoot.get(ShopUser_.lastName), 
                userRoot.get(ShopUser_.firstName), userRoot.get(ShopUser_.companyName), userRoot.get(ShopUser_.streetAddress), 
                userRoot.get(ShopUser_.city), userProvince.get(Province_.name), userRoot.get(ShopUser_.country), 
                userRoot.get(ShopUser_.postalCode), userRoot.get(ShopUser_.homePhone), userRoot.get(ShopUser_.cellPhone), 
                userRoot.get(ShopUser_.email), userGenre.get(Genre_.name), userRoot.get(ShopUser_.isManager));
//        query.multiselect(userRoot.get("id"), userRoot.get("title"), userRoot.get("lastName"), 
//                userRoot.get("firstName"), userRoot.get("companyName"), userRoot.get("streetAddress"), 
//                userRoot.get("city"), userProvince.get("name"), userRoot.get("country"), 
//                userRoot.get("postalCode"), userRoot.get("homePhone"), userRoot.get("cellPhone"), 
//                userRoot.get("email"), userGenre.get("name"), userRoot.get("isManager"));

        // Subquery
        Subquery<Invoice> subquery = query.subquery(Invoice.class);
        Root<Invoice> invoiceRoot = subquery.from(Invoice.class);
        subquery.select(invoiceRoot);
        subquery.where(cb.equal(invoiceRoot.get(Invoice_.userId), userRoot));
//        subquery.where(cb.equal(invoiceRoot.get("userId"), userRoot));
        // TODO: Missing timeframes, also in params 

        // Putting them together
        query.where(cb.not(cb.exists(subquery)));
        TypedQuery<Object[]> typedQuery = em.createQuery(query);

        return typedQuery.getResultList();
    }
}

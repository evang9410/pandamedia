package com.pandamedia.beans;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import persistence.entities.Invoice;
import persistence.entities.ShopUser;

/**
 * This class provides common methods for the report pages, and
 * keeps track of the desired start and end dates of the current report.
 *
 * @author Erika Bourque
 */
@Named("reports")
@RequestScoped
public class ReportBackingBean implements Serializable {
    private static final Logger LOG = Logger.getLogger("ReportBackingBean.class");

    @PersistenceContext
    private EntityManager em;

    private Date startDate;
    private Date endDate;

    public ReportBackingBean() {
    }

    // TODO: should this be in a diff bean?
    public Date getDefaultEndDate() {
        return Calendar.getInstance().getTime();
    }

    // TODO: should this be in a diff bean?
    public Date getDefaultStartDate() {
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_YEAR, -30);

        return start.getTime();
    }

    // TODO: should this be in a diff bean?
    public Date getStartDate() {
        if (startDate == null) {
            startDate = getDefaultStartDate();
        }
        return startDate;
    }

    // TODO: should this be in a diff bean?
    public Date getEndDate() {
        if (endDate == null) {
            endDate = getDefaultEndDate();
        }
        return endDate;
    }

    // TODO: should this be in a diff bean?
    public void setStartDate(Date date) {
        LOG.log(Level.INFO, "--- New start date: {0}", date);
        LOG.log(Level.INFO, "--- Current end date: {0}", endDate);
        startDate = date;
    }

    // TODO: should this be in a diff bean?
    public void setEndDate(Date date) {
        LOG.log(Level.INFO, "--- New end date: {0}", date);
        LOG.log(Level.INFO, "--- Current start date: {0}", startDate);
        endDate = date;
    }

    /**
     * This method returns a list of all the shop users that did not make any
     * purchases in the time frame specified.
     *
     * @author Erika Bourque
     * @return The list of shop users
     */
   /* public List<Object[]> getZeroUsers() {
        LOG.log(Level.INFO, "Zero Users start date: {0}", startDate);
        LOG.log(Level.INFO, "Zero Users end date: {0}", endDate);

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

        // Using predicates to avoid compiler errors, does not like CriteriaBuilder's between method
        Predicate p1 = cb.equal(invoiceRoot.get(Invoice_.userId), userRoot);
        Predicate p2 = cb.between(invoiceRoot.get(Invoice_.saleDate).as(Date.class), getStartDate(), getEndDate());
        subquery.where(cb.and(p1, p2));

        // Putting them together
        query.where(cb.not(cb.exists(subquery)));
        TypedQuery<Object[]> typedQuery = em.createQuery(query);

        return typedQuery.getResultList();
    }*/
}

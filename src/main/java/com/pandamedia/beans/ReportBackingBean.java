package com.pandamedia.beans;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import persistence.entities.Invoice;
import persistence.entities.InvoiceTrack;
import persistence.entities.InvoiceTrackPK_;
import persistence.entities.InvoiceTrack_;
import persistence.entities.Invoice_;
import persistence.entities.ShopUser;
import persistence.entities.ShopUser_;
import persistence.entities.Track;
import persistence.entities.Track_;

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

    public ReportBackingBean() {
    }

    /**
     * This method returns a list of all the shop users that did not make any
     * purchases in the time frame specified.
     *
     * @author Erika Bourque
     * @param startDate         The report's start date
     * @param endDate           The report's end date
     * @return                  The list of shop users
     */
    public List<ShopUser> getZeroUsers(Date startDate, Date endDate) {
        String logMsg = "Zero Users\tStart: " + startDate + "\tEnd: " + endDate;
        LOG.log(Level.INFO, logMsg);

        // Query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ShopUser> query = cb.createQuery(ShopUser.class);
        Root<ShopUser> userRoot = query.from(ShopUser.class);
        query.select(userRoot);

        // Subquery
        Subquery<Invoice> subquery = query.subquery(Invoice.class);
        Root<Invoice> invoiceRoot = subquery.from(Invoice.class);
        subquery.select(invoiceRoot);

        // Using predicates to avoid compiler errors, does not like CriteriaBuilder's between method
        Predicate p1 = cb.equal(invoiceRoot.get(Invoice_.userId), userRoot);
        Predicate p2 = cb.between(invoiceRoot.get(Invoice_.saleDate).as(Date.class), startDate, endDate);
        Predicate p3 = cb.equal(invoiceRoot.get(Invoice_.removalStatus), 0);
        subquery.where(cb.and(cb.and(p1, p2), p3));

        // Putting them together
        query.where(cb.not(cb.exists(subquery)));
        TypedQuery<ShopUser> typedQuery = em.createQuery(query);

        return typedQuery.getResultList();
    }
    
    public List<Track> getZeroTracks(Date startDate, Date endDate)
    {
        String logMsg = "Zero Tracks\tStart: " + startDate + "\tEnd: " + endDate;
        LOG.log(Level.INFO, logMsg);

        // Query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Track> query = cb.createQuery(Track.class);
        Root<Track> trackRoot = query.from(Track.class);
        query.select(trackRoot);

        // Subquery
        Subquery<InvoiceTrack> subquery = query.subquery(InvoiceTrack.class);
        Root<InvoiceTrack> invoiceTrackRoot = subquery.from(InvoiceTrack.class);
        subquery.select(invoiceTrackRoot);
        Join invoiceJoin = invoiceTrackRoot.join(InvoiceTrack_.invoice);

        // Using predicates to avoid compiler errors, does not like CriteriaBuilder's between method
        Predicate p1 = cb.equal(invoiceTrackRoot.get(InvoiceTrack_.invoiceTrackPK).get(InvoiceTrackPK_.trackId), trackRoot.get(Track_.id));
        Predicate p2 = cb.between(invoiceJoin.get(Invoice_.saleDate).as(Date.class), startDate, endDate);
        Predicate p3 = cb.equal(invoiceTrackRoot.get(InvoiceTrack_.removalStatus), 0);
        Predicate p4 = cb.equal(invoiceJoin.get(Invoice_.removalStatus), 0);
        subquery.where(cb.and(cb.and(p1, p2), cb.and(p3, p4)));

        // Putting them together
        query.where(cb.and(cb.not(cb.exists(subquery)), cb.equal(trackRoot.get(Track_.removalStatus), 0)));
        
        TypedQuery<Track> typedQuery = em.createQuery(query);
        return typedQuery.getResultList();
    }
    
    public List<Object[]> getTopClients(Date startDate, Date endDate)
    {
        String logMsg = "Top Clients\tStart: " + startDate + "\tEnd: " + endDate;
        LOG.log(Level.INFO, logMsg);
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<ShopUser> userRoot = query.from(ShopUser.class);
        
        // Join
        ListJoin invoiceJoin = userRoot.join(ShopUser_.invoiceList, JoinType.LEFT);
        
        // Select
        query.multiselect(cb.sum(invoiceJoin.get(Invoice_.totalGrossValue)), userRoot);
        query.groupBy(userRoot.get(ShopUser_.id));
        
        // Where
        Predicate p1 = cb.between(invoiceJoin.get(Invoice_.saleDate).as(Date.class), startDate, endDate);
        Predicate p2 = cb.equal(invoiceJoin.get(Invoice_.removalStatus), 0);
        query.where(cb.and(p1, p2));
        
        // Order By
        query.orderBy(cb.desc(cb.sum(invoiceJoin.get(Invoice_.totalGrossValue))));
        
        TypedQuery<Object[]> typedQuery = em.createQuery(query);
        return typedQuery.getResultList();
    }
    
//    public List<Object[]> getTopSellers(Date startDate, Date endDate)
//    {
//        
//    }
}

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
import persistence.entities.InvoiceTrack;
import persistence.entities.InvoiceTrackPK_;
import persistence.entities.InvoiceTrack_;
import persistence.entities.Invoice_;
import persistence.entities.ShopUser;
import persistence.entities.Track;

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
    public List<ShopUser> getZeroUsers() {
        LOG.log(Level.INFO, "Zero Users start date: {0}", startDate);
        LOG.log(Level.INFO, "Zero Users end date: {0}", endDate);

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
        Predicate p2 = cb.between(invoiceRoot.get(Invoice_.saleDate).as(Date.class), getStartDate(), getEndDate());
        subquery.where(cb.and(p1, p2));
        // TODO: and invoice not removed

        // Putting them together
        query.where(cb.not(cb.exists(subquery)));
        TypedQuery<ShopUser> typedQuery = em.createQuery(query);

        return typedQuery.getResultList();
    }
    
    public List<Track> getZeroTracks()
    {
        LOG.log(Level.INFO, "Zero tracks start date: {0}", startDate);
        LOG.log(Level.INFO, "Zero tracks end date: {0}", endDate);

        // Query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Track> query = cb.createQuery(Track.class);
        Root<Track> trackRoot = query.from(Track.class);
        query.select(trackRoot).distinct(true);

        // Subquery
        Subquery<InvoiceTrack> subquery = query.subquery(InvoiceTrack.class);
        Root<InvoiceTrack> invoiceTrackRoot = subquery.from(InvoiceTrack.class);
        subquery.select(invoiceTrackRoot);
        Join invoiceJoin = invoiceTrackRoot.join(InvoiceTrack_.invoice);

        // Using predicates to avoid compiler errors, does not like CriteriaBuilder's between method
        Predicate p1 = cb.equal(invoiceTrackRoot.get(InvoiceTrack_.invoiceTrackPK).get(InvoiceTrackPK_.trackId), trackRoot);
        Predicate p2 = cb.between(invoiceJoin.get(Invoice_.saleDate).as(Date.class), getStartDate(), getEndDate());
        subquery.where(cb.and(p1, p2));
//        subquery.where(p2);
        // TODO: and invoice not removed
        // TODO: and track not removed

        // Putting them together
        query.where(cb.not(cb.exists(subquery)));
        TypedQuery<Track> typedQuery = em.createQuery(query);

        return typedQuery.getResultList();
    }
}

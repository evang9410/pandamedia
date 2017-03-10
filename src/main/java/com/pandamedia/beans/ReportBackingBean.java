package com.pandamedia.beans;

import java.io.Serializable;
import java.util.ArrayList;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
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
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import persistence.entities.Album;
import persistence.entities.Album_;
import persistence.entities.Artist;
import persistence.entities.Invoice;
import persistence.entities.InvoiceAlbum;
import persistence.entities.InvoiceAlbum_;
import persistence.entities.InvoiceTrack;
import persistence.entities.InvoiceTrackPK_;
import persistence.entities.InvoiceTrack_;
import persistence.entities.Invoice_;
import persistence.entities.ShopUser;
import persistence.entities.ShopUser_;
import persistence.entities.Track;
import persistence.entities.Track_;

/**
 * This class provides common methods for the report pages.
 *
 * @author Erika Bourque
 */
@Named("reports")
@RequestScoped
public class ReportBackingBean implements Serializable {

    // TODO: add checks for was it valid during report time and now is removed
    // TODO: valid all values coming in are good ones
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
     * @param startDate The report's start date
     * @param endDate The report's end date
     * @return The list of shop users
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

    public List<Track> getZeroTracks(Date startDate, Date endDate) {
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

    public List<Object[]> getTopClients(Date startDate, Date endDate) {
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

    public List<Object[]> getTopTrackSellers(Date startDate, Date endDate) {
        String logMsg = "Top Track Sellers\tStart: " + startDate + "\tEnd: " + endDate;
        LOG.log(Level.INFO, logMsg);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Track> trackRoot = query.from(Track.class);

        // Joins
        ListJoin invoiceTrackJoin = trackRoot.join(Track_.invoiceTrackList, JoinType.LEFT);
        Join invoiceJoin = invoiceTrackJoin.join(InvoiceTrack_.invoice);

        // Select
        query.multiselect(cb.count(invoiceTrackJoin), trackRoot);
        query.groupBy(trackRoot.get(Track_.id));

        // Where
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.between(invoiceJoin.get(Invoice_.saleDate).as(Date.class), startDate, endDate));
        predicates.add(cb.equal(invoiceJoin.get(Invoice_.removalStatus), 0));
        predicates.add(cb.equal(invoiceTrackJoin.get(InvoiceTrack_.removalStatus), 0));
        predicates.add(cb.equal(trackRoot.get(Track_.removalStatus), 0));
        query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

        // Order by
        query.orderBy(cb.desc(cb.count(invoiceTrackJoin)));

        TypedQuery<Object[]> typedQuery = em.createQuery(query);
        return typedQuery.getResultList();
    }

    public List<Object[]> getTopAlbumSellers(Date startDate, Date endDate) {
        String logMsg = "Top Album Sellers\tStart: " + startDate + "\tEnd: " + endDate;
        LOG.log(Level.INFO, logMsg);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Album> albumRoot = query.from(Album.class);

        // Joins
        ListJoin invoiceAlbumJoin = albumRoot.join(Album_.invoiceAlbumList, JoinType.LEFT);
        Join invoiceJoin = invoiceAlbumJoin.join(InvoiceTrack_.invoice);

        // Select
        query.multiselect(cb.count(invoiceAlbumJoin), albumRoot);
        query.groupBy(albumRoot.get(Album_.id));

        // Where
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.between(invoiceJoin.get(Invoice_.saleDate).as(Date.class), startDate, endDate));
        predicates.add(cb.equal(invoiceJoin.get(Invoice_.removalStatus), 0));
        predicates.add(cb.equal(invoiceAlbumJoin.get(InvoiceTrack_.removalStatus), 0));
        predicates.add(cb.equal(albumRoot.get(Album_.removalStatus), 0));
        query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

        // Order by
        query.orderBy(cb.desc(cb.count(invoiceAlbumJoin)));

        TypedQuery<Object[]> typedQuery = em.createQuery(query);
        return typedQuery.getResultList();
    }

    public List<Object[]> getTotalTrackSalesDetails(Date startDate, Date endDate) {
        // TODO add way to get zeros if no sales have been made

        String logMsg = "Total Track Sales\tStart: " + startDate + "\tEnd: " + endDate;
        LOG.log(Level.INFO, logMsg);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Track> trackRoot = query.from(Track.class);
//        Root<InvoiceTrack> invoiceTrackRoot = query.from(InvoiceTrack.class);
        Join<Track, InvoiceTrack> invoiceTrackJoin = trackRoot.join(Track_.invoiceTrackList, JoinType.LEFT);
        query.groupBy(trackRoot.get(Track_.id));
        Join invoiceJoin = invoiceTrackJoin.join(InvoiceTrack_.invoice);
//        Join trackJoin = invoiceTrackRoot.join(InvoiceTrack_.track, JoinType.LEFT);

        // Subquery
//        Subquery<Double> salesTotal = query.subquery(Double.class);
//        Root<InvoiceTrack> invoiceTrackRoot = salesTotal.from(InvoiceTrack.class);
////        Join invoiceJoin = invoiceTrackRoot.join(InvoiceTrack_.invoice);
//        salesTotal.select(cb.sum(invoiceTrackRoot.get(InvoiceTrack_.finalPrice)));
//        salesTotal.groupBy(invoiceTrackRoot.get(InvoiceTrack_.track).get(Track_.id));
        // Subquery Where
        List<Predicate> predicates = new ArrayList<>();
//        predicatesSub.add(cb.between(invoiceJoin.get(Invoice_.saleDate).as(Date.class), startDate, endDate));
//        predicatesSub.add(cb.equal(invoiceJoin.get(Invoice_.removalStatus), 0));
//        predicatesSub.add(cb.equal(invoiceTrackRoot.get(InvoiceTrack_.track), trackRoot.get(Track_.id)));
//        salesTotal.where(cb.and(predicatesSub.toArray(new Predicate[predicatesSub.size()])));

        predicates.add(cb.between(invoiceJoin.get(Invoice_.saleDate).as(Date.class), startDate, endDate));
        predicates.add(cb.equal(invoiceJoin.get(Invoice_.removalStatus), 0));
        predicates.add(cb.equal(trackRoot.get(Track_.removalStatus), 0));
        query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

        // Where
//        List<Predicate> predicatesQuery = new ArrayList<>();
//        predicatesQuery.add(cb.equal(trackRoot.get(Track_.removalStatus), 0));
        // Order by
        query.orderBy(cb.asc(trackRoot));

        query.multiselect(cb.sum(invoiceTrackJoin.get(InvoiceTrack_.finalPrice)), trackRoot);
//        query.multiselect(salesTotal.getSelection(), trackRoot);

        TypedQuery<Object[]> typedQuery = em.createQuery(query);
        return typedQuery.getResultList();
    }

    public List<Object[]> getTotalAlbumSalesDetails(Date startDate, Date endDate) {
        // TODO add way to get zeros if no sales have been made
        // TODO add total cost and total profit

        String logMsg = "Total Album Sales\tStart: " + startDate + "\tEnd: " + endDate;
        LOG.log(Level.INFO, logMsg);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Album> albumRoot = query.from(Album.class);
//        Root<InvoiceTrack> invoiceTrackRoot = query.from(InvoiceTrack.class);
        Join<Album, InvoiceAlbum> invoiceAlbumJoin = albumRoot.join(Album_.invoiceAlbumList, JoinType.LEFT);
        query.groupBy(albumRoot.get(Album_.id));
        Join invoiceJoin = invoiceAlbumJoin.join(InvoiceAlbum_.invoice);
//        Join trackJoin = invoiceTrackRoot.join(InvoiceTrack_.track, JoinType.LEFT);

        // Subquery
//        Subquery<Double> salesTotal = query.subquery(Double.class);
//        Root<InvoiceTrack> invoiceTrackRoot = salesTotal.from(InvoiceTrack.class);
////        Join invoiceJoin = invoiceTrackRoot.join(InvoiceTrack_.invoice);
//        salesTotal.select(cb.sum(invoiceTrackRoot.get(InvoiceTrack_.finalPrice)));
//        salesTotal.groupBy(invoiceTrackRoot.get(InvoiceTrack_.track).get(Track_.id));
        // Subquery Where
        List<Predicate> predicates = new ArrayList<>();
//        predicatesSub.add(cb.between(invoiceJoin.get(Invoice_.saleDate).as(Date.class), startDate, endDate));
//        predicatesSub.add(cb.equal(invoiceJoin.get(Invoice_.removalStatus), 0));
//        predicatesSub.add(cb.equal(invoiceTrackRoot.get(InvoiceTrack_.track), trackRoot.get(Track_.id)));
//        salesTotal.where(cb.and(predicatesSub.toArray(new Predicate[predicatesSub.size()])));

        predicates.add(cb.between(invoiceJoin.get(Invoice_.saleDate).as(Date.class), startDate, endDate));
        predicates.add(cb.equal(invoiceJoin.get(Invoice_.removalStatus), 0));
        predicates.add(cb.equal(albumRoot.get(Album_.removalStatus), 0));
        query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

        // Where
//        List<Predicate> predicatesQuery = new ArrayList<>();
//        predicatesQuery.add(cb.equal(trackRoot.get(Track_.removalStatus), 0));
        // Order by
        query.orderBy(cb.asc(albumRoot));

        query.multiselect(cb.sum(invoiceAlbumJoin.get(InvoiceAlbum_.finalPrice)), 
                albumRoot);
//        query.multiselect(salesTotal.getSelection(), trackRoot);

        TypedQuery<Object[]> typedQuery = em.createQuery(query);
        return typedQuery.getResultList();
    }
    
    // TODO need to test
    public List<Object[]> getSalesByTrack(Date startDate, Date endDate, Track track)
    {
        if (track == null)
        {
            String logMsg = "Sales By Track\tStart: " + startDate + "\tEnd: " + endDate + "\tTrack: null";
            LOG.log(Level.INFO, logMsg);
            return null;
        }
        
        String logMsg = "Sales By Track\tStart: " + startDate + "\tEnd: " + endDate + "\tTrack: " + track.getId();
        LOG.log(Level.INFO, logMsg);
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<InvoiceTrack> invoiceTrackRoot = query.from(InvoiceTrack.class);
        Join invoiceJoin = invoiceTrackRoot.join(InvoiceTrack_.invoice);
        
        List<Predicate> predicates = new ArrayList<>();
        // might cause error, do id to id if needed
        predicates.add(cb.equal(invoiceTrackRoot.get(InvoiceTrack_.track), track));
        predicates.add(cb.between(invoiceJoin.get(Invoice_.saleDate).as(Date.class), startDate, endDate));
        predicates.add(cb.equal(invoiceJoin.get(Invoice_.removalStatus), 0));
        query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        
        query.multiselect(invoiceJoin.get(Invoice_.saleDate),
                invoiceTrackRoot.get(InvoiceTrack_.finalPrice), 
                invoiceTrackRoot.get(InvoiceTrack_.track).get(Track_.costPrice),
                cb.diff(invoiceTrackRoot.get(InvoiceTrack_.finalPrice), invoiceTrackRoot.get(InvoiceTrack_.track).get(Track_.costPrice)),
                invoiceJoin.get(Invoice_.id),
                invoiceJoin.get(Invoice_.userId));
        
        TypedQuery<Object[]> typedQuery = em.createQuery(query);
        return typedQuery.getResultList();
    }
    
    public List<Object[]> getSalesByArtistTracks(Date startDate, Date endDate, Artist artist)
    {
        if (artist == null)
        {
            // TODO should this default to artist #1?
            String logMsg = "Sales By Artist Tracks\tStart: " + startDate + "\tEnd: " + endDate + "\tArtist: null";
            LOG.log(Level.INFO, logMsg);
            return null;
        }
        
        String logMsg = "Sales By Artist Tracks\tStart: " + startDate + "\tEnd: " + endDate + "\tArtist: " + artist.getId();
        LOG.log(Level.INFO, logMsg);
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<InvoiceTrack> invoiceTrackRoot = query.from(InvoiceTrack.class);
        Join invoiceJoin = invoiceTrackRoot.join(InvoiceTrack_.invoice);
        Join trackJoin = invoiceTrackRoot.join(InvoiceTrack_.track);
        
        List<Predicate> predicates = new ArrayList<>();
        // might cause error, do id to id if needed
        predicates.add(cb.equal(trackJoin.get(Track_.artistId), artist));
        predicates.add(cb.between(invoiceJoin.get(Invoice_.saleDate).as(Date.class), startDate, endDate));
        predicates.add(cb.equal(invoiceJoin.get(Invoice_.removalStatus), 0));
        query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        
        query.multiselect(invoiceJoin.get(Invoice_.saleDate),
                trackJoin,
                invoiceTrackRoot.get(InvoiceTrack_.finalPrice), 
                invoiceTrackRoot.get(InvoiceTrack_.track).get(Track_.costPrice),
                cb.diff(invoiceTrackRoot.get(InvoiceTrack_.finalPrice), trackJoin.get(Track_.costPrice)),
                invoiceJoin,
                invoiceJoin.get(Invoice_.userId));
        
        TypedQuery<Object[]> typedQuery = em.createQuery(query);
        
        LOG.info("size of list = " + typedQuery.getResultList().size());
        return typedQuery.getResultList();
    }
    
    public List<Object[]> getSalesByArtistAlbums(Date startDate, Date endDate, Artist artist)
    {
        if (artist == null)
        {
            // TODO should this default to artist #1?
            String logMsg = "Sales By Artist Albums\tStart: " + startDate + "\tEnd: " + endDate + "\tArtist: null";
            LOG.log(Level.INFO, logMsg);
            return null;
        }
        
        String logMsg = "Sales By Artist Albums\tStart: " + startDate + "\tEnd: " + endDate + "\tArtist: " + artist.getId();
        LOG.log(Level.INFO, logMsg);
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<InvoiceAlbum> invoiceAlbumRoot = query.from(InvoiceAlbum.class);
        Join invoiceJoin = invoiceAlbumRoot.join(InvoiceAlbum_.invoice);
        Join albumJoin = invoiceAlbumRoot.join(InvoiceAlbum_.album);
        
        List<Predicate> predicates = new ArrayList<>();
        // might cause error, do id to id if needed
        predicates.add(cb.equal(albumJoin.get(Album_.artistId), artist));
        predicates.add(cb.between(invoiceJoin.get(Invoice_.saleDate).as(Date.class), startDate, endDate));
        predicates.add(cb.equal(invoiceJoin.get(Invoice_.removalStatus), 0));
        query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        
        query.multiselect(invoiceJoin.get(Invoice_.saleDate),
                albumJoin,
                invoiceAlbumRoot.get(InvoiceAlbum_.finalPrice), 
                invoiceAlbumRoot.get(InvoiceAlbum_.album).get(Album_.costPrice),
                cb.diff(invoiceAlbumRoot.get(InvoiceAlbum_.finalPrice), albumJoin.get(Album_.costPrice)),
                invoiceJoin,
                invoiceJoin.get(Invoice_.userId));
        
        TypedQuery<Object[]> typedQuery = em.createQuery(query);
        
        LOG.info("size of list = " + typedQuery.getResultList().size());
        return typedQuery.getResultList();
    }
    
    // TODO need to test
    public List<Object[]> getSalesByAlbum(Date startDate, Date endDate, Album album)
    {
        if (album == null)
        {
            String logMsg = "Sales By Album\tStart: " + startDate + "\tEnd: " + endDate + "\tAlbum: null";
            LOG.log(Level.INFO, logMsg);
            return null;
        }
        
        String logMsg = "Sales By Album\tStart: " + startDate + "\tEnd: " + endDate + "\tAlbum: " + album.getId();
        LOG.log(Level.INFO, logMsg);
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<InvoiceAlbum> invoiceAlbumRoot = query.from(InvoiceAlbum.class);
        Join invoiceJoin = invoiceAlbumRoot.join(InvoiceAlbum_.invoice);
        
        List<Predicate> predicates = new ArrayList<>();
        // might cause error, do id to id if needed
        predicates.add(cb.equal(invoiceAlbumRoot.get(InvoiceAlbum_.album), album));
        predicates.add(cb.between(invoiceJoin.get(Invoice_.saleDate).as(Date.class), startDate, endDate));
        predicates.add(cb.equal(invoiceJoin.get(Invoice_.removalStatus), 0));
        query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        
        query.multiselect(invoiceJoin.get(Invoice_.saleDate),
                invoiceAlbumRoot.get(InvoiceAlbum_.finalPrice), 
                invoiceAlbumRoot.get(InvoiceAlbum_.album).get(Album_.costPrice),
                cb.diff(invoiceAlbumRoot.get(InvoiceAlbum_.finalPrice), invoiceAlbumRoot.get(InvoiceAlbum_.album).get(Album_.costPrice)),
                invoiceJoin.get(Invoice_.id),
                invoiceJoin.get(Invoice_.userId));
        
        TypedQuery<Object[]> typedQuery = em.createQuery(query);
        return typedQuery.getResultList();
    }
}
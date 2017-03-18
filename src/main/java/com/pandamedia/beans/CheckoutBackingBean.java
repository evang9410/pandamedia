package com.pandamedia.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import persistence.controllers.InvoiceJpaController;
import persistence.entities.Album;
import persistence.entities.Invoice;
import persistence.entities.InvoiceAlbum;
import persistence.entities.InvoiceTrack;
import persistence.entities.ShopUser;
import persistence.entities.Track;

/**
 *
 * @author Erika Bourque
 */
@Named("checkout")
@RequestScoped
public class CheckoutBackingBean implements Serializable {

    @Inject
    private ShoppingCart cart;

    @Inject
    private ShopUser user;
    
    @Inject
    private InvoiceJpaController invoiceController;

    private double gst;
    private double hst;
    private double pst;
    private double total;

    @PostConstruct
    public void init() {
        gst = cart.getSubTotal() * user.getProvinceId().getGstRate();
        pst = cart.getSubTotal() * user.getProvinceId().getPstRate();
        hst = cart.getSubTotal() * user.getProvinceId().getHstRate();
        total = cart.getSubTotal() + gst + pst + hst;
    }

    public List<SelectItem> getMonthSelector() {
        List<SelectItem> list = new ArrayList<>();
        list.add(new SelectItem(1, "01"));
        list.add(new SelectItem(2, "02"));
        list.add(new SelectItem(3, "03"));
        list.add(new SelectItem(4, "04"));
        list.add(new SelectItem(5, "05"));
        list.add(new SelectItem(6, "06"));
        list.add(new SelectItem(7, "07"));
        list.add(new SelectItem(8, "08"));
        list.add(new SelectItem(9, "09"));
        list.add(new SelectItem(10, "10"));
        list.add(new SelectItem(11, "11"));
        list.add(new SelectItem(12, "12"));
        return list;
    }

    public List<SelectItem> getYearSelector() {
        List<SelectItem> list = new ArrayList<>();
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        int maxYears = 10;

        for (int i = 0; i < maxYears; i++) {
            list.add(new SelectItem(curYear + i));
        }

        return list;
    }

    public double getGst() {
        return gst;
    }

    public double getPst() {
        return pst;
    }

    public double getHst() {
        return hst;
    }

    public double getTotal() {
        return total;
    }

    public String buildInvoice() throws Exception {
        // TODO: verify that this actually works
        Invoice invoice = new Invoice();
        
        // Setting invoice details
        invoice.setSaleDate(Calendar.getInstance().getTime());
        invoice.setTotalGrossValue(cart.getSubTotal());
        invoice.setGstTax(gst);
        invoice.setHstTax(hst);
        invoice.setPstTax(pst);
        invoice.setTotalNetValue(total);
        invoice.setUserId(user);
        invoice.setInvoiceAlbumList(createInvoiceAlbumList());
        invoice.setInvoiceTrackList(createInvoiceTrackList());
        
        // Create invoice
        invoiceController.create(invoice);
        
        // TODO: clear shopping cart here
        
        // Redirect or forward to invoice summary page
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("invoice", invoice);
        return "invoicesummary";
    }
    
//    public double getFinalPrince(double listPrice, double salePrice)
//    {
//        return listPrice - salePrice;
//    }
    
    private List<InvoiceAlbum> createInvoiceAlbumList()
    {
        List<InvoiceAlbum> list = new ArrayList<>();
        List<Album> albums = cart.getAlbumsFromCart();
        
        for(int i = 0; i < albums.size(); i++)
        {
            double finalCost = albums.get(i).getListPrice() - albums.get(i).getSalePrice();
            list.add(new InvoiceAlbum());
            list.get(i).setAlbum(albums.get(i));
            list.get(i).setFinalPrice(finalCost);
        }
        
        return list;
    }
    
    private List<InvoiceTrack> createInvoiceTrackList()
    {
        List<InvoiceTrack> list = new ArrayList<>();
        List<Track> tracks = cart.getTracksFromCart();
        
        for(int i = 0; i < tracks.size(); i++)
        {
            double finalCost = tracks.get(i).getListPrice() - tracks.get(i).getSalePrice();
            list.add(new InvoiceTrack());
            list.get(i).setTrack(tracks.get(i));
            list.get(i).setFinalPrice(finalCost);
        }
        
        return list;
    }
}
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

    /**
     * This method calculates the taxes and total for the invoice after the cart
     * and user have been injected.
     *
     * @author Erika Bourque
     */
    @PostConstruct
    public void init() {
//        gst = cart.getSubTotal() * user.getProvinceId().getGstRate();
//        pst = cart.getSubTotal() * user.getProvinceId().getPstRate();
//        hst = cart.getSubTotal() * user.getProvinceId().getHstRate();
//        total = cart.getSubTotal() + gst + pst + hst;
    }

    /**
     * This method creates a list of months in the form of SelectItems for use
     * in the select one menu tag.
     *
     * @author Erika Bourque
     * @return The list of months
     */
    public List<SelectItem> getMonthSelector() {
        List<SelectItem> list = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            if (i < 10) // For display purposes, adds a 0 in front
            {
                list.add(new SelectItem(i, "0" + i));
            } else // Uses value as display by default
            {
                list.add(new SelectItem(i));
            }
        }

        return list;
    }

    /**
     * This method creates a list of years in the form of SelectItems for use in
     * the select one menu tag.
     *
     * @author Erika Bourque
     * @return The list of years
     */
    public List<SelectItem> getYearSelector() {
        List<SelectItem> list = new ArrayList<>();
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        // Default max value of exp years = 10
        int maxYears = 10;

        for (int i = 0; i < maxYears; i++) {
            list.add(new SelectItem(curYear + i));
        }

        return list;
    }

    /**
     * Getter for gst total of the purchase.
     *
     * @author Erika Bourque
     * @return the gst
     */
    public double getGst() {
        return gst;
    }

    /**
     * Getter for pst total of the purchase.
     *
     * @author Erika Bourque
     * @return the pst
     */
    public double getPst() {
        return pst;
    }

    /**
     * Getter for hst total of the purchase.
     *
     * @author Erika Bourque
     * @return the hst
     */
    public double getHst() {
        return hst;
    }

    /**
     * Getter for net total of the purchase.
     *
     * @author Erika Bourque
     * @return the net total
     */
    public double getTotal() {
        return total;
    }

    /**
     * This method finalizes the transaction and redirects the user to the
     * invoice summary page.
     *
     * @author Erika Bourque
     * @return  The name of the page to redirect to
     * @throws Exception    if the persist of the invoice fails
     */
    public String finalizePurchase() throws Exception {
        // TODO: verify that finalizePurchase actually works
        // Create the invoice
        Invoice invoice = buildInvoice();

        // Setting invoice purchases        
        invoice.setInvoiceAlbumList(buildInvoiceAlbumList());
        invoice.setInvoiceTrackList(buildInvoiceTrackList());

        // Persist invoice
        invoiceController.create(invoice);

        // TODO: clear shopping cart here
        // Redirect or forward to invoice summary page
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("invoice", invoice);
        return "invoicesummary";
    }

    /**
     * This method creates an invoice based on the purchase details 
     * and the cart.
     * 
     * @author Erika Bourque
     * @return The invoice
     */
    private Invoice buildInvoice()
    {
        Invoice invoice = new Invoice();
        
        // Setting the invoice details
        invoice.setSaleDate(Calendar.getInstance().getTime());
        invoice.setTotalGrossValue(cart.getSubTotal());
        invoice.setGstTax(gst);
        invoice.setHstTax(hst);
        invoice.setPstTax(pst);
        invoice.setTotalNetValue(total);
        invoice.setUserId(user);
        
        return invoice;
    }
    
    /**
     * This method creates a list of the InvoiceAlbums, based on the albums
     * in the cart.
     * 
     * @author Erika Bourque
     * @return  The list of InvoiceAlbums
     */
    private List<InvoiceAlbum> buildInvoiceAlbumList() {
        List<InvoiceAlbum> list = new ArrayList<>();
        List<Album> albums = cart.getAlbumsFromCart();

        for (int i = 0; i < albums.size(); i++) {
            double finalCost = albums.get(i).getListPrice() - albums.get(i).getSalePrice();
            list.add(new InvoiceAlbum());
            list.get(i).setAlbum(albums.get(i));
            list.get(i).setFinalPrice(finalCost);
        }

        return list;
    }

    /**
     * This method creates a list of the InvoiceTracks, based on the tracks
     * in the cart.
     * 
     * @author Erika Bourque
     * @return  The list of InvoiceTracks
     */
    private List<InvoiceTrack> buildInvoiceTrackList() {
        List<InvoiceTrack> list = new ArrayList<>();
        List<Track> tracks = cart.getTracksFromCart();

        for (int i = 0; i < tracks.size(); i++) {
            double finalCost = tracks.get(i).getListPrice() - tracks.get(i).getSalePrice();
            list.add(new InvoiceTrack());
            list.get(i).setTrack(tracks.get(i));
            list.get(i).setFinalPrice(finalCost);
        }

        return list;
    }
}

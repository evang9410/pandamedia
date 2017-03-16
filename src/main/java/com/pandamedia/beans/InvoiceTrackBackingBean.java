
package com.pandamedia.beans;

import java.io.Serializable;
import java.util.Calendar;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import persistence.controllers.InvoiceTrackJpaController;
import persistence.entities.InvoiceTrack;
import persistence.entities.InvoiceTrackPK;

/**
 * This class will be used as the invoice track backing bean. It can update,
 * delete and query invoice tracks.
 * @author  Naasir Jusab
 */
@Named("invoiceTrackBacking")
@SessionScoped
public class InvoiceTrackBackingBean implements Serializable{
    
    @Inject
    private InvoiceTrackJpaController invoiceTrackController;
    private InvoiceTrack invoiceTrack;
    @PersistenceContext
    private EntityManager em;
    
    /**
     * This method will return an invoice track if it exists already. Otherwise, 
     * it will return a new invoice track.
     * @return invoice object
     */
    public InvoiceTrack getInvoiceTrack(){
        
        if(invoiceTrack == null){
            invoiceTrack = new InvoiceTrack();
        }
        return invoiceTrack;
    }
    
    /**
     * This method will remove an invoice track by searching its embedded id
     * then changing its removal status to 1 and the removal date to the date
     * when the remove was clicked. Then, the controller edits these values
     * so they do not show on the data table.
     * @param invTrackPK InvoiceTrackPK object
     * @return null string that should refresh the page
     */
    public String removeInvoiceTrack(InvoiceTrackPK invTrackPK)
    {
        InvoiceTrack invTrack = invoiceTrackController.findInvoiceTrack(invTrackPK);
        
        if(invTrack.getRemovalStatus() != 1)
        {
            short i = 1;
            invTrack.setRemovalStatus(i);
            invTrack.setRemovalDate(Calendar.getInstance().getTime());

            try
            {
                invoiceTrackController.edit(invTrack);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
        return null; 
        
    }
    
}

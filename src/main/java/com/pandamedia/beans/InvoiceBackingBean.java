package com.pandamedia.beans;

import persistence.controllers.InvoiceJpaController;
import persistence.entities.Invoice;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;


/**
 *
 * @author Naasir
 */

@Named("invoiceBacking")
@SessionScoped
public class InvoiceBackingBean implements Serializable{
    @Inject
    private InvoiceJpaController invoiceController;
    private Invoice invoice;
    @PersistenceContext
    private EntityManager em;
    
    
    public Invoice getInvoice(){
        if(invoice == null){
            invoice = new Invoice();
        }
        return invoice;
    }
    
    
    
    public String removeItem(Integer id) throws Exception
    {
        
        invoice = invoiceController.findInvoice(id);
        
        invoiceController.destroy(invoice.getId());
        
        return null; 
    }
    
    public List<Invoice> getAll()
    {
        return invoiceController.findInvoiceEntities();
    }

    
    
}

package com.pandamedia.beans;

import persistence.controllers.InvoiceJpaController;
import persistence.entities.Invoice;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
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
    private List<Invoice> invoices;
    private List<Invoice> filteredInvoices;
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void init()
    {
        this.invoices = invoiceController.findInvoiceEntities();     
    }
    
    public List<Invoice> getInvoices()
    {
        return invoices;
    }
    
    public void setInvoices(List<Invoice> invoices)
    {
        this.invoices = invoices;
    }
    
    public void setFilteredInvoices(List<Invoice> filteredInvoices)
    {
        this.filteredInvoices = filteredInvoices;
    }
    
    public List<Invoice> getFilteredInvoices()
    {
        return filteredInvoices;
    }
    
    public Invoice getInvoice(){
        
        if(invoice == null){
            invoice = new Invoice();
        }
        return invoice;
    }
    
    public String addItem(Integer id) throws Exception
    {        
        invoice = invoiceController.findInvoice(id);
        if(invoice.getRemovalStatus() != 0)
        {
            short i = 0;
            invoice.setRemovalStatus(i);
            invoice.setRemovalDate(null);

            invoiceController.edit(invoice);     
        }
        
        return null; 
    }
    
    public String removeItem(Integer id) throws Exception
    {        
        invoice = invoiceController.findInvoice(id);
        if(invoice.getRemovalStatus() != 1)
        {
            short i = 1;
            invoice.setRemovalStatus(i);
            invoice.setRemovalDate(Calendar.getInstance().getTime());

            invoiceController.edit(invoice);     
        }
        
        return null; 
    }
    
    public List<Invoice> getAll()
    {
        return invoiceController.findInvoiceEntities();
    }
    
    public String loadEditForOrders(Integer id)
    {
        this.invoice = invoiceController.findInvoice(id);
        return "editOrders.xhtml";
    }

    public String edit() throws Exception
    {
        invoiceController.edit(invoice);
        return "welcome_orders";
    }
    
    
}

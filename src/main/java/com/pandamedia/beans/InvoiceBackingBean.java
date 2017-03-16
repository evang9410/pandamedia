package com.pandamedia.beans;

import persistence.controllers.InvoiceJpaController;
import persistence.entities.Invoice;
import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import persistence.entities.InvoiceTrack;
import persistence.entities.InvoiceTrackPK;
import persistence.entities.InvoiceTrack_;
import persistence.entities.Invoice_;
import persistence.entities.Track;
import persistence.entities.Track_;



/**
 * This class will be used as the invoice backing bean. It can create, update,
 * delete and query invoices.
 * @author  Naasir Jusab
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
    
    /**
     * This method will initialize a list of invoices that will be used by the 
     * data table. PostConstruct is used in methods that need to be executed after 
     * dependency injection is done to perform any initialization. In this case,
     * I need the list of invoices after invoiceController has been injected.
     */
    @PostConstruct
    public void init()
    {
        this.invoices = invoiceController.findInvoiceEntities();     
    }
    
    /**
     * This method will return all the invoices in a list so it can be displayed
     * on the data table.
     * @return all invoices in the database
     */
    public List<Invoice> getInvoices()
    {
        return invoices;
    }
    
    /**
     * This method will set a list of invoices to make changes to the current
     * list of all invoices.
     * @param invoices all invoices in the database
     */
    public void setInvoices(List<Invoice> invoices)
    {
        this.invoices = invoices;
    }
    
    /**
     * This method will set a list of filtered invoices to change the current
     * list of filtered invoices.
     * @param filteredInvoices list of filtered invoices
     */
    public void setFilteredInvoices(List<Invoice> filteredInvoices)
    {
        this.filteredInvoices = filteredInvoices;
    }
    
    /**
     * This method will return a list of filtered invoices so that the manager
     * can make searches on invoices.
     * @return list of filteredInvoices
     */
    public List<Invoice> getFilteredInvoices()
    {
        return filteredInvoices;
    }
    
    /**
     * This method will return an invoice if it exists already. Otherwise, it 
     * will return a new invoice.
     * @return invoice
     */
    public Invoice getInvoice(){
        
        if(invoice == null){
            invoice = new Invoice();
        }
        return invoice;
    }
    
    /**
     * This method will add an invoice that has been removed. It will change
     * the removal status to 0 which means that it will be added to reports.
     * 1 means that it will not be added to reports. It will set the removal 
     * date to null since it has not been removed. The return type null
     * should refresh the page.
     * @param id of the invoice that will be added
     * @return null refresh the page 
     */
    public String addItem(Integer id) 
    {        
        invoice = invoiceController.findInvoice(id);
        if(invoice.getRemovalStatus() != 0)
        {
            short i = 0;
            invoice.setRemovalStatus(i);
            invoice.setRemovalDate(null);

            try
            {
                invoiceController.edit(invoice);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
        this.invoice = null;
        this.filteredInvoices = invoiceController.findInvoiceEntities();
        return null; 
    }
    
    /**
     * This method will remove an invoice that has been added. It will change
     * the removal status to 1 which means that it will not be added to reports.
     * 0 means that it will be added to reports. It will set the removal 
     * date to the date when you clicked on the remove, meaning today's date. 
     * The return type null should refresh the page.
     * @param id of the invoice that will be removed
     * @return null refresh the page
     */
    public String removeItem(Integer id) 
    {        
        invoice = invoiceController.findInvoice(id);
        if(invoice.getRemovalStatus() != 1)
        {
            short i = 1;
            invoice.setRemovalStatus(i);
            invoice.setRemovalDate(Calendar.getInstance().getTime());

            try
            {
                invoiceController.edit(invoice);  
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
        this.invoice = null;
        this.filteredInvoices = invoiceController.findInvoiceEntities();
        return null; 
    }
    
    /**
     * This method will return all the invoices in the database so it can be 
     * displayed on the data table.
     * @return list of invoices
     */
    public List<Invoice> getAll()
    {
        return invoiceController.findInvoiceEntities();
    }
    
    /**
     * This method will set the invoice so that when the editOrders.xhtml loads.
     * The fields of the page will have values already. All the manager has to do 
     * is change the values. The id will make sure that the right invoice is 
     * being edited and the return type will display the edit page for the invoice.
     * @param id of an invoice that will be edited
     * @return string that is the edit page for an invoice
     */
    public String loadEditForOrders(Integer id)
    {
        this.invoice = invoiceController.findInvoice(id);
        return "editOrders.xhtml";
    }
    
    public String loadIndivTracks(Integer id)
    {
        this.invoice = invoiceController.findInvoice(id);
        return "removeIndivTracks.xhtml";
    }
    
    public List<Track> loadTable()
    {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Track> query = cb.createQuery(Track.class);
        Root<Invoice> invoiceRoot = query.from(Invoice.class);
        Join invoiceTrackJoin = invoiceRoot.join(Invoice_.invoiceTrackList);
        Join  invoiceJoin = invoiceTrackJoin.join(InvoiceTrack_.invoice);
        
        query.select(invoiceTrackJoin);
        // Where clause
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(invoiceTrackJoin.get(Invoice_.removalStatus), 0));
        predicates.add(cb.equal(invoiceJoin.get(InvoiceTrack_.removalStatus), 0));
        predicates.add(cb.equal(invoiceRoot.get(Invoice_.id), invoice.getId()));
        
        query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        
        TypedQuery<Track> typedQuery = em.createQuery(query);
        
        return typedQuery.getResultList();
        
    }

    /**
     * This method will be called to edit an invoice.  
     * @return string that is the main page for invoices
     */
    public String edit() 
    {
        try
        {
            invoiceController.edit(invoice);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        this.invoice = null;
        this.filteredInvoices = invoiceController.findInvoiceEntities();
        return "welcome_orders";
    }
    
    
    public String back()
    {
        this.invoice = null;
        this.filteredInvoices = invoiceController.findInvoiceEntities();
        return "welcome_orders";
    }
    
    
    
}

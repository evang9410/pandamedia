package com.pandamedia.beans;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import persistence.entities.Invoice;

/**
 *
 * @author Erika Bourque
 */
@Named("invoiceSum")
@RequestScoped
public class InvoiceSummaryBackingBean {
    @Inject
    Invoice invoice;
    
    @PostConstruct
    public void init() {
        invoice = (Invoice) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("invoice");
    }
    
    public Invoice getInvoice()
    {
        return invoice;
    }
}

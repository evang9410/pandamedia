/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Panda
 */
@Entity
@Table(name = "invoice_track")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InvoiceTrack.findAll", query = "SELECT i FROM InvoiceTrack i")
    , @NamedQuery(name = "InvoiceTrack.findByInvoiceId", query = "SELECT i FROM InvoiceTrack i WHERE i.invoiceTrackPK.invoiceId = :invoiceId")
    , @NamedQuery(name = "InvoiceTrack.findByTrackId", query = "SELECT i FROM InvoiceTrack i WHERE i.invoiceTrackPK.trackId = :trackId")
    , @NamedQuery(name = "InvoiceTrack.findByFinalPrice", query = "SELECT i FROM InvoiceTrack i WHERE i.finalPrice = :finalPrice")})
public class InvoiceTrack implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected InvoiceTrackPK invoiceTrackPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "final_price")
    private double finalPrice;
    @JoinColumn(name = "invoice_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Invoice invoice;
    @JoinColumn(name = "track_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Track track;

    public InvoiceTrack() {
    }

    public InvoiceTrack(InvoiceTrackPK invoiceTrackPK) {
        this.invoiceTrackPK = invoiceTrackPK;
    }

    public InvoiceTrack(InvoiceTrackPK invoiceTrackPK, double finalPrice) {
        this.invoiceTrackPK = invoiceTrackPK;
        this.finalPrice = finalPrice;
    }

    public InvoiceTrack(int invoiceId, int trackId) {
        this.invoiceTrackPK = new InvoiceTrackPK(invoiceId, trackId);
    }

    public InvoiceTrackPK getInvoiceTrackPK() {
        return invoiceTrackPK;
    }

    public void setInvoiceTrackPK(InvoiceTrackPK invoiceTrackPK) {
        this.invoiceTrackPK = invoiceTrackPK;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (invoiceTrackPK != null ? invoiceTrackPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvoiceTrack)) {
            return false;
        }
        InvoiceTrack other = (InvoiceTrack) object;
        if ((this.invoiceTrackPK == null && other.invoiceTrackPK != null) || (this.invoiceTrackPK != null && !this.invoiceTrackPK.equals(other.invoiceTrackPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.entities.InvoiceTrack[ invoiceTrackPK=" + invoiceTrackPK + " ]";
    }
    
}

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
@Table(name = "invoice_album")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InvoiceAlbum.findAll", query = "SELECT i FROM InvoiceAlbum i")
    , @NamedQuery(name = "InvoiceAlbum.findByInvoiceId", query = "SELECT i FROM InvoiceAlbum i WHERE i.invoiceAlbumPK.invoiceId = :invoiceId")
    , @NamedQuery(name = "InvoiceAlbum.findByAlbumId", query = "SELECT i FROM InvoiceAlbum i WHERE i.invoiceAlbumPK.albumId = :albumId")
    , @NamedQuery(name = "InvoiceAlbum.findByFinalPrice", query = "SELECT i FROM InvoiceAlbum i WHERE i.finalPrice = :finalPrice")})
public class InvoiceAlbum implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected InvoiceAlbumPK invoiceAlbumPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "final_price")
    private double finalPrice;
    @JoinColumn(name = "invoice_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Invoice invoice;
    @JoinColumn(name = "album_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Album album;

    public InvoiceAlbum() {
    }

    public InvoiceAlbum(InvoiceAlbumPK invoiceAlbumPK) {
        this.invoiceAlbumPK = invoiceAlbumPK;
    }

    public InvoiceAlbum(InvoiceAlbumPK invoiceAlbumPK, double finalPrice) {
        this.invoiceAlbumPK = invoiceAlbumPK;
        this.finalPrice = finalPrice;
    }

    public InvoiceAlbum(int invoiceId, int albumId) {
        this.invoiceAlbumPK = new InvoiceAlbumPK(invoiceId, albumId);
    }

    public InvoiceAlbumPK getInvoiceAlbumPK() {
        return invoiceAlbumPK;
    }

    public void setInvoiceAlbumPK(InvoiceAlbumPK invoiceAlbumPK) {
        this.invoiceAlbumPK = invoiceAlbumPK;
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

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (invoiceAlbumPK != null ? invoiceAlbumPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvoiceAlbum)) {
            return false;
        }
        InvoiceAlbum other = (InvoiceAlbum) object;
        if ((this.invoiceAlbumPK == null && other.invoiceAlbumPK != null) || (this.invoiceAlbumPK != null && !this.invoiceAlbumPK.equals(other.invoiceAlbumPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.entities.InvoiceAlbum[ invoiceAlbumPK=" + invoiceAlbumPK + " ]";
    }
    
}

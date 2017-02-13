/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author 1432581
 */
@Entity
@Table(name = "invoice")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Invoice.findAll", query = "SELECT i FROM Invoice i")
    , @NamedQuery(name = "Invoice.findById", query = "SELECT i FROM Invoice i WHERE i.id = :id")
    , @NamedQuery(name = "Invoice.findByUserId", query = "SELECT i FROM Invoice i WHERE i.userId = :userId")
    , @NamedQuery(name = "Invoice.findBySaleDate", query = "SELECT i FROM Invoice i WHERE i.saleDate = :saleDate")
    , @NamedQuery(name = "Invoice.findByTotalNetValue", query = "SELECT i FROM Invoice i WHERE i.totalNetValue = :totalNetValue")
    , @NamedQuery(name = "Invoice.findByPstTax", query = "SELECT i FROM Invoice i WHERE i.pstTax = :pstTax")
    , @NamedQuery(name = "Invoice.findByGstTax", query = "SELECT i FROM Invoice i WHERE i.gstTax = :gstTax")
    , @NamedQuery(name = "Invoice.findByHstTax", query = "SELECT i FROM Invoice i WHERE i.hstTax = :hstTax")
    , @NamedQuery(name = "Invoice.findByTotalGrossValue", query = "SELECT i FROM Invoice i WHERE i.totalGrossValue = :totalGrossValue")})
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private int userId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sale_date")
    @Temporal(TemporalType.DATE)
    private Date saleDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_net_value")
    private double totalNetValue;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pst_tax")
    private double pstTax;
    @Basic(optional = false)
    @NotNull
    @Column(name = "gst_tax")
    private double gstTax;
    @Basic(optional = false)
    @NotNull
    @Column(name = "hst_tax")
    private double hstTax;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_gross_value")
    private double totalGrossValue;

    public Invoice() {
    }

    public Invoice(Integer id) {
        this.id = id;
    }

    public Invoice(Integer id, int userId, Date saleDate, double totalNetValue, double pstTax, double gstTax, double hstTax, double totalGrossValue) {
        this.id = id;
        this.userId = userId;
        this.saleDate = saleDate;
        this.totalNetValue = totalNetValue;
        this.pstTax = pstTax;
        this.gstTax = gstTax;
        this.hstTax = hstTax;
        this.totalGrossValue = totalGrossValue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public double getTotalNetValue() {
        return totalNetValue;
    }

    public void setTotalNetValue(double totalNetValue) {
        this.totalNetValue = totalNetValue;
    }

    public double getPstTax() {
        return pstTax;
    }

    public void setPstTax(double pstTax) {
        this.pstTax = pstTax;
    }

    public double getGstTax() {
        return gstTax;
    }

    public void setGstTax(double gstTax) {
        this.gstTax = gstTax;
    }

    public double getHstTax() {
        return hstTax;
    }

    public void setHstTax(double hstTax) {
        this.hstTax = hstTax;
    }

    public double getTotalGrossValue() {
        return totalGrossValue;
    }

    public void setTotalGrossValue(double totalGrossValue) {
        this.totalGrossValue = totalGrossValue;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Invoice)) {
            return false;
        }
        Invoice other = (Invoice) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistance.beans.Invoice[ id=" + id + " ]";
    }
    
}

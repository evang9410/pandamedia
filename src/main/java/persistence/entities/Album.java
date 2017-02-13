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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author 1432581
 */
@Entity
@Table(name = "album")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Album.findAll", query = "SELECT a FROM Album a")
    , @NamedQuery(name = "Album.findById", query = "SELECT a FROM Album a WHERE a.id = :id")
    , @NamedQuery(name = "Album.findByTitle", query = "SELECT a FROM Album a WHERE a.title = :title")
    , @NamedQuery(name = "Album.findByReleaseDate", query = "SELECT a FROM Album a WHERE a.releaseDate = :releaseDate")
    , @NamedQuery(name = "Album.findByArtistId", query = "SELECT a FROM Album a WHERE a.artistId = :artistId")
    , @NamedQuery(name = "Album.findByGenreId", query = "SELECT a FROM Album a WHERE a.genreId = :genreId")
    , @NamedQuery(name = "Album.findByRecordingLabelId", query = "SELECT a FROM Album a WHERE a.recordingLabelId = :recordingLabelId")
    , @NamedQuery(name = "Album.findByNumTracks", query = "SELECT a FROM Album a WHERE a.numTracks = :numTracks")
    , @NamedQuery(name = "Album.findByDateEntered", query = "SELECT a FROM Album a WHERE a.dateEntered = :dateEntered")
    , @NamedQuery(name = "Album.findByCostPrice", query = "SELECT a FROM Album a WHERE a.costPrice = :costPrice")
    , @NamedQuery(name = "Album.findByListPrice", query = "SELECT a FROM Album a WHERE a.listPrice = :listPrice")
    , @NamedQuery(name = "Album.findBySalePrice", query = "SELECT a FROM Album a WHERE a.salePrice = :salePrice")
    , @NamedQuery(name = "Album.findByRemovalStatus", query = "SELECT a FROM Album a WHERE a.removalStatus = :removalStatus")
    , @NamedQuery(name = "Album.findByRemovalDate", query = "SELECT a FROM Album a WHERE a.removalDate = :removalDate")
    , @NamedQuery(name = "Album.findTracks", query = "SELECT t FROM Track t WHERE t.albumId = :id")})
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "title")
    private String title;
    @Basic(optional = false)
    @NotNull
    @Column(name = "release_date")
    @Temporal(TemporalType.DATE)
    private Date releaseDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "artist_id")
    private int artistId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "genre_id")
    private int genreId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "recording_label_id")
    private int recordingLabelId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "num_tracks")
    private int numTracks;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date_entered")
    @Temporal(TemporalType.DATE)
    private Date dateEntered;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cost_price")
    private double costPrice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "list_price")
    private double listPrice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sale_price")
    private double salePrice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "removal_status")
    private short removalStatus;
    @Column(name = "removal_date")
    @Temporal(TemporalType.DATE)
    private Date removalDate;

    public Album() {
    }

    public Album(Integer id) {
        this.id = id;
    }

    public Album(Integer id, String title, Date releaseDate, int artistId, int genreId, int recordingLabelId, int numTracks, Date dateEntered, double costPrice, double listPrice, double salePrice, short removalStatus) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.artistId = artistId;
        this.genreId = genreId;
        this.recordingLabelId = recordingLabelId;
        this.numTracks = numTracks;
        this.dateEntered = dateEntered;
        this.costPrice = costPrice;
        this.listPrice = listPrice;
        this.salePrice = salePrice;
        this.removalStatus = removalStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public int getRecordingLabelId() {
        return recordingLabelId;
    }

    public void setRecordingLabelId(int recordingLabelId) {
        this.recordingLabelId = recordingLabelId;
    }

    public int getNumTracks() {
        return numTracks;
    }

    public void setNumTracks(int numTracks) {
        this.numTracks = numTracks;
    }

    public Date getDateEntered() {
        return dateEntered;
    }

    public void setDateEntered(Date dateEntered) {
        this.dateEntered = dateEntered;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getListPrice() {
        return listPrice;
    }

    public void setListPrice(double listPrice) {
        this.listPrice = listPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public short getRemovalStatus() {
        return removalStatus;
    }

    public void setRemovalStatus(short removalStatus) {
        this.removalStatus = removalStatus;
    }

    public Date getRemovalDate() {
        return removalDate;
    }

    public void setRemovalDate(Date removalDate) {
        this.removalDate = removalDate;
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
        if (!(object instanceof Album)) {
            return false;
        }
        Album other = (Album) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistance.beans.Album[ id=" + id + " ]";
    }
    
}

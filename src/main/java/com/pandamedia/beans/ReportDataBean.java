package com.pandamedia.beans;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import persistence.entities.Album;
import persistence.entities.Artist;
import persistence.entities.Track;

/**
 *
 * @author Erika Bourque
 */
@Named("reportData")
@RequestScoped
public class ReportDataBean {
    private static final Logger LOG = Logger.getLogger("ReportDataBean.class");
    
    private Date startDate;
    private Date endDate;
    private Artist artist;
    private Album album;
    private Track track;

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }
    
    public Date getDefaultEndDate() {
        return Calendar.getInstance().getTime();
    }

    public Date getDefaultStartDate() {
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_YEAR, -30);

        return start.getTime();
    }

    public Date getStartDate() {
        if (startDate == null) {
            startDate = getDefaultStartDate();
        }
        return startDate;
    }

    public Date getEndDate() {
        if (endDate == null) {
            endDate = getDefaultEndDate();
        }
        return endDate;
    }

    public void setStartDate(Date date) {
        LOG.log(Level.INFO, "--- New start date: {0}", date);
        LOG.log(Level.INFO, "--- Current end date: {0}", endDate);
        startDate = date;
    }

    public void setEndDate(Date date) {
        LOG.log(Level.INFO, "--- New end date: {0}", date);
        LOG.log(Level.INFO, "--- Current start date: {0}", startDate);
        endDate = date;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandamedia.beans;

import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import persistence.entities.Artist;

/**
 *
 * @author Erika Bourque
 */
@Named
@RequestScoped
public class ArtistBackingBean {
    private static final Logger LOG = Logger.getLogger("ArtistBackingBean.class");
    private Artist artist;
    
    public Artist getArtist()
    {
        return artist;
    }
    
    public void setArtist(Artist artist)
    {
        LOG.info("New artist id: " + artist.getId());
        this.artist = artist;
    }
}

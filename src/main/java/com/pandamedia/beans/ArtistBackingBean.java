/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandamedia.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import persistence.controllers.ArtistJpaController;
import persistence.entities.Artist;


/**
 *
 * @author Pierre Azelart
 */
@Named("artistBacking")
@SessionScoped
public class ArtistBackingBean implements Serializable{
    private static final Logger LOG = Logger.getLogger("ArtistBackingBean.class");
    @Inject
    private ArtistJpaController artistController;
    private Artist artist;
    private List artists;
    @PersistenceContext
    private EntityManager em;
    
    public String artistPage(Artist a){
        this.artist = a;
        LOG.info("" + a.getId() +"\n" + a.getName() +"\n");
        return "artist";
    }
    
    public Artist getArtist()
    {
        return artist;
    }
    
    public void setArtist(Artist artist)
    {
        LOG.info("New artist id: " + artist.getId());
        this.artist = artist;
    }
    
    public List<Artist> getArtistsList(){
        String q = "SELECT a FROM Artist a";
        TypedQuery<Artist> query = em.createQuery(q, Artist.class);
        this.artists = query.getResultList();
        return this.artists;
    }
    
    public void setArtistList(ArrayList<Artist> a){
        this.artists = a;
    }
}

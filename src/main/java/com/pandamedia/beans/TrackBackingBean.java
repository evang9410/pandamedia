/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandamedia.beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import persistence.controllers.TrackJpaController;
import persistence.entities.Track;

/**
 *
 * @author Evang
 */
@Named("track")
@SessionScoped
public class TrackBackingBean implements Serializable{
    @Inject
    private TrackJpaController trackController;
    @PersistenceContext
    private EntityManager em;
    private Track track;
    private String genre_string;
    
    public Track getTrack(){
        if(track == null){
            track = new Track();
        }
        return track;
    }
    
    
}

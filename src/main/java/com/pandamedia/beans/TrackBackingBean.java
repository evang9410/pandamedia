package com.pandamedia.beans;

import java.io.Serializable;
import persistence.controllers.TrackJpaController;
import persistence.entities.Track;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import persistence.controllers.TrackJpaController;
import persistence.entities.Album;
import persistence.entities.Track;

/**
 *
 * @author Naasir, Evan
 */
@Named("trackBacking")
@SessionScoped
public class TrackBackingBean implements Serializable{
    @Inject
    private TrackJpaController trackController;
    private Track track;
    private List<Track> tracks;
    private List<Track> filteredTracks;
    @PersistenceContext
    private EntityManager em;
    private String genre_string;
    
    @PostConstruct
    public void init()
    {
        this.tracks = trackController.findTrackEntities();     
    }
    
    public Track getTrack(){
        if(track == null){
            track = new Track();
        }
        return track;
    }
    
    public void setFilteredTracks(List<Track> filteredTracks)
    {
        this.filteredTracks = filteredTracks;
    }
    
    public List<Track> getFilteredTracks()
    {
        return this.filteredTracks;
    }
    
    public List<Track> getTracks()
    {
        return tracks;
    }
    
    public void setTracks(List<Track> tracks)
    {
        this.tracks = tracks;
    }
    
    public void setTrack(Track track){
        this.track = track;
    }

    public String trackPage(Track t){
        track = t;
        return "track";
    }

    /**
     * Finds the Track from its id.
     * @param id
     * @return 
     */
    public Track findTrackById(int id){
        track = trackController.findTrack(id); 
        return track;
    }
    
    public String addItem(Integer id) throws Exception
    {
        track = trackController.findTrack(id);
        if(track.getRemovalStatus() != 0)
        {
            short i = 0;
            track.setRemovalStatus(i);
            track.setRemovalDate(null);

            trackController.edit(track);

            
        }
        
        return null; 
    }
    
    public String removeItem(Integer id) throws Exception
    {
        track = trackController.findTrack(id);
        if(track.getRemovalStatus() != 1)
        {
            short i = 1;
            track.setRemovalStatus(i);
            track.setRemovalDate(Calendar.getInstance().getTime());

            trackController.edit(track);
        }
        
        return null; 
    }
    
    public String loadEditForIndex(Integer id)
    {
        this.track = trackController.findTrack(id);
        return "TrackFunctionality/editTrack.xhtml";
    }
    
    public String edit() throws Exception
    {
        trackController.edit(track);
        
        return "welcome_manager";
    }
    
    public String create() throws Exception
    {
        trackController.create(track);
        return "welcome_manager";
    }

    
    
}


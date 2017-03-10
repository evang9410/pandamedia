
package com.pandamedia.beans;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import persistence.controllers.SongwriterJpaController;
import persistence.entities.Songwriter;


/**
 *
 * @author Naasir
 */
@Named("songWriterBacking")
@SessionScoped
public class SongWriterBackingBean implements Serializable{
    @Inject
    private SongwriterJpaController songWriterController;
    private Songwriter songWriter;
    @PersistenceContext
    private EntityManager em;
    
    
    public Songwriter getSongwriter(){
        if(songWriter == null){
            songWriter = new Songwriter();
        }
        return songWriter;
    }
    
    
    public List<Songwriter> getAll()
    {
        return songWriterController.findSongwriterEntities();
    }
    
    

}

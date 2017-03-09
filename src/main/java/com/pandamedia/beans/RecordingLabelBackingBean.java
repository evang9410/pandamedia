
package com.pandamedia.beans;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import persistence.controllers.RecordingLabelJpaController;
import persistence.entities.RecordingLabel;


/**
 *
 * @author Naasir 
 */
@Named("recordingLabelBacking")
@SessionScoped
public class RecordingLabelBackingBean implements Serializable {
    @Inject
    private RecordingLabelJpaController recordingLabelController;
    private RecordingLabel recordingLabel;
    @PersistenceContext
    private EntityManager em;
    
    
    public RecordingLabel getRecordingLabel(){
        if(recordingLabel == null){
            recordingLabel = new RecordingLabel();
        }
        return recordingLabel;
    }
    
    /**
     * Finds the RecordingLabel from its id.
     * @param id
     * @return 
     */
    public RecordingLabel findRecordingLabelById(int id){
        recordingLabel = recordingLabelController.findRecordingLabel(id); 
        return recordingLabel;
    }
    
    public List<RecordingLabel> getAll()
    {
        return recordingLabelController.findRecordingLabelEntities();
    }
    
}

package com.pandamedia.beans;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * This class provides common methods for the report pages.
 * 
 * @author Erika Bourque
 */
@Named
@RequestScoped
public class reportBackingBean implements Serializable{
    
    public String getCurrentDate()
    {
        return null;
    }
}

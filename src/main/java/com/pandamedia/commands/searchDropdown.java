/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandamedia.commands;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author Pierre Azelart
 */
@Named("searchDropdown")
@RequestScoped
public class searchDropdown implements Serializable{
    
    private FacesContext context;
    private ResourceBundle msgs;
    private String toDisplay = "Type";
    
    private String type;

    @PostConstruct
    public void init(){
        
        try{
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundle msgs = ResourceBundle.getBundle("bundles.messages", context.getViewRoot().getLocale());
            String toDisplay = msgs.getString("tracks");
            
            //Default search type
            type = "tracks";
        }
        catch(java.lang.ClassCastException ex){
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        
        //Update the string diplayed to match selected language
        setToDisplay(type);
    }

    public String getToDisplay() {
        return toDisplay;
    }

    public void setToDisplay(String type) {
        context = FacesContext.getCurrentInstance();
        msgs = ResourceBundle.getBundle("bundles.messages", context.getViewRoot().getLocale());
        this.toDisplay = msgs.getString(type);
    }
}

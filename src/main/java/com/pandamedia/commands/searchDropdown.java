/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandamedia.commands;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author Pierre Azelart
 */
@Named("searchDropdown")
@SessionScoped
public class searchDropdown implements Serializable{
    
    FacesContext context = FacesContext.getCurrentInstance();
    ResourceBundle msgs = ResourceBundle.getBundle("bundles.messages", context.getViewRoot().getLocale());
    String toDisplay = msgs.getString("tracks");
    
    private String type;

    @PostConstruct
    public void init(){
        //Default search type
        type = "tracks";
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

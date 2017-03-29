/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandamedia.commands;

import java.io.Serializable;
import java.util.Locale;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author Pierre Azelart
 */
@Named("ChangeLanguage")
@SessionScoped
public class ChangeLanguage implements Serializable {
    
    private Locale localValue;
    
    public String frenchAction() {
        FacesContext context = FacesContext.getCurrentInstance();
        localValue = Locale.CANADA_FRENCH;
        context.getViewRoot().setLocale(localValue);
        return null;
    }
    public String englishAction() {
        FacesContext context = FacesContext.getCurrentInstance();
        localValue = Locale.CANADA;
        context.getViewRoot().setLocale(localValue);
        return null;
    }
    
    public Locale getLocale()
    {
        return localValue;
    }
    
    
}

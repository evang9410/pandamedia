
package com.pandamedia.converters;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;
import persistence.controllers.CoverArtJpaController;
import persistence.entities.CoverArt;

/**
 *
 * @author Naasir
 */
@RequestScoped
@Named("coverArtConverter")
public class CoverArtConverter  implements Converter {
    
    @Inject
    private CoverArtJpaController service;
 
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            try {   
                return service.findCoverArt(Integer.parseInt(value));
               
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
            }
        }
        else {
            return null;
        }
    }
 
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((CoverArt) object).getId());
        }
        else {
            return null;
        }
    }   
}         

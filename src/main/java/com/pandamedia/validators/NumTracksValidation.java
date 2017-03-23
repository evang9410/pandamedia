
package com.pandamedia.validators;

import java.util.Calendar;
import java.util.Date;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

/**
 *
 * @author Naasir
 */
@Named
@RequestScoped
public class NumTracksValidation implements Validator {
    
     @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) {
        if (value == null) 
           throw new ValidatorException(new FacesMessage("Number Tracks cannot be null"));         
        
        int numTracks = (int) value;
        
        if(numTracks <= 0)
            throw new ValidatorException(new FacesMessage("Cannot be less or equal to 0"));
               
    }
    
}

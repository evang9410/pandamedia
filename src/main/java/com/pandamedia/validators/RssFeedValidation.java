
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
public class RssFeedValidation implements Validator {
    
    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) {
        if (value == null) 
           throw new ValidatorException(new FacesMessage("Url cannot be null"));         
        
        String rssFeed = (String) value;
        
        if(!rssFeed.startsWith("https://") && !rssFeed.startsWith("http://"))
            throw new ValidatorException(new FacesMessage("RSS Feed is invalid"));
        
        if(!rssFeed.endsWith(".xml"))
            throw new ValidatorException(new FacesMessage("RSS Feed is invalid"));
               
    }
    
}

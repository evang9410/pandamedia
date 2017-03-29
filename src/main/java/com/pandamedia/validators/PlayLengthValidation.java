
package com.pandamedia.validators;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import org.primefaces.component.outputlabel.OutputLabel;

/**
 *
 * @author Naasir 
 */
@Named
@RequestScoped
public class PlayLengthValidation implements Validator {
    
    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) {
        if (value == null) 
           throw new ValidatorException(new FacesMessage("Play Length cannot be null"));         
        
        String playLength = (String) value;
        int colon = playLength.indexOf(":");
        if(colon == -1)
            throw new ValidatorException(new FacesMessage("Invalid play length"));
        
        String firstPart = playLength.substring(0,colon);
        String secondPart = playLength.substring(colon+1);
        
        try
        {
            Integer.parseInt(firstPart);
            Integer.parseInt(secondPart);
        }
        catch(Exception e)
        {
            throw new ValidatorException(new FacesMessage("Invalid play length"));
        }
    }
    
}

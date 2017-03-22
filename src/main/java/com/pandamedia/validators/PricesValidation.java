
package com.pandamedia.validators;

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
public class PricesValidation implements Validator{
    
    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) {
        if (value == null) 
           throw new ValidatorException(new FacesMessage("Prices cannot be null")); 

        Double price = (Double) value;
        
        if(price < 0 )
            throw new ValidatorException(new FacesMessage("Cannot be negative"));

               
    }
    
}


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
import org.primefaces.component.inputtext.InputText;

/**
 *
 * @author Naasir
 */
@Named
@RequestScoped
public class RemovalDateValidation implements Validator{
    
    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) {
        
        InputText removalField = (InputText)component.findComponent("removalStatus");
        short removalStatus = (short)removalField.getValue();
        if(removalStatus == 1)
        {
            if (value == null) 
               throw new ValidatorException(new FacesMessage("Date cannot be null"));   

            Date date = (Date)value;
            Date todaysDate =(Date) Calendar.getInstance().getTime();
            if(date.after(todaysDate))
                throw new ValidatorException(new FacesMessage("Date cannot be in the future"));
        }
        
        else
        {
            if(value != null)
                throw new ValidatorException(new FacesMessage("Date has to be null"));   
        }
               
    }
    
}

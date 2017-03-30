
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
import org.primefaces.component.outputlabel.OutputLabel;

/**
 *
 * @author Naasir
 */
@Named
@RequestScoped
public class DateValidation implements Validator {
    
    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) {
        if (value == null) 
           throw new ValidatorException(new FacesMessage("Date cannot be null"));         
        
        Date date = (Date)value;
        Date todaysDate =(Date) Calendar.getInstance().getTime();
        if(date.after(todaysDate))
            throw new ValidatorException(new FacesMessage("Date cannot be in the future"));
               
    }
    
}

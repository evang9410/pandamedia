
package com.pandamedia.validators;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.outputlabel.OutputLabel;



/**
 *
 * @author Naasir
 */
@Named
@RequestScoped
public class SalePriceValidation implements Validator {
    
    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) {
        if (value == null) 
           throw new ValidatorException(new FacesMessage("Sale price cannot be null"));         
        
        Double salePrice = (Double) value;
        if(salePrice < 0)
            throw new ValidatorException(new FacesMessage("Sale price cannot be negative"));
        
        String id = component.getId();
        Double listPrice;
        if(id.equals("salePrice"))
        {
            OutputLabel listField = (OutputLabel) component.findComponent("listPrice");
        
            listPrice = (Double)listField.getValue();    
        }
        
        else
        {
            InputText listField = (InputText) component.findComponent("listPrice");
            
            listPrice = (Double)listField.getValue();
                           
        }
        
        if(salePrice >= listPrice)
            throw new ValidatorException(new FacesMessage("Sale price cannot be greater or equal to list price"));
        
    }
    
    
}

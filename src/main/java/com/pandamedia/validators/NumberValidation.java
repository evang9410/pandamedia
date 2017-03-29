
package com.pandamedia.validators;


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
public class NumberValidation implements Validator{
    
    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) {
        if (value == null) 
           throw new ValidatorException(new FacesMessage("Album Track Number cannot be null")); 
        
        InputText partOfAlbumText = (InputText)component.findComponent("partOfAlbum");
        
        short partOfAlbumValue = (short)partOfAlbumText.getValue();
        
        int albumTrackNumber = (int) value;
        if(partOfAlbumValue == 1)
        {
            if(albumTrackNumber <= 0 )
                throw new ValidatorException(new FacesMessage("Cannot be less or equal to 0"));
        }
        
        else
        {
            if(albumTrackNumber != 0)
                throw new ValidatorException(new FacesMessage("Cannot have track # if it is not part of an album"));
        }

               
    }
    
}

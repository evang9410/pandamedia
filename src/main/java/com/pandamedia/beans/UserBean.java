package com.pandamedia.beans;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import persistence.entities.ShopUser;
import jodd.mail.*;

/**
 *
 * @author Hau Gilles Che
 */
@Named
@SessionScoped
public class UserBean implements Serializable {
    @Inject
    private ShopUser shopUser;
    private String confirmPasswd;

    public ShopUser getShopUser() {
        return shopUser;
    }

    public void setShopUser(ShopUser shopUser) {
        this.shopUser = shopUser;
    }

    public String getConfirmPasswd() {
        return confirmPasswd;
    }

    public void setConfirmPasswd(String confirmPasswd) {
        this.confirmPasswd = confirmPasswd;
    }
    
    public void validatePassword(FacesContext fc, UIComponent c, Object value){
        if(!shopUser.getPassword().equals((String)value))
            throw new ValidatorException(new FacesMessage(
                    "Passwords do not match"));
    }
    
    /**
     * Validates the email address to make sure it is properly formatted
     * @param fc
     * @param c
     * @param value 
     */
    public void validateEmail(FacesContext fc, UIComponent c, Object value){
        EmailAddress email=new EmailAddress((String)value);
        
        if(!email.isValid())
            throw new ValidatorException(new FacesMessage(
            "Email must be formatted properly i.e: renuchan@renuchan.ren"));
    }
}

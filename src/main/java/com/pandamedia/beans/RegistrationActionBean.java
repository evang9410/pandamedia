package com.pandamedia.beans;

import com.pandamedia.utilities.PasswordHelper;
import java.io.Serializable;
import static java.lang.Math.random;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import jodd.mail.EmailAddress;
import persistence.controllers.ProvinceJpaController;
import persistence.controllers.ShopUserJpaController;
import persistence.entities.Province;
import persistence.entities.ShopUser;

/**
 * This bean provides basic logic for login and registration.
 *
 * @author Hau Gilles Che
 * @version 1.0.01
 */
@Named
@SessionScoped
public class RegistrationActionBean implements Serializable {

    @Inject
    private ShopUserJpaController userController;

    @Inject
    private UserBean userBean;

    @Inject
    private ProvinceJpaController provinceController;

    // Needed variables.
    private ShopUser loggedUser;
    private List<Province> provinces;
    private Province province;

    @PostConstruct
    public void init() {
        provinces = provinceController.findProvinceEntities();
    }
    
    /**
     * Responsible for creating a new user
     * @param province Id of the province chosen by the user.
     */
    public void register(String province) {
        ShopUser user = userBean.getShopUser();
        setFields(user);
        int provinceId = Integer.parseInt(province);
        Province prov = provinceController.findProvince(provinceId);
        user.setProvinceId(prov);
        try {
            userController.create(user);
            loggedUser = user;
            Logger.getLogger(RegistrationActionBean.class.getName()).log(
                    Level.SEVERE, null, "User created");
        } catch (Exception ex) {
            FacesMessage msg = com.pandamedia.utilities.Messages.getMessage(
                    "bundles.messages", "duplicateEmail", null);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(
                    "registrationForm:emailInput", msg);
        }
    }

    public ShopUser getLoggedUser() {
        return loggedUser;
    }

    public List<Province> getProvinces() {
        return provinces;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }
    
    /**
     * Sets the remaining fields that cannot be directly set by the user 
     * through the form.
     * @param user 
     */
    private void setFields(ShopUser user) {
        //instantiating the class responsible for password security
        PasswordHelper pwdHelper = new PasswordHelper();
        String salt = pwdHelper.getSalt();

        byte[] hashedPwd = pwdHelper.hash("FuckPriver", salt);

        user.setSalt(salt);
        user.setHashedPw(hashedPwd);

        user.setPostalCode(userBean.getPostalCode().toString());
    }

    /**
     * Validates the email address to make sure it is properly formatted
     *
     * @param fc
     * @param c
     * @param value
     */
    public void validateEmail(FacesContext fc, UIComponent c, Object value) {
        EmailAddress email = new EmailAddress((String) value);

        if (!email.isValid()) {
            FacesMessage msg = com.pandamedia.utilities.Messages.getMessage(
                    "bundles.messages", "badEmailFormat", null);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

}

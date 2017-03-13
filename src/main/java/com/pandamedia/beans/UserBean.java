package com.pandamedia.beans;

import java.io.Serializable;
import static java.lang.Math.random;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.enterprise.context.RequestScoped;
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
import persistence.controllers.ProvinceJpaController;
import persistence.controllers.ShopUserJpaController;
import persistence.entities.Province;

/**
 *
 * @author Hau Gilles Che
 */
@Named
@RequestScoped
public class UserBean implements Serializable {

    @Inject
    private ShopUser shopUser;
    @Inject
    private ShopUserJpaController userController;
    @Inject
    private ProvinceJpaController provinceController;
    private String password;
    private String confirmPasswd;
    //used to salt the password
    private  SecureRandom random;
    private List<Province> provinces;

    @PostConstruct
    public void init(){
        provinces=provinceController.findProvinceEntities();
        random = new SecureRandom();
    }
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Province> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<Province> provinces) {
        this.provinces = provinces;
    }
    
    

    public String createNewUser() throws Exception {
        setFields();
        userController.create(shopUser);
        return null;
    }

    public void validatePassword(FacesContext fc, UIComponent c, Object value) {
        if (!password.equals(confirmPasswd)) {
            throw new ValidatorException(new FacesMessage(
                    "Passwords do not match"));
        }
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
            throw new ValidatorException(new FacesMessage(
                    "Email must be formatted properly i.e: renuchan@renuchan.ren"));
        }
    }

    public void setFields() {
        //set address informations empty, to be filled later
        /*
        shopUser.setCity("");
        shopUser.setCountry("");
        shopUser.setHomePhone("");
        shopUser.setPostalCode("");
        shopUser.setStreetAddress("");
        shopUser.setProvinceId(provinceController.findProvince(1));
        */
        //generates a random salt to salt the password
        String salt=getSalt();
        byte[] hashedPwd=hash(password,salt);
        
        shopUser.setSalt(salt);
        shopUser.setHashedPw(hashedPwd);
        
    }

    /**
     * Generates a random salt
     * @author The Bodzay
     * @return The randomly generated salt
     */
    public String getSalt() {
        return new BigInteger(140, random).toString(32);
    }

    /**
     * Creates a hash from the given password and salt
     *
     * @author The Bodzay
     * @param password The password to be used in the hash
     * @param salt The salt to be used in the hash
     * @return The has
     */
    public byte[] hash(String password, String salt) {
        if (password == null || password.equals("")) {
            return null;
        }

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1024, 256);

            SecretKey key = skf.generateSecret(spec);
            byte[] hash = key.getEncoded();
            return hash;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

}

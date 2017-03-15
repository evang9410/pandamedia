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
import javax.inject.Inject;
import javax.inject.Named;
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
    //private SecureRandom secureRand;
    private ShopUser loggedUser;
    private List<Province> provinces;
    private Province province;

    @PostConstruct
    public void init() {
        provinces = provinceController.findProvinceEntities();
       // secureRand = new SecureRandom();
    }

    public void register(String province) {
        System.out.println(province);
        ShopUser user = userBean.getShopUser();
        if (!userController.checkIfUserExists(user)) {
            // User does not exist.
            setFields(user);
            int provinceId=Integer.parseInt(province);
            Province prov=provinceController.findProvince(provinceId);
            System.out.println(prov.getName()+" "+prov.getId());
            user.setProvinceId(prov);
            System.out.println(provinceId);
            try {
                if(province == null)
                    Logger.getLogger(RegistrationActionBean.class.getName()).log(Level.SEVERE, null, "User created");
                //user.setProvinceId(provinces.get(0));
                userController.create(user);
                loggedUser = user;
                Logger.getLogger(RegistrationActionBean.class.getName()).log(Level.SEVERE, null, "User created");
            } catch (Exception ex) {
                Logger.getLogger(RegistrationActionBean.class.getName()).log(Level.SEVERE, null, "RollBackException");
            }

        } else {
            // Display error
            Logger.getLogger(RegistrationActionBean.class.getName()).log(Level.SEVERE, null, "Non Unique user");
        }
    }

    /* Getters and Setter*/
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
    

    private void setFields(ShopUser user) {
        //instantiating the class responsible for password security
        PasswordHelper pwdHelper = new PasswordHelper(); 
        String salt = pwdHelper.getSalt();

        byte[] hashedPwd = pwdHelper.hash("FuckPriver", salt);

        user.setSalt(salt);
        user.setHashedPw(hashedPwd);
        
        user.setPostalCode(userBean.getPostalCode().toString());
    }

}

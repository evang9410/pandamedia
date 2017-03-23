package com.pandamedia.beans;

import com.pandamedia.utilities.PasswordHelper;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
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
import persistence.controllers.UserActionController;
import persistence.entities.Province;
import persistence.entities.ShopUser;

/**
 * Responsible for user connection interaction (login, logout,registration)
 *
 * @author Hau Gilles Che
 * @version 1.0.01
 */
@Named("userAction")
@SessionScoped
public class UserActionBean implements Serializable {

    @Inject
    private UserActionController userActionController;

    @Inject
    private ShopUserJpaController userController;

    @Inject
    private UserBean userBean;

    @Inject
    private ProvinceJpaController provinceController;

    private final PasswordHelper pwdHelper = new PasswordHelper();

    // Needed variables for login and registration
    private ShopUser currUser;
    private List<Province> provinces;
    private Province province;

    @PostConstruct
    public void init() {
        provinces = provinceController.findProvinceEntities();
    }

    public ShopUser getcurrUser() {
        return currUser;
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
     * Responsible for creating a new user records
     *
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
            currUser = user;
            Logger.getLogger(UserActionBean.class.getName()).log(
                    Level.SEVERE, null, "User created");
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("mainpage.xhtml");
        } catch (IOException ioe) {
            Logger.getLogger(UserActionBean.class.getName())
                    .log(Level.WARNING, "Error when redirecting: {0}",
                            ioe.getMessage());
        } catch (Exception ex) {
            FacesMessage msg = com.pandamedia.utilities.Messages.getMessage(
                    "bundles.messages", "duplicateEmail", null);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(
                    "registrationForm:emailInput", msg);
        }
    }

    /**
     * Responsible for login in a user.
     */
    public void login() {
        currUser = userBean.getShopUser();
        ShopUser userRecord = userActionController.findUserByEmail(
                currUser.getEmail());
        if (userRecord == null) {
            FacesMessage msg = com.pandamedia.utilities.Messages.getMessage(
                    "bundles.messages", "duplicateEmail", null);
            FacesContext.getCurrentInstance().addMessage("loginForm", msg);
        } else {
            byte[] hashRecord = userRecord.getHashedPw();
            byte[] loginPwdHash = pwdHelper.hash(userBean.getPassword(),
                    userRecord.getSalt());

            if (!Arrays.equals(hashRecord, loginPwdHash)) {
                FacesMessage msg = com.pandamedia.utilities.Messages.getMessage(
                        "bundles.messages", "invalidEmailOrPwd", null);
                FacesContext.getCurrentInstance().addMessage("loginForm", msg);
                currUser = null;
            } else {
                try {
                    currUser = userRecord;
                    FacesContext.getCurrentInstance().getExternalContext()
                            .redirect("mainpage.xhtml");
                } catch (IOException ioe) {
                    Logger.getLogger(UserActionBean.class.getName())
                            .log(Level.WARNING, "Error when redirecting: {0}",
                                    ioe.getMessage());
                }
            }
        }

    }

    /**
     * Responsible for login out users.
     */
    public void logout() {
        //for security purposes, if an admin logout, redirects to index
        if (currUser.getIsManager() == 1) {
            currUser = null;
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("mainpage.xhtml");
            } catch (IOException ioe) {
                Logger.getLogger(UserActionBean.class.getName())
                        .log(Level.WARNING, "Error when redirecting: {0}",
                                ioe.getMessage());
            }
        }
        currUser = null;

    }

    /**
     * Checks if a user is logged in.
     *
     * @return true if a user is logged in, false otherwise.
     */
    public boolean isLogin() {
        return currUser != null;
    }

    /**
     *
     * @return the current logged in user.
     */
    public ShopUser getCurrUser() {
        return currUser;
    }

    /**
     * Sets the remaining fields that cannot be directly set by the user through
     * the form.
     *
     * @param user
     */
    private void setFields(ShopUser user) {
        String salt = pwdHelper.getSalt();

        byte[] hashedPwd = pwdHelper.hash(userBean.getPassword(), salt);

        user.setSalt(salt);
        user.setHashedPw(hashedPwd);

        user.setPostalCode(userBean.getPostalCode().toString());
        user.setHomePhone(userBean.getHomePhone().toString());

        if (userBean.getCellPhone() != null) {
            user.setCellPhone(userBean.getCellPhone().toString());
        }
    }

    /**
     * Validates the email address to make sure it is properly formatted.
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

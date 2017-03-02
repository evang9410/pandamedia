package com.pandamedia.beans;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import persistence.entities.ShopUser;

/**
 *
 * @author Hau Gilles Che
 */
@Named
@SessionScoped
public class userBean implements Serializable {
    @Inject
    private ShopUser shopUser;

    public ShopUser getShopUser() {
        return shopUser;
    }

    public void setShopUser(ShopUser shopUser) {
        this.shopUser = shopUser;
    }
    
    
}

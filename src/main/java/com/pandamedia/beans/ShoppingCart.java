/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandamedia.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Evang
 */
@Named("cart")
@SessionScoped
public class ShoppingCart implements Serializable{
    private List<Object> cart;
    public ShoppingCart(){
        this.cart = new ArrayList();
    }
    /**
     * returns the list of items in the shopping cart.
     * @return 
     */
    public List<Object> getCart(){
        return this.cart;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandamedia.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.media.Track;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import persistence.entities.Album;

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
    /**
     * Gathers the shopping cart album objects and returns them as a list to be displayed in the cart.
     * @return 
     */
    public List<Album> getAlbumsFromCart(){
        List<Album> albums = new ArrayList();
        for(int i = 0; i < cart.size(); i++){
            if(cart.get(i) instanceof Album){
                albums.add((Album)cart.get(i));
            }
        }
        return albums;
    }
    /**
     * gets the tracks from the cart.
     * There has to be a more efficient way to sort this list. Maybe a hashmap?
     * @return 
     */
    public List<Track> getTracksFromCart(){
        List<Track> tracks = new ArrayList();
        for(int i = 0; i < cart.size(); i++){
            if(cart.get(i) instanceof Track){
                tracks.add((Track)cart.get(i));
            }
        }
        return tracks;
    }
    
}

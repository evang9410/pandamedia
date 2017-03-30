/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandamedia.beans;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import persistence.controllers.ShopUserJpaController;
import persistence.controllers.exceptions.NonexistentEntityException;
import persistence.controllers.exceptions.RollbackFailureException;
import persistence.entities.Album;
import persistence.entities.Genre;
import persistence.entities.ShopUser;

/**
 * Special thanks to BallusC for his stack overflow answer over at
 * http://stackoverflow.com/questions/20934016/how-to-add-cookie-in-jsf/20935305
 * @author Evan Glicakis
 */
@SessionScoped
@Named("clientTracking")
public class ClientTrackingBean implements Serializable{
    @Inject
    private UserActionBean uab;
    @Inject
    private AlbumBackingBean albumBacking;
    @Inject
    private ShopUserJpaController userController;
    private String genreString;
    private boolean isTracking = false;
    
    public List<Album> trackClient(){
        String genre = "";
        if(uab.isLogin()){
            if(uab.getCurrUser().getLastGenreSearched() == null){
                isTracking = false;
                return null;
            }
            genre = uab.getCurrUser().getLastGenreSearched().getName();
            return albumBacking.getSuggestedAlbums(genre);
        }else{
            genre = readCookie("suggested");
            return albumBacking.getSuggestedAlbums(genre);
        }
        
    } 

    public boolean isIsTracking() {
        return isTracking;
    }  
        
    public void peristTracking(Genre g){
        if(uab.isLogin()){
            ShopUser user = uab.getCurrUser();
            user.setLastGenreSearched(g);
            try {
                userController.edit(user);
                isTracking=true;
            }catch (Exception ex) {
                Logger.getLogger(ClientTrackingBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            genreString = g.getName();
            writeCookie("suggested",genreString);
            isTracking = true;
        }
    }
    
    public String readCookie(String name) {
        Cookie cookie =(Cookie)FacesContext.getCurrentInstance()
                .getExternalContext().getRequestCookieMap().get(name);
        String value="";
        try {
            value = URLDecoder.decode(cookie.getValue(),"UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ClientTrackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }
    
    public void writeCookie(String key, String value) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, Object> properties = new HashMap<>();
        properties.put("maxAge",31536000);
        properties.put("path", "/");
        try {
            context.getExternalContext().addResponseCookie(key, URLEncoder.encode(value,"UTF-8"), properties);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ClientTrackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}

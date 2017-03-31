package com.pandamedia.beans.purchasing;

import com.pandamedia.beans.UserBackingBean;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import persistence.entities.Track;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import persistence.entities.Album;

/**
 *
 * @author Evang
 */
@Named("cart")
@SessionScoped
public class ShoppingCart implements Serializable {
//    private List<Object> cart;

    private List<Album> albums;
    private List<Track> tracks;
    private UIViewRoot prevPage;
    @Inject
    private UserBackingBean user;

    public ShoppingCart() {
//        this.cart = new ArrayList();
        albums = new ArrayList<>();
        tracks = new ArrayList<>();
    }

    /**
     * returns the list of items in the shopping cart.
     *
     * @return
     */
//    public List<Object> getCart(){
//        return this.cart;
//    }
    /**
     * Gathers the shopping cart album objects and returns them as a list to be
     * displayed in the cart.
     *
     * @return
     */
    public List<Album> getAlbumsFromCart() {
//        List<Album> albums = new ArrayList();
//        for(int i = 0; i < cart.size(); i++){
//            if(cart.get(i) instanceof Album){
//                albums.add((Album)cart.get(i));
//            }
//        }
        return albums;
    }

    /**
     * gets the tracks from the cart. There has to be a more efficient way to
     * sort this list. Maybe a hashmap?
     *
     * @return
     */
    public List<Track> getTracksFromCart() {
//        List<Track> tracks = new ArrayList();
//        for(int i = 0; i < cart.size(); i++){
//            if(cart.get(i) instanceof Track){
//                tracks.add((Track)cart.get(i));
//            }
//        }
        return tracks;
    }

    public double getSubTotal() {
        double subtotal = 0;
        for (Album a : albums) {
            subtotal += a.getListPrice() - a.getSalePrice();
        }
        for (Track t : tracks) {
            subtotal += t.getListPrice() - t.getSalePrice();
        }
//        for(Object o : cart){
//            if(o instanceof Album){
//                subtotal += ((Album) o).getListPrice() - ((Album) o).getSalePrice();
//            }
//            if(o instanceof Track){
//                subtotal += ((Track)o).getListPrice() - ((Track)o).getSalePrice();
//            }
//        }
        return subtotal;
    }

    /**
     * returns the amount of items in the cart, to be used by the navigation bar
     * to display the amount of items currently in the cart.
     *
     * @return
     */
    public String getCartCount() {
        int size = albums.size() + tracks.size();
//        if(cart.size() > 0)
//            return " ( " + this.cart.size() +" )";
//        else
//            return "";
        if (size > 0) {
            return " ( " + size + " )";
        } else {
            return "";
        }
    }

    public boolean getIsCartEmpty() {
//        boolean isEmpty = false;
//        if(cart.size() == 0){
//           isEmpty= true;
//        }
//        return isEmpty;
        return (albums.size() + tracks.size()) == 0;
    }

    public void removeAlbumFromCart(Album a) {
        albums.remove(a);
    }

    public void removeTrackFromCart(Track t) {
        tracks.remove(t);
    }

//    public void removeItem(Object o){
//        cart.remove(o);
//    }
    /**
     * Sets the UIViewRoot object, to be called when the shopping cart icon in
     * the navigation bar is clicked is clicked.
     *
     * @return
     */
    public String setPrevPage() {
        prevPage = FacesContext.getCurrentInstance().getViewRoot();
        return "cart";
    }

    /**
     * returns the user to the location of the ui where the prevPage object is
     * holding. If the prevPage is null or not defined, return them to the
     * mainpage.
     */
    public void continueShopping() throws IOException {
        if (prevPage != null) {
            FacesContext.getCurrentInstance().setViewRoot(prevPage);
            FacesContext.getCurrentInstance().renderResponse();
        } else {
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            context.redirect(context.getRequestContextPath() + "/mainpage.xhtml");
            System.out.println(context.getRequestContextPath());
        }

    }

    public void addAlbum(Album album) {
        albums.add(album);
    }

    public void addTrack(Track track) {
        tracks.add(track);
    }
    
    public void clearCart()
    {
        albums = new ArrayList<>();
        tracks = new ArrayList<>();
    }
}

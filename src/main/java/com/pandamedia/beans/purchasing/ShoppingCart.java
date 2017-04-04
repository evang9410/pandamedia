package com.pandamedia.beans.purchasing;

import com.pandamedia.beans.UserBackingBean;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    private List<Album> albums;
    private List<Track> tracks;
    private UIViewRoot prevPage;
    @Inject
    private UserBackingBean user;

    public ShoppingCart() {
        albums = new ArrayList<>();
        tracks = new ArrayList<>();
    }

    /**
     * Gathers the shopping cart album objects and returns them as a list to be
     * displayed in the cart.
     *
     * @return
     */
    public List<Album> getAlbumsFromCart() {
        return albums;
    }

    /**
     * gets the tracks from the cart. There has to be a more efficient way to
     * sort this list. Maybe a hashmap?
     *
     * @return
     */
    public List<Track> getTracksFromCart() {
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
        if (size > 0) {
            return " ( " + size + " )";
        } else {
            return "";
        }
    }

    public boolean getIsCartEmpty() {
        return (albums.size() + tracks.size()) == 0;
    }

    public void removeAlbumFromCart(Album a) {
        albums.remove(a);
    }

    public void removeTrackFromCart(Track t) {
        tracks.remove(t);
    }

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
        if (!albums.contains(album)) {
            albums.add(album);
        }
    }

    public void addTrack(Track track) {
        // if the user already has the album of the track they are trying to add,
        // do not add the track. Super dirty and gross, sorry.
        if (albums.contains(track.getAlbumId())) {
            return;
        }
        // get the album that the track belongs to.
        Album album = track.getAlbumId();
        // list of tracks that are in the album.
        List<Track> album_tracks = new ArrayList();
        if (!tracks.contains(track)) {
            tracks.add(track);
            int i = 0;
            for (Track t : tracks) {
                if (t.getAlbumId().getId() == album.getId()) {
                    album_tracks.add(t);
                    i++;
                }
            }
            if(tracks.containsAll(album.getTrackList())){
                tracks.removeAll(album_tracks);
                albums.add(album);
            }
        }

    }

    public void clearCart() {
        albums = new ArrayList<>();
        tracks = new ArrayList<>();
    }
}
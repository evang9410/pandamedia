package com.pandamedia.beans;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * Holds the URL of desired RSSFeed
 * @author Hau Gilles Che
 */
@Named("feedManager")
@SessionScoped
public class RSSFeedManager implements Serializable {
    private String url="http://rss.cbc.ca/lineup/politics.xml";
    
    public RSSFeedManager(){
        //doesnt seem to work
        url="http://rss.cbc.ca/lineup/politics.xml";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }   
}

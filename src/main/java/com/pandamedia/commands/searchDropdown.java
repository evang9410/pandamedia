/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandamedia.commands;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Pierre Azelart
 */
@Named("searchDropdown")
@SessionScoped
public class searchDropdown implements Serializable{
    private String type;
    
    @PostConstruct
    public void init(){
        //Default search type
        type = "Track";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

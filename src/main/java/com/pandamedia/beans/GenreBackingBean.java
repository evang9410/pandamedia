/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandamedia.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import persistence.controllers.GenreJpaController;
import persistence.entities.Genre;

/**
 *
 * @author Evang
 */
@Named("genreBacking")
@RequestScoped
public class GenreBackingBean {
    @Inject
    private GenreJpaController genreController;
    
    private Genre genre;
    
    public Genre getGenre(){
        if(genre == null){
            genre = new Genre();
        }
        return genre;
    }
    
    
}

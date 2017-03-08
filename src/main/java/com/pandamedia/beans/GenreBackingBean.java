/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandamedia.beans;

import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
    @PersistenceContext
    private EntityManager em;
    
    private Genre genre;
    
    public Genre getGenre(){
        if(genre == null){
            genre = new Genre();
        }
        return genre;
    }
    
    public List<String> getAllGenresNames(){
        String q = "SELECT g.name FROM Genre g";
        TypedQuery query = em.createQuery(q, Genre.class).setMaxResults(5); // for testing this is set to 5 results max. In production, we should remove this limit.
        return query.getResultList();
    }
    
    
}

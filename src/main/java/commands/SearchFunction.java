/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Cache;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

/**
 *
 * @author 1710026
 */
@Named("SearchFunction")
@RequestScoped
public class SearchFunction implements Serializable{
    private ArrayList resultsList;
    @Inject
    private EntityManager em;
    private String parameters;
    
    public ArrayList searchDefault(String str){
        //JPA queries
        //set resultsList
        //returns it
       
        
        return resultsList;
    }
    
    public void setParameters(String key){
        this.parameters = key;
    }
    
    public String getParameters(){
        return this.parameters;
    }
}

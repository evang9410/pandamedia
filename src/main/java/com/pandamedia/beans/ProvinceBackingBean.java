
package com.pandamedia.beans;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import persistence.controllers.ProvinceJpaController;
import persistence.entities.Province;

/**
 *
 * @author Naasir
 */
@Named("provinceBacking")
@SessionScoped
public class ProvinceBackingBean implements Serializable{
    @Inject
    private ProvinceJpaController provinceController;
    private Province province;
    @PersistenceContext
    private EntityManager em;
    
    
    
    public Province getProvince(){
        if(province == null){
            province = new Province();
        }
        return province;
    }
      
    public List<Province> getAll()
    {
        return provinceController.findProvinceEntities();
    }
    
    
}

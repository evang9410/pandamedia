/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Pierre Azelart
 */

@ManagedBean
@ViewScoped
public class searchDropdown implements Serializable{
    private String type;
    private ArrayList<String> types;
    
    public void init(){
        types = new ArrayList<>();
        types.add("Track");
        types.add("Album");
        types.add("Artist");
        types.add("Date");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public ArrayList<String> getTypes() {
        return types;
    }
    
    public void onTypeChange(){
        
    }
    
}

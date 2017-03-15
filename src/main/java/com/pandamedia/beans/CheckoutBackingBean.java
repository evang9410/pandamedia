package com.pandamedia.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;

/**
 *
 * @author Erika Bourque
 */
@Named("checkout")
@RequestScoped
public class CheckoutBackingBean {
    
    public List<SelectItem> getMonths()
    {
        List<SelectItem> list = new ArrayList<>();
        list.add(new SelectItem(1, "01"));
        list.add(new SelectItem(2, "02"));
        list.add(new SelectItem(3, "03"));
        list.add(new SelectItem(4, "04"));
        list.add(new SelectItem(5, "05"));
        list.add(new SelectItem(6, "06"));
        list.add(new SelectItem(7, "07"));
        list.add(new SelectItem(8, "08"));
        list.add(new SelectItem(9, "09"));
        list.add(new SelectItem(10, "10"));
        list.add(new SelectItem(11, "11"));
        list.add(new SelectItem(12, "12"));
        return list;
    }
    
    public List<SelectItem> getYears()
    {
        List<SelectItem> list = new ArrayList<>();        
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        int maxYears = 10;
        
        for (int i = 0; i < maxYears; i++)
        {
            list.add(new SelectItem(curYear + i));
        }
        
        return list;
    }
}

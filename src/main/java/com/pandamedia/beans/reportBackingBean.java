package com.pandamedia.beans;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.util.Calendar;
import java.util.Date;

/**
 * This class provides common methods for the report pages.
 * 
 * @author Erika Bourque
 */
@Named
@RequestScoped
public class reportBackingBean implements Serializable{
    
    private Date startDate;
    private Date endDate;
    
    public Date getDefaultEndDate()
    {               
        return Calendar.getInstance().getTime();
    }
    
    public Date getDefaultStartDate()
    {
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_YEAR, -30);
        
        return start.getTime();
    }
    
    public Date getStartDate()
    {
        return startDate;
    }
    
    public Date getEndDate()
    {
        return endDate;
    }
    
    public void setStartDate(Date date)
    {
        startDate = date;
    }
    
    public void setEndDate(Date date)
    {
        endDate = date;
    }
}

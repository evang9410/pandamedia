package com.pandamedia.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

/**
 *
 * @author Erika Bourque
 */
@Named("checkout")
@RequestScoped
public class CheckoutBackingBean {
    // TODO: create converter to handle white spaces in cc number
    // TODO: create validator for luhn check for cc number
    private int cardMonth;
    private int cardYear;
    private int cardNum;
    private String cardType;
    private int cardCode;
    private String cardName;
    
    public int getCardMonth()
    {
        return cardMonth;
    }
    
    public void setCardMonth(int month)
    {
        this.cardMonth = month;
    }
    
    public int getCardYear()
    {
        return cardYear;
    }
    
    public void setCardYear(int year)
    {
        this.cardYear = year;
    }
    
    public int getCardNum()
    {
        return cardNum;
    }
    
    public void setCardNum(int cardNum)
    {
        this.cardNum = cardNum;
    }
    
    public String getCardType()
    {
        return cardType;
    }
    
    public void setCardType(String cardType)
    {
        this.cardType = cardType;
    }
    
    public int getCardCode()
    {
        return cardCode;
    }
    
    public void setCardCode(int cardCode)
    {
        this.cardCode = cardCode;
    }
    
    public String getCardName()
    {
        return cardName;
    }
    
    public void setCardName(String cardName)
    {
        this.cardName = cardName;
    }
    
    public List<SelectItem> getMonthSelector()
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
    
    public List<SelectItem> getYearSelector()
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
    
    public void validateDate(FacesContext context, UIComponent component,
            Object value) {

        // Must read this value from user interface
        UIInput monthInput = (UIInput) component.findComponent("cardMonth");

        int month = ((Integer) monthInput.getLocalValue());
        int year = ((Integer) value);

        if (!isValidDate(month, year))
        {
            // Date in past
            FacesMessage message = com.pandamedia.util.Messages.getMessage(
                    "bundles.messages", "cardPastDateError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }
    
    public boolean isValidDate(int month, int year)
    {
        Calendar current = Calendar.getInstance();
        
        Calendar given = Calendar.getInstance();
        given.set(Calendar.YEAR, year);
        given.set(Calendar.MONTH, month);       
        
        return given.after(current);
    }
}

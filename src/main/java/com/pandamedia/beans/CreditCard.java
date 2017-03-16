package com.pandamedia.beans;

import java.util.Calendar;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

/**
 *
 * @author Erika Bourque
 */
@Named
@RequestScoped
public class CreditCard {
    private int cardMonth;
    private int cardYear;
    private String cardNum;
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
    
    public String getCardNum()
    {
        return cardNum;
    }
    
    public void setCardNum(String cardNum)
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
    
    private boolean isValidDate(int month, int year)
    {
        Calendar current = Calendar.getInstance();
        
        Calendar given = Calendar.getInstance();
        given.set(Calendar.YEAR, year);
        given.set(Calendar.MONTH, month);       
        
        return given.after(current);
    }
    
    public void validateCardNum(FacesContext context, UIComponent component,
            Object value) {
        String number = value.toString();
        String regex = "^\\d+$";
        
        if (!number.matches(regex))
        {
            // Invalid character
            FacesMessage message = com.pandamedia.util.Messages.getMessage(
                    "bundles.messages", "cardNumIllegalChar", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
        
        // Visa allows 13, 16, or 19 digits, MasterCard allows 16 digits
        if ((number.length() != 13) && (number.length() != 16) && (number.length() != 19))
        {
            // Incorrect number of digits
            FacesMessage message = com.pandamedia.util.Messages.getMessage(
                    "bundles.messages", "cardNumWrongNumDigits", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
        
        if (!luhnCheck(number))
        {
            // Luhn check failed
            FacesMessage message = com.pandamedia.util.Messages.getMessage(
                    "bundles.messages", "cardNumBadInput", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }
    
    /**
     * 
     * @author Ken Fogel
     * @param numbers
     * @return 
     */
    private boolean luhnCheck(String numbers)
    {
        int sum = 0;

        for (int i = numbers.length() - 1; i >= 0; i -= 2) {
            sum += Integer.parseInt(numbers.substring(i, i + 1));
            if (i > 0) {
                int d = 2 * Integer.parseInt(numbers.substring(i - 1, i));
                if (d > 9) {
                    d -= 9;
                }
                sum += d;
            }
        }

        return sum % 10 == 0;
    }
}

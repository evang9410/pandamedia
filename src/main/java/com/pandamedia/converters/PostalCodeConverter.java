package com.pandamedia.converters;

import com.pandamedia.beans.PostalCodeBean;
import java.io.Serializable;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Hau Gilles Che
 */
@FacesConverter(forClass=PostalCodeBean.class)
public class PostalCodeConverter implements Converter, Serializable {
    private String separator;
    
    public void setSeparator(String separator){
        this.separator=separator;
    }
    
    /**
     * Used when inputing a value Accept the string and check that includes
     * character or whitespace
     *
     * @param context
     * @param component
     * @param newValue
     * @return
     */
    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String newValue) throws ConverterException {
        StringBuilder builder=new StringBuilder(newValue);
        boolean foundInvalidCharacter=false;
        char invalidChar;
        int i=0;
        while(i<builder.length() && !foundInvalidCharacter){
            char ch=builder.charAt(i);
            if(Character.isAlphabetic(ch)){
                char tempChar=Character.toUpperCase(ch);
                builder.setCharAt(i, tempChar);
                i++;
            }else if(Character.isDigit(ch)){
                i++;
            }else if(Character.isWhitespace(ch)){
                    builder.deleteCharAt(i);              
            }else{
                foundInvalidCharacter=true;
                invalidChar=ch;
            }
        }
        /*
        if(foundInvalidCharacter){
            FacesMessage msg=
        }
*/
        return new PostalCodeBean(builder.toString());
    }
    
    /**
     * Used when display the value Depending on the length of the string add
     * space
     *
     * @param context
     * @param component
     * @param value
     * @return
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component,
            Object value) throws ConverterException {
        if(!(value instanceof PostalCodeBean)){
            throw new ConverterException();
        }
        String v=((PostalCodeBean)value).toString();
        String sep=separator;
        if(sep==null){
            sep=" ";
        }
        //if length is 7, space is already there
        if(v.length() == 7)
            return v;
        
        //inserts the space in the middle
        StringBuilder result=new StringBuilder();
        result.append(v.substring(0,3));
        result.append(sep);
        result.append(v.substring(3));
        
        return result.toString();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandamedia.validators;

import java.util.Calendar;
import java.util.Date;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import persistence.controllers.AdvertisementJpaController;
import persistence.controllers.FrontPageSettingsJpaController;
import persistence.controllers.SurveyJpaController;
import persistence.entities.Advertisement;
import persistence.entities.Survey;

/**
 *
 * @author Evang, Naasir
 */
@FacesValidator(value="frontPageValidator")
public class FrontPageValidator implements Validator {
    @Inject
    private FrontPageSettingsJpaController fpsController;
    @Inject
    private SurveyJpaController surveyController;
    @Inject
    private AdvertisementJpaController adController;

    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) {
        int id = (Integer) value;
        if (component.getId().equals("surveyValidator")) 
            if (fpsController.findFrontPageSettings(1).getSurveyId().equals(surveyController.findSurvey(id))) 
                throw new ValidatorException(new FacesMessage("This survey is used on the front page"));

    }

}

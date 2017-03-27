package com.pandamedia.beans;

import persistence.controllers.SurveyJpaController;
import persistence.controllers.exceptions.RollbackFailureException;
import persistence.entities.Survey;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * This class will be used as the survey backing bean. It is used as a means
 * of getting surveys and querying them.
 * @author Hau Gilles Che, Naasir Jusab
 */
@Named("surveyBean")
@SessionScoped
public class SurveyBean implements Serializable {

    private int surveyId=1;
    private String userChoice;
    private Survey survey;
    private List<String> answers;
    private boolean userAnswered;
    private boolean showOptions;
    
    @Inject
    private FrontPageSettingsJpaController fpsController;
    @Inject
    private SurveyJpaController surveys;
    @Inject
    private SurveyActionController surveyActionController;
    @Inject
    private FrontPageSettingsJpaController fpsController;
   
    
    @PostConstruct
    public void init(){
        //survey = surveyActionController.getCurrentSurvey();
        //survey=surveys.findSurvey(1);
        FrontPageSettings fps = fpsController.findFrontPageSettings(1);
        survey = fps.getSurveyId();
        createAnswerList();
        userAnswered=false;
        showOptions=true;
}
/**
     * This method will return a survey if it exists already. Otherwise, it 
     * will return a new survey object.
     * @return survey object
     */
    public Survey getSurvey(){
        if(survey == null){
            survey = new Survey();
        }
        return survey;
    }
    
     /**
     * Finds the survey from its id.
     * @param id of the survey
     * @return survey object
     */
    public Survey findSurveyById(int id){
        survey = surveys.findSurvey(id); 
        return survey;
    }
    
    /**
     * This method will return all the surveys in the database so it can be 
     * displayed on the data table.
     * @return list of all the surveys
     */
    public List<Survey> getAll()
    {
        return surveys.findSurveyEntities();
    }
    
    /**
     * This method will save the survey to the database and select
     * it so that the manager can change the survey that is being displayed on 
     * the main page.
     * @return null should make it stay on the same page
     */
    public String save()
    {
        try
        {
            surveys.create(survey);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        this.survey = null;
        return null;
    }
        
    /**
     * This method will destroy the survey in the database and it sets the survey
     * object to null so that it does not stay in session scoped.
     * @param id of the survey object
     * @return null should make it stay on the same page
     */
    public String remove(Integer id)
    {
        try
        {
            surveys.destroy(id);
        }
        
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        this.survey = null;
        return null;
    }
    
    /**
     * This method will find the survey from its id and set it in order to change 
     * the survey being displayed on the main page.
     * @param id of the survey object
     * @return null should make it stay on the same page
     */
    public String select(Integer id)
    {
        survey = surveys.findSurvey(id);
        
        FrontPageSettings fps = fpsController.findFrontPageSettings(1);
        fps.setSurveyId(survey);
        
        try
        {
            fpsController.edit(fps);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        
        this.survey = null;
        return null;
    }
    
    
    public String getUserChoice() {
        return userChoice;
    }

    public void setUserChoice(String userChoice) {
        this.userChoice = userChoice;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public boolean isShowOptions() {
        return showOptions;
    }

    public void setShowOptions(boolean showOptions) {
        this.showOptions = showOptions;
    }
    
    
    
    public int getTotalVotes(){
        int totalVotes=0;
        totalVotes+=survey.getVotesA();
        totalVotes+=survey.getVotesB();
        totalVotes+=survey.getVotesC();
        totalVotes+=survey.getVotesD();
        
        return totalVotes;
    }

    public boolean isUserAnswered() {
        return userAnswered;
    }

    public void setUserAnswered(boolean userAnswered) {
        this.userAnswered = userAnswered;
    }
    
    
    
    public String updateSurvey() throws Exception{
        incrementVoteCount();
        userAnswered=true;
        showOptions=false;
        return null;
    }
    
    public void incrementVoteCount() throws RollbackFailureException, Exception{
        //surveyResults.setDisplayed(true);
      int voteNum=0;
      if(userChoice.equalsIgnoreCase(survey.getAnswerA())){
          voteNum=survey.getVotesA();
          voteNum++;
          survey.setVotesA(voteNum);
      }else if(userChoice.equalsIgnoreCase(survey.getAnswerB())){
          voteNum=survey.getVotesB();
          voteNum++;
          survey.setVotesB(voteNum);
      }else if(userChoice.equalsIgnoreCase(survey.getAnswerC())){
          voteNum=survey.getVotesC();
          voteNum++;
          survey.setVotesC(voteNum);
      }else{
          voteNum=survey.getVotesD();
          voteNum++;
          survey.setVotesD(voteNum);
      }
      surveys.edit(survey);
    }
    
    private void createAnswerList(){
        answers=new ArrayList<>();
        answers.add(survey.getAnswerA());
        answers.add(survey.getAnswerB());
        answers.add(survey.getAnswerC());
        answers.add(survey.getAnswerD());
    }
    
}

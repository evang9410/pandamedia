package com.pandamedia.beans;

import persistence.controllers.SurveyJpaController;
import persistence.controllers.exceptions.RollbackFailureException;
import persistence.entities.Survey;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Hau Gilles Che
 */
@Named("surveyBean")
@RequestScoped
public class SurveyBean implements Serializable {
    private int surveyId=1;
    private String userChoice;
    private Survey survey;
    private List<String> answers;
    private boolean userAnswered;
    private boolean showOptions;
    
    @Inject
    private SurveyJpaController surveys;
   
    
    @PostConstruct
    public void init(){
        survey=surveys.findSurvey(1);
    
        createAnswerList();
        userAnswered=false;
        showOptions=true;
        
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

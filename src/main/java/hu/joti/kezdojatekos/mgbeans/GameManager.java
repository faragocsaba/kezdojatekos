/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.joti.kezdojatekos.mgbeans;

import javax.inject.Named;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import hu.joti.kezdojatekos.model.Question;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Joti
 */
@ManagedBean
@Named(value = "gameManager")
@SessionScoped
public class GameManager implements Serializable {

  @ManagedProperty("#{questionManager}")
  QuestionManager questionManager;

  private List<Question> recentQuestions;
  private Question lastQuestion;
  private int questionIndex;
  private boolean addIndiscreet = false;
  private boolean noEquivocal = false;

  // Az összes kérdés hány százalékának kell elfogynia ahhoz, hogy egy kérdés ismét előfordulhasson
  private static final double QUESTION_REPEAT_RATE = 0.3;
  private static final Logger LOGGER = LogManager.getLogger(GameManager.class.getName());

  public GameManager() {
    recentQuestions = new ArrayList<>();
    lastQuestion = null;
    LOGGER.trace("GameManager starting");
  }

  public void prevQuestion(){
    if (questionIndex > 1){
      questionIndex--;
      lastQuestion = recentQuestions.get(questionIndex - 1);
    }
  }

  public void newQuestion(){
     questionIndex = recentQuestions.size();
     nextQuestion();
  }

  public void nextQuestion(){
    if (questionIndex < recentQuestions.size()){
      questionIndex++;
      lastQuestion = recentQuestions.get(questionIndex - 1);
    } else {
    
      LOGGER.debug("questionIndex: " + questionIndex);
      
      List<Question> availQuestions = new ArrayList<>(questionManager.getActiveQuestions(noEquivocal, addIndiscreet));
      int allSize = availQuestions.size();

      for (Question question : recentQuestions) {
        availQuestions.removeIf(b -> b.getId() == question.getId());
      }

      availQuestions.removeAll(recentQuestions);
      LOGGER.debug("Összes: " + allSize + ", megkérdezve: " + recentQuestions.size() + ", elérhető: " + availQuestions.size());

      Random rnd = new Random();
      int rndIndex = rnd.nextInt(availQuestions.size());
      Question rndQuestion = availQuestions.get(rndIndex);
      try {
        lastQuestion = (Question) rndQuestion.clone();
      } catch (CloneNotSupportedException ex) {
        LOGGER.error(ex.toString());
      }
      LOGGER.debug(lastQuestion.getText()); 
      
      String newText;
      int brStart = lastQuestion.getText().indexOf("[");
      if (brStart >= 0){
         int brEnd = lastQuestion.getText().indexOf("]");
         if (brEnd > brStart + 1){
           String[] items = lastQuestion.getText().substring(brStart + 1, brEnd).split(";");

           rndIndex = rnd.nextInt(items.length);
           newText = lastQuestion.getText().substring(0, brStart) + items[rndIndex] + lastQuestion.getText().substring(brEnd + 1);
           LOGGER.debug("Új kérdés: " + newText);
           lastQuestion.setText(newText);
         }
      }  

      recentQuestions.add(lastQuestion);
      questionIndex++;
      
      LOGGER.debug("Ismétlődési határ: " + ((int)(QUESTION_REPEAT_RATE * allSize)));
      if (recentQuestions.size() > (int)(QUESTION_REPEAT_RATE * allSize)){
        int qIndex = 0;
        do {
          if (recentQuestions.get(qIndex).isUnequivocal() || !noEquivocal){
            LOGGER.debug("Kérdés ismét jöhet: " + recentQuestions.get(qIndex).getText());
            recentQuestions.remove(qIndex);
            questionIndex--;
            qIndex = -1;
          } else {
            qIndex++;
          }
        } while (qIndex >= 0 && qIndex < recentQuestions.size());
      }
    }  
  };
  
  public QuestionManager getQuestionManager() {
    return questionManager;
  }

  public int getQuestionNumber(){
    return recentQuestions.size();
  }

  public String getLastQuestionText(){
    if (!(lastQuestion.getExplanation().isEmpty()))
      return lastQuestion.getText().toUpperCase() + "\u00A0" + "*";
    else 
      return lastQuestion.getText().toUpperCase();
  }

  public String getLastQuestionExplanation(){
    String explanation = lastQuestion.getExplanation().toUpperCase();
    if (!explanation.isEmpty())
      explanation = "* " + explanation;
    return explanation;
  }

  public Question getLastQuestion() {
    return lastQuestion;
  }

  public void setLastQuestion(Question lastQuestion) {
    this.lastQuestion = lastQuestion;
  }
  
  public void setQuestionManager(QuestionManager questionManager) {
    this.questionManager = questionManager;
  }

  public List<Question> getRecentQuestions() {
    return recentQuestions;
  }

  public void setRecentQuestions(List<Question> recentQuestions) {
    this.recentQuestions = recentQuestions;
  }

  public int getQuestionIndex() {
    return questionIndex;
  }

  public void setQuestionIndex(int questionIndex) {
    this.questionIndex = questionIndex;
  }

  public boolean isAddIndiscreet() {
    return addIndiscreet;
  }

  public void setAddIndiscreet(boolean addIndiscreet) {
    this.addIndiscreet = addIndiscreet;
  }

  public boolean isNoEquivocal() {
    return noEquivocal;
  }

  public void setNoEquivocal(boolean noEquivocal) {
    this.noEquivocal = noEquivocal;
  }
  
}

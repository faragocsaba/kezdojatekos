/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mgbeans;

import javax.inject.Named;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import model.Question;
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

  // Az összes kérdés hány százalékának kell elfogynia ahhoz, hogy egy kérdés ismét előfordulhasson
  private static final double QUESTION_REPEAT_RATE = 0.3;
  private static final Logger LOGGER = LogManager.getLogger(GameManager.class.getName());

  public GameManager() {
    recentQuestions = new ArrayList<>();
    lastQuestion = null;
    LOGGER.trace("GameManager starting");
  }

  public void nextQuestion(){
    List<Question> availQuestions = new ArrayList<>(questionManager.getAllQuestions());
    int allSize = availQuestions.size();
    LOGGER.debug("Összes kérdés száma: " + allSize);
    LOGGER.debug("Már megkérdezve: " + recentQuestions.size());
    availQuestions.removeAll(recentQuestions);
    LOGGER.debug("Elérhető kérdések száma: " + availQuestions.size());
    
    Random rnd = new Random();
    int rndIndex = rnd.nextInt(availQuestions.size());
    LOGGER.debug("Véletlen szám: " + rndIndex);
    lastQuestion = availQuestions.get(rndIndex);
    LOGGER.debug(lastQuestion.getText()); 
    
    recentQuestions.add(lastQuestion);
    LOGGER.debug("Már megkérdezve 2: " + recentQuestions.size());
    LOGGER.debug("Ismétlődési határ: " + ((int)(QUESTION_REPEAT_RATE * allSize)));
    if (recentQuestions.size() > (int)(QUESTION_REPEAT_RATE * allSize)){
      LOGGER.debug("Kérdés ismét jöhet: " + recentQuestions.get(0).getText());
      recentQuestions.remove(0);
    }
  };
  
  public QuestionManager getQuestionManager() {
    return questionManager;
  }

  public int getQuestionNumber(){
    return recentQuestions.size();
  }

  public String getLastQuestionText(){
    return lastQuestion.getText();
  }

  public String getLastQuestionExplanation(){
    return lastQuestion.getExplanation();
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
  
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.joti.kezdojatekos.mgbeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
import hu.joti.kezdojatekos.model.Category;
import hu.joti.kezdojatekos.model.Question;
import hu.joti.kezdojatekos.model.QuestionDao;
import hu.joti.kezdojatekos.model.QuestionDaoMysql;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Joti
 */
@ManagedBean
@Named(value = "questionManager")
@ApplicationScoped
public class QuestionManager implements Serializable {

  private List<Question> allQuestions;
  private List<Category> categories;

  private static final Logger LOGGER = LogManager.getLogger(QuestionManager.class.getName());
  
  public QuestionManager() {
    LOGGER.trace("QuestionManager starting");
    allQuestions = new ArrayList<>();
    loadQuestions();
  }

  public void loadQuestions(){
    QuestionDao questionDao = new QuestionDaoMysql();

    categories = questionDao.findAllCategories();
    LOGGER.debug("Kategóriák száma:" + categories.size());

    Map<Integer,Category> categoryMap = new TreeMap<>();

    for (Category category : categories) {
      categoryMap.put(category.getId(), category);
    }

    allQuestions = questionDao.findAllQuestions(categoryMap);

    int brStart, brEnd;
    String[] items;
    String newText;
    int maxId = 0;
    List<Question> newQuestions = new ArrayList<>();

    for (Question question : allQuestions) {
      if (question.getId() > maxId) 
        maxId = question.getId();
    }

    for (Question question : allQuestions) {
      brStart = question.getText().indexOf("{");
      if (brStart >= 0){
        LOGGER.debug("Van zárójel: " + question.getText());
        brEnd = question.getText().indexOf("}");
        if (brEnd > brStart + 1){
          items = question.getText().substring(brStart + 1, brEnd).split(";");
          for (String item : items) {
            LOGGER.debug("item: " + item);
            newText = question.getText().substring(0, brStart) + item + question.getText().substring(brEnd + 1);
            LOGGER.debug("newText: " + newText);
            maxId++;
            Question newQuestion = new Question(maxId, newText, question.getExplanation(), question.isActive(), question.isUnequivocal(), question.isIndiscreet(), question.getCategory());
            newQuestions.add(question);
          }
          question.setActive(false);
        }
      }
    }
    
    LOGGER.debug("Kérdések száma:" + allQuestions.size());
    LOGGER.debug("Új kérdések száma:" + newQuestions.size());
    allQuestions.addAll(newQuestions);
    LOGGER.debug("Kérdések száma:" + allQuestions.size());
  }

  public void saveQuestionsToFile(){
    LOGGER.debug("Kérdések mentése fájlba...");
  }
          
  public void readQuestionsFromFile(){
    LOGGER.debug("Fájl feltöltése...");
  }
  
  public List<Question> getActiveQuestions(boolean noEquivocal, boolean addIndiscreet) {
    List<Question> questions = new ArrayList<>(allQuestions);
    List<Question> inactQuestions = new ArrayList<>();

    for (Question question : questions) {
      if (!question.isActive() || (noEquivocal && !question.isUnequivocal()) || (!addIndiscreet && question.isIndiscreet()))
        inactQuestions.add(question);
    }
    
    questions.removeAll(inactQuestions);
    return questions;
  }

  public List<Question> getAllQuestions() {
    return allQuestions;
  }

  public void setAllQuestions(List<Question> allQuestions) {
    this.allQuestions = allQuestions;
  }
  
}

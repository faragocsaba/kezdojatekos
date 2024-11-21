/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mgbeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
import model.Category;
import model.Question;
import model.QuestionDao;
import model.QuestionDaoMysql;
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

    QuestionDao questionDao;
            
    questionDao = new QuestionDaoMysql();

    categories = questionDao.findAllCategories();
    LOGGER.debug("Kategóriák száma:" + categories.size());

    Map<Integer,Category> categoryMap = new TreeMap<>();

    for (Category category : categories) {
      categoryMap.put(category.getId(), category);
    }

    allQuestions = questionDao.findAllQuestions(categoryMap);
    LOGGER.debug("Kérdések száma:" + allQuestions.size());

  }

  public List<Question> getAllQuestions() {
    return allQuestions;
  }

  public void setAllQuestions(List<Question> allQuestions) {
    this.allQuestions = allQuestions;
  }
  
}

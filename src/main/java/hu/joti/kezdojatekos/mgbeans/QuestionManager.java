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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.IllegalFormatException;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
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
  private static final String CSV_DELIMITER = "|";
  
  public QuestionManager() {
    LOGGER.trace("QuestionManager starting");
    allQuestions = new ArrayList<>();
    loadQuestions();
  }

  public void loadQuestions(){
    QuestionDao questionDao = new QuestionDaoMysql();

    categories = questionDao.findAllCategories();
    LOGGER.debug("Kategóriák száma:" + categories.size());

    Map<Integer,Category> categoryMap = getCategoryMap();

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
            Question newQuestion = new Question(maxId, newText, question.getExplanation(), question.getWeight(), question.isActive(), question.isUnequivocal(), question.isIndiscreet(), question.getCategory());
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

  public List<Question> readQuestionsFromFile(Part file, boolean uniqueChk) throws IllegalArgumentException {
    LOGGER.debug("Fájl feltöltése.....");
    LOGGER.debug(file.getName());

    String fileName = Paths.get(file.getSubmittedFileName()).getFileName().toString();
    LOGGER.debug("filename: " + fileName);

    InputStream input = null;
    List <Question> questions = new ArrayList();
    boolean header = true;

    Map<Integer,Category> categoryMap = getCategoryMap();
    Map<String,Question> questionMap = null;
    if (uniqueChk){
      questionMap = getQuestionMap();
    }
    
    try {
      input = file.getInputStream();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
          String line;
          Question q;
          while ((line = reader.readLine()) != null) {
              LOGGER.debug(line);
              
              String[] values = line.split(CSV_DELIMITER);
              
              if (header && (values[2].trim().equals("1") || values[2].trim().equals("0"))){
                continue;
              }
              header = false;
              
              if (uniqueChk){
                if ( questionMap.get(values[0].trim()) != null ){
                  throw new IllegalArgumentException("A kérdés már szerepel az adatbázisban: " + values[0].trim());
                }
              }
              
              q = new Question();
              q.setText(values[0].trim());
              q.setExplanation(values[1].trim());
              q.setActive(values[2].trim().equals("1"));
              q.setUnequivocal(values[3].trim().equals("1"));
              q.setIndiscreet(values[4].trim().equals("1"));
              
              try {
                int categoryId = Integer.parseInt(values[5].trim());
                if ( categoryMap.get(categoryId) != null )
                  q.setCategory( categoryMap.get(categoryId) );
                else
                  throw new IllegalArgumentException("Ismeretlen kategória.");
              }
              catch (NumberFormatException e) {
                throw new IllegalArgumentException("A megadott kategória azonosító nem szám.");
              }              
              
              questions.add(q);
              
          }
      } catch (FileNotFoundException e) {
          LOGGER.info("Hiba: FileNotFoundException");
          throw new IllegalArgumentException("Fájl nem található.");
      } catch (IOException e) {
          LOGGER.info("Hiba: IOException");
          throw new IllegalArgumentException("Fájl olvasási hiba.");
      }      
      
    }
    catch (IOException e) {
      LOGGER.info("Hiba: IOException");
      throw new IllegalArgumentException("File read error.");
    }    

    return questions;
  }

  public void saveQuestions( List<Question> questions, boolean append ){
    LOGGER.debug("Kérdések mentése...");
    QuestionDao questionDao = new QuestionDaoMysql();

    LOGGER.debug("...most még nem mentünk.");   
//    questionDao.saveAllQuestions(questions, append);
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

  public Map<Integer,Category> getCategoryMap(){
    Map<Integer,Category> categoryMap = new TreeMap<>();
    for (Category category : categories) {
      categoryMap.put(category.getId(), category);
    }
    return categoryMap;
  }  

  public Map<String,Question> getQuestionMap(){
    Map<String,Question> questionMap = new TreeMap<>();
    for (Question question : allQuestions) {
      questionMap.put(question.getText(), question);
    }
    return questionMap;
  }  

  public void saveCategoriesToFile() {
    LOGGER.info("Saving categories...");

    String fileName = "kategoriak.csv";
    LOGGER.info(fileName);

    FacesContext fc = FacesContext.getCurrentInstance();
    HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

    response.reset();
    response.setContentType("text/plain");
    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

    PrintWriter writer;

    try {
      response.setCharacterEncoding("UTF-8");
      writer = response.getWriter();
      writer.println("AZONOSÍTÓ" + CSV_DELIMITER + "MEGNEVEZÉS");
      for (Category c : categories) {
        writer.println( c.getId() + CSV_DELIMITER + 
                        c.getName() );
      }
      writer.close ();      
    } catch (IOException ex) {
      LOGGER.error("Hiba a fájlba írás során.");
    }

    LOGGER.info(response.getContentType());
    fc.responseComplete();
  }

  public void saveQuestionsToFile() {
    LOGGER.info("Saving questions");

    String fileName = "kerdesek.csv";
    LOGGER.info(fileName);

    FacesContext fc = FacesContext.getCurrentInstance();
    HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

    response.reset();
    response.setContentType("text/plain");
    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

    PrintWriter writer;

    try {
      response.setCharacterEncoding("UTF-8");
      writer = response.getWriter();
      writer.println("KÉRDÉS" + CSV_DELIMITER + "KIEGÉSZÍTÉS" + CSV_DELIMITER + "AKTÍV" + CSV_DELIMITER + "ELLENŐRIZHETŐ" + CSV_DELIMITER + "KELLEMETLEN" + CSV_DELIMITER + "KATEGÓRIA");
      for (Question q : allQuestions) {
        writer.println( q.getText() + CSV_DELIMITER + 
                        q.getExplanation() + CSV_DELIMITER + 
                        (q.isActive() ? "1" : "0") + CSV_DELIMITER +
                        (q.isUnequivocal() ? "1" : "0") + CSV_DELIMITER +
                        (q.isIndiscreet() ? "1" : "0") + CSV_DELIMITER +
                        q.getCategory().getId() );
      }
      writer.close ();      
    } catch (IOException ex) {
      LOGGER.error("Hiba a fájlba írás során.");
    }

    LOGGER.info(response.getContentType());
    fc.responseComplete();
  }
  
  public int getQuestionCount(){
    return allQuestions.size();
  }
  
  public List<Question> getAllQuestions() {
    return allQuestions;
  }

  public void setAllQuestions(List<Question> allQuestions) {
    this.allQuestions = allQuestions;
  }

  public List<Category> getCategories() {
    return categories;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }
  
}

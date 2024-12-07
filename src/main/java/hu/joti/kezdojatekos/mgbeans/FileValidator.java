/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.joti.kezdojatekos.mgbeans;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Joti
 */
@ManagedBean
@RequestScoped
@FacesValidator("fileValidator")
public class FileValidator implements Validator{

  @ManagedProperty("#{questionManager}")
  QuestionManager questionManager;

  @ManagedProperty("#{gameManager}")
  GameManager gameManager;

  private static final Logger LOGGER = LogManager.getLogger(FileValidator.class.getName());

  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    LOGGER.debug("FileValidator running");
    if (value == null){
      FacesMessage msg = new FacesMessage("Nincs kiválasztva fájl.");
      msg.setSeverity(FacesMessage.SEVERITY_ERROR);
      throw new ValidatorException(msg);
    }
    
    Part file = (Part) value;
    LOGGER.info("File size: " + file.getSize());
    
    if (file.getSize() > 10000) {
      FacesMessage msg = new FacesMessage("Nem megfelelő fájl.");
      msg.setSeverity(FacesMessage.SEVERITY_ERROR);
      throw new ValidatorException(msg);
    }
    
    LOGGER.info("B");

    try {
      boolean fullLoad = gameManager.isFullLoad();
      questionManager.readQuestionsFromFile(file, !fullLoad);
    } catch (IllegalArgumentException ex) {
      LOGGER.info("A fájl tartalma nem megfelelő: " + ex.getMessage());
      FacesMessage msg = new FacesMessage("A fájl tartalma nem megfelelő: " + ex.getMessage());
      msg.setSeverity(FacesMessage.SEVERITY_ERROR);
      throw new ValidatorException(msg);
    }    
    
  }

  public QuestionManager getQuestionManager() {
    return questionManager;
  }

  public void setQuestionManager(QuestionManager questionManager) {
    this.questionManager = questionManager;
  }
  
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.joti.kezdojatekos.mgbeans;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
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
@FacesValidator("fileValidator")
public class FileValidator implements Validator, Serializable{

  private static final Logger LOGGER = LogManager.getLogger(FileValidator.class.getName());

  public FileValidator() {
    LOGGER.trace("FileValidator starting");
  }

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
    
    QuestionManager questionManager = context.getApplication().evaluateExpressionGet(context, "#{questionManager}", QuestionManager.class);
    GameManager gameManager = context.getApplication().evaluateExpressionGet(context, "#{gameManager}", GameManager.class);
    
    LOGGER.info(gameManager == null);
    LOGGER.info(questionManager == null);

    try {
      boolean fullLoad = gameManager.isFullLoad();
      LOGGER.info("fullLoad = " + fullLoad);
      questionManager.readQuestionsFromFile(file, !fullLoad);
    } catch (IllegalArgumentException ex) {
      LOGGER.info("A fájl tartalma nem megfelelő: " + ex.getMessage());
      FacesMessage msg = new FacesMessage("A fájl tartalma nem megfelelő: " + ex.getMessage());
      msg.setSeverity(FacesMessage.SEVERITY_ERROR);
      throw new ValidatorException(msg);
    }    
    
  }
  
}

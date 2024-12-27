/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.joti.kezdojatekos.mgbeans;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Joti
 */
@ManagedBean
@Named(value = "viewManager")
@ViewScoped
public class ViewManager implements Serializable {

  @ManagedProperty("#{gameManager}")
  GameManager gameManager;

  private boolean admin = false;
  private String paramText = "ABCDE";
  private static final Logger LOGGER = LogManager.getLogger(ViewManager.class.getName());

  public ViewManager() {
    LOGGER.debug("ViewManager starting");
  }

  @PostConstruct
  public void init() {
    chkAdmin();
  }
    
  public void chkAdmin(){
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    String username = request.getParameter("username");  
    admin = (username != null && isAdminLogin(username));
    LOGGER.info("username: " + username + ", " + "admin: " + admin);
  }

  public static boolean isAdminLogin(String name) {
    final String adminHash = "2C3EA4960E07C5E88EEDDA070519662DB2B442F532160173E71F3EEE865A77E4AE7229D6EFB862AFAAFB4C7C40B166E15250F2DB5B49F11CF8C8942BB01A197F";
    byte[] hashedName;
    boolean isAdmin = false;
    
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      hashedName = md.digest(name.getBytes(StandardCharsets.UTF_8));
      isAdmin = (DatatypeConverter.printHexBinary(hashedName).equals(adminHash));
    } catch (NoSuchAlgorithmException ex) {
      LOGGER.error(ex);
    }

    return isAdmin; 
  }

  public GameManager getGameManager() {
    return gameManager;
  }

  public void setGameManager(GameManager gameManager) {
    this.gameManager = gameManager;
  }

  public boolean isAdmin() {
    return admin;
  }

  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  public String getParamText() {
    return paramText;
  }

  public void setParamText(String paramText) {
    this.paramText = paramText;
  }
  
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.Serializable;

/**
 * @author Joti
 */
public class Question implements Serializable, Cloneable {

  private int id;
  private String text;
  private String explanation;
  private boolean active;
  private boolean unequivocal;
  private boolean indiscreet;
  private Category category;

  public Question(int id, String text, String explanation, boolean active, boolean unequivocal, boolean indiscreet, Category category) {
    this.id = id;
    this.text = text;
    this.explanation = explanation;
    this.active = active;
    this.unequivocal = unequivocal;
    this.indiscreet = indiscreet;
    this.category = category;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone(); //To change body of generated methods, choose Tools | Templates.
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getExplanation() {
    return explanation;
  }

  public void setExplanation(String explanation) {
    this.explanation = explanation;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isUnequivocal() {
    return unequivocal;
  }

  public void setUnequivocal(boolean unequivocal) {
    this.unequivocal = unequivocal;
  }

  public boolean isIndiscreet() {
    return indiscreet;
  }

  public void setIndiscreet(boolean indiscreet) {
    this.indiscreet = indiscreet;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }
  
}

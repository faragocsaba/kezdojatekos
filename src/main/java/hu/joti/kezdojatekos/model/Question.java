package hu.joti.kezdojatekos.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Question implements Serializable, Cloneable {

  private int id;
  private String text;
  private String explanation;
  private int weight;
  private boolean active;
  private boolean unequivocal;
  private boolean indiscreet;
  private Category category;
  
  private List<Player> players;
  private Set<Player> winners;
  private boolean composite;
  private boolean derived;
  private boolean decider;
  private int seq;

  private static final Logger LOGGER = LogManager.getLogger(Question.class.getName());

  public Question(int id, String text, String explanation, int weight, boolean active, boolean unequivocal, boolean indiscreet, Category category) {
    this.id = id;
    this.text = text;
    this.explanation = explanation;
    this.weight = weight;            
    this.active = active;
    this.unequivocal = unequivocal;
    this.indiscreet = indiscreet;
    this.category = category;
  }

  public Question() {
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

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public boolean isComposite() {
    return composite;
  }

  public void setComposite(boolean composite) {
    this.composite = composite;
  }

  public boolean isDerived() {
    return derived;
  }

  public void setDerived(boolean derived) {
    this.derived = derived;
  }

  public boolean isDecider() {
    return decider;
  }

  public void setDecider(boolean decider) {
    this.decider = decider;
  }

  public int getSeq() {
    return seq;
  }

  public void setSeq(int seq) {
    this.seq = seq;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public void setPlayers(List<Player> players) {
    this.players = players;
  }

  public Set<Player> getWinners() {
    return winners;
  }

  public void setWinners(Set<Player> winners) {
    this.winners = winners;
  }
  
}

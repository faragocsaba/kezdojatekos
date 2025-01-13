package hu.joti.kezdojatekos.model;

import java.io.Serializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Player implements Serializable, Cloneable {

  private int seq;
  private String name;
  private String color;
  
  private boolean winner;

  private static final Logger LOGGER = LogManager.getLogger(Player.class.getName());

  public Player() {
  }
  
  public Player(int seq, String name, String color) {
    this.seq = seq;
    this.name = name;
    this.color = color;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone(); //To change body of generated methods, choose Tools | Templates.
  }
  
  public int getSeq() {
    return seq;
  }

  public void setSeq(int seq) {
    this.seq = seq;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public boolean isWinner() {
    return winner;
  }

  public void setWinner(boolean winner) {
    this.winner = winner;
  }
  
}

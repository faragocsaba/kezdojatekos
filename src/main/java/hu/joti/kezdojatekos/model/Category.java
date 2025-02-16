package hu.joti.kezdojatekos.model;

import java.io.Serializable;

public class Category implements Serializable {

  private int id;
  private String name;
  private int sortOrder;

  public int getId() {
    return id;
  }

  public Category(int id, String name, int sortOrder) {
    this.id = id;
    this.sortOrder = sortOrder;
    this.name = name;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(int sortOrder) {
    this.sortOrder = sortOrder;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  
}

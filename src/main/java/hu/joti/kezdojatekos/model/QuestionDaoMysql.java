package hu.joti.kezdojatekos.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QuestionDaoMysql implements QuestionDao, Serializable {

  private static final Logger LOGGER = LogManager.getLogger(QuestionDaoMysql.class.getName());
  
  private static Connection getConnection() {

    Connection conn = null;
    try {
      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup("java:comp/env");
      DataSource ds = (DataSource) envCtx.lookup("jdbc/kezdojatekos");
      conn = ds.getConnection();
    } catch (NamingException ex) {
      LOGGER.error(ex);
    } catch (SQLException ex) {
      LOGGER.error(ex);
    }
    
    return conn;
  }

  @Override
  public List<Category> findAllCategories() {
    List<Category> categories = null;

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      conn = getConnection();
      if (conn != null){
        categories = new ArrayList<>();

        pstmt = conn.prepareStatement("select * from category");
        rs = pstmt.executeQuery();

        while (rs.next()) {
          int id = rs.getInt("category_id");
          String name = rs.getString("name");
          int sortOrder = rs.getInt("sort_order");
          
          Category category = new Category(id, name, sortOrder);
          categories.add(category);
        }  
      }
    } catch (SQLException ex) {
      LOGGER.error(ex);
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException ex) {
          LOGGER.error(ex);
        }
      }
      if (pstmt != null) {
        try {
          pstmt.close();
        } catch (SQLException ex) {
          LOGGER.error(ex);
        }
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException ex) {
          LOGGER.error(ex);
        }
      }
    }

    if (categories != null) {
      LOGGER.info("No. of categories in db: " + categories.size());
    }  
    return categories;
  }
  
  @Override
  public List<Question> findAllQuestions(Map<Integer, Category> categories) {
    List<Question> questions = null;

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      conn = getConnection();
      if (conn != null){
        questions = new ArrayList<>();

        pstmt = conn.prepareStatement("select * from question");
        rs = pstmt.executeQuery();

        while (rs.next()) {
          int question_id = rs.getInt("question_id");
          String text = rs.getString("text");
          String explanation = rs.getString("explanation");
          int weight = rs.getInt("weight");
          boolean active = rs.getBoolean("is_active");
          boolean unequivocal = rs.getBoolean("is_unequivocal");
          boolean indiscreet = rs.getBoolean("is_indiscreet");
          int category_id = rs.getInt("category_id");
          
          Category category = categories.get(category_id);

          Question question = new Question(question_id, text, explanation, weight, active, unequivocal, indiscreet, category);
          questions.add(question);
        }  
      }
    } catch (SQLException ex) {
      LOGGER.error(ex);
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException ex) {
          LOGGER.error(ex);
        }
      }
      if (pstmt != null) {
        try {
          pstmt.close();
        } catch (SQLException ex) {
          LOGGER.error(ex);
        }
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException ex) {
          LOGGER.error(ex);
        }
      }
    }

    if (questions != null) {
      LOGGER.info("No. of questions in db: " + questions.size());
      LOGGER.debug("Ez egy debug debug debug...");
      LOGGER.trace("Ez egy trace trace trace...");
    }  
    return questions;
  }

  @Override
  public void saveAllQuestions(List<Question> questions, boolean append) {
    Connection conn = null;
    PreparedStatement pstmt = null;
    int wordNum = 0;

    LOGGER.info("questions.size: " + questions.size());

    try {
      conn = getConnection();

      if (!append){
        pstmt = conn.prepareStatement("truncate question");
        pstmt.execute();
      }  

      pstmt = conn.prepareStatement("insert into question (text, explanation, weight, is_active, is_unequivocal, is_indiscreet, category_id) values (?, ?, ?, ?, ?, ?, ?);");

      for (Question q : questions) {
        pstmt.setString(1, q.getText());
        pstmt.setString(2, q.getExplanation());
        pstmt.setInt(3, q.getWeight() );
        pstmt.setInt(4, q.isActive()? 1 : 0 );
        pstmt.setInt(5, q.isUnequivocal() ? 1 : 0 );
        pstmt.setInt(6, q.isIndiscreet() ? 1 : 0 );
        pstmt.setInt(7, q.getCategory().getId());
        pstmt.executeUpdate();
      }

    } catch (SQLException ex) {
      LOGGER.error(ex);
    } finally {
      if (pstmt != null) {
        try {
          pstmt.close();
        } catch (SQLException ex) {
          LOGGER.error(ex);
        }
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException ex) {
          LOGGER.error(ex);
        }
      }
    }
  }

}

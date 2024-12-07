package hu.joti.kezdojatekos.model;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Joti
 */
public interface QuestionDao {

  List<Category> findAllCategories();
  List<Question> findAllQuestions(Map<Integer, Category> categories);
  void saveAllQuestions(List<Question> questions, boolean append);
  
}

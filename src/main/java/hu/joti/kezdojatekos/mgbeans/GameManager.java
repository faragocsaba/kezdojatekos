package hu.joti.kezdojatekos.mgbeans;

import hu.joti.kezdojatekos.model.BgImageManager;
import javax.inject.Named;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import hu.joti.kezdojatekos.model.Player;
import hu.joti.kezdojatekos.model.Question;
import hu.joti.kezdojatekos.model.Category;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.Part;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

@ManagedBean
@Named(value = "gameManager")
@SessionScoped
public class GameManager implements Serializable {

  @ManagedProperty("#{questionManager}")
  QuestionManager questionManager;

  private List<Question> recentQuestions;
  private List<Player> currentPlayers;
  private Question lastQuestion;
  private Part file;
  private UploadedFile inputfile;

  private int questionIndex; /* A recentQuestions hányadik eleme (1-től indul) */
  private boolean addIndiscreet = false;
  private boolean noEquivocal = false;
  private boolean adminPage = false;
  private boolean canLoadFile = false;
  private boolean fullLoad = false;
  private int lastQuestionSeq = 0;
  private int lastDeciderSeq = 0;
  
  // Az összes kérdés hány százalékának kell elfogynia ahhoz, hogy egy kérdés ismét előfordulhasson
  private static final double QUESTION_REPEAT_RATE = 0.3;
  private static final Logger LOGGER = LogManager.getLogger(GameManager.class.getName());

  public GameManager() {
    recentQuestions = new ArrayList<>();
    currentPlayers = new ArrayList<>();
    lastQuestion = null;

    LOGGER.trace("GameManager starting");
  }

  public void prevQuestion(){
    if (questionIndex > 1){
      setLastQuestionWinners();
      
      questionIndex--;
      lastQuestion = recentQuestions.get(questionIndex - 1);

      setWinnerPlayers(lastQuestion);
    }
  }

  public void newQuestion(){
    questionIndex = recentQuestions.size();
    nextQuestion();
  }

  public void setLastQuestionWinners(){
    if (lastQuestion != null){
      for (Player p : currentPlayers) {
        if (p.isWinner()){
          lastQuestion.getWinners().add(p);
        } else {
          lastQuestion.getWinners().remove(p);
        }
      }
    }  
  }

  public void setWinnerPlayers(Question question){
    for (Player p : currentPlayers) {
      if (question != null)
        p.setWinner(question.getWinners().contains(p));
      else
        p.setWinner(false);
    }
  } 
  
  public void nextQuestion(){

    setLastQuestionWinners();
    
    if (questionIndex < recentQuestions.size()){

      questionIndex++;
      lastQuestion = recentQuestions.get(questionIndex - 1);

      setWinnerPlayers(lastQuestion);
      
    } else {
    
      LOGGER.debug("questionIndex: " + questionIndex);

      setWinnerPlayers(null);
      
      List<Question> availQuestions = new ArrayList<>(questionManager.getActiveQuestions(noEquivocal, addIndiscreet));
      int allSize = availQuestions.size();

      for (Question question : recentQuestions) {
        availQuestions.removeIf(b -> b.getId() == question.getId());
      }

      availQuestions.removeAll(recentQuestions);
      LOGGER.debug("Összes: " + allSize + ", megkérdezve: " + recentQuestions.size() + ", elérhető: " + availQuestions.size());

      Map<Integer, Integer> wQMap = new TreeMap<>();
      for (int qIndex = 0; qIndex < availQuestions.size(); qIndex++) {
        int mapSize = wQMap.size();
        Question q = availQuestions.get(qIndex);
        for (int i = 0; i < q.getWeight(); i++) {
          wQMap.put(mapSize + i, qIndex);
        }
      }
      
      Random rnd = new Random();
      int rndIndex = rnd.nextInt(wQMap.size());
      Question rndQuestion = availQuestions.get(wQMap.get(rndIndex));
      try {
        lastQuestion = (Question) rndQuestion.clone();
      } catch (CloneNotSupportedException ex) {
        LOGGER.error(ex.toString());
      }
      LOGGER.debug(lastQuestion.getText()); 
      
      String newText;
      int brStart = lastQuestion.getText().indexOf("[");
      if (brStart >= 0){
         int brEnd = lastQuestion.getText().indexOf("]");
         if (brEnd > brStart + 1){
           String[] items = lastQuestion.getText().substring(brStart + 1, brEnd).split("\\|");

           rndIndex = rnd.nextInt(items.length);
           newText = lastQuestion.getText().substring(0, brStart) + items[rndIndex] + lastQuestion.getText().substring(brEnd + 1);
           LOGGER.debug("Új kérdés: " + newText);
           lastQuestion.setText(newText);
         }
      }  

      lastQuestion.setSeq(++lastQuestionSeq);
      lastQuestion.setPlayers(currentPlayers);
      lastQuestion.setWinners(new HashSet<>());
      recentQuestions.add(lastQuestion);
      questionIndex++;
      
      LOGGER.debug("Ismétlődési határ: " + ((int)(QUESTION_REPEAT_RATE * allSize)));
      if (recentQuestions.size() > (int)(QUESTION_REPEAT_RATE * allSize)){
        int qIndex = 0;
        do {
          if (recentQuestions.get(qIndex).isUnequivocal() || !noEquivocal){
            LOGGER.debug("Kérdés ismét jöhet: " + recentQuestions.get(qIndex).getText());
            recentQuestions.remove(qIndex);
            questionIndex--;
            qIndex = -1;
          } else {
            qIndex++;
          }
        } while (qIndex >= 0 && qIndex < recentQuestions.size());
      }
    }  
  };
  
  public void updateQuestionsFromFile(){
    LOGGER.debug("Fájl feltöltése előtt.....");
    List <Question> questions = questionManager.readQuestionsFromFile(file, false);
    LOGGER.debug("Beolvasott kérdések száma:" + questions.size());
    canLoadFile = false;
    questionManager.saveQuestions(questions, !fullLoad);
  }

  public void addPlayer(String color){
    int playerId = currentPlayers.size() + 1;
    Player player = new Player(playerId, "", color);
    currentPlayers.add(player);
    LOGGER.trace("New player: " + player.getSeq() + " - " + player.getColor());
    
    // Fókusz az új sor input mezőjére
    PrimeFaces.current().executeScript("setTimeout(function() { document.getElementById('pageform:dt_currentplayers:" + (currentPlayers.size() - 1) + ":it_pname').focus(); }, 100);");
  }

  public void addPlayer(){
    int playerId = currentPlayers.size() + 1;
    Player player = new Player(playerId, "", "");
    currentPlayers.add(player);
  }

  public void removePlayer(Player player){
    for (Player p : currentPlayers) {
      if (p.getSeq() > player.getSeq())
        p.setSeq(p.getSeq() - 1);
    }
    currentPlayers.remove(player);
  }

  public String getPlayerPointsText (Player player){
    int points = getPlayerPoints(player);
    if (points == 0)
      return "";
    return points + " pont";
  }
  
  public int getPlayerPoints (Player player){
    int points = 0;

    if (lastQuestion != null){
      for (int i = recentQuestions.size() - 1; i >= 0; i--) {
        Question q = recentQuestions.get(i);
        if (q != lastQuestion){
          if (q.getWinners().contains(player)){
            points++;
          }  
        }
      }
      
      if (player.isWinner())
        points++;
    }

    return points;
  }
  
  public boolean hasPlayers(){
    return !currentPlayers.isEmpty();
  }
  
  public QuestionManager getQuestionManager() {
    return questionManager;
  }

  public int getQuestionNumber(){
    return recentQuestions.size();
  }

  public void clearQuestions(){
    recentQuestions.clear();
  }

  public String getLastQuestionText(){
    if (lastQuestion == null)
      return "";
      
    if (!(lastQuestion.getExplanation().isEmpty()))
      return lastQuestion.getText().toUpperCase() + "\u00A0" + "*";
    else 
      return lastQuestion.getText().toUpperCase();
  }

  public String getLastQuestionClass(){
    if (lastQuestion != null){
      int len = lastQuestion.getText().length();
      if (len > 70)
        return "questiontextsmall";
      if (len > 50)
        return "questiontextmedium";
    }
    return "questiontext";
  }

  public String getLastQuestionExplanation(){
    if (lastQuestion == null)
      return "";

    String explanation = lastQuestion.getExplanation().toUpperCase();
    if (!explanation.isEmpty())
      explanation = "* " + explanation;
    return explanation;
  }

  public Question getLastQuestion() {
    return lastQuestion;
  }

  public void setLastQuestion(Question lastQuestion) {
    this.lastQuestion = lastQuestion;
  }
  
  public void setQuestionManager(QuestionManager questionManager) {
    this.questionManager = questionManager;
  }

  public List<Question> getRecentQuestions() {
    return recentQuestions;
  }

  public List<Question> getRecentQuestionsReverse(boolean skipLast) {
    List<Question> reverseQuestions = new ArrayList<>(recentQuestions);
    if (reverseQuestions.size() > 0){
      if (skipLast)
        reverseQuestions.remove(reverseQuestions.size() - 1);
      else if (lastQuestion != null) {
        setLastQuestionWinners();
      }  
    }
    
    Collections.reverse(reverseQuestions);
    
    return reverseQuestions;
  }

  public void setRecentQuestions(List<Question> recentQuestions) {
    this.recentQuestions = recentQuestions;
  }

  public int getQuestionIndex() {
    return questionIndex;
  }

  public void setQuestionIndex(int questionIndex) {
    this.questionIndex = questionIndex;
    if (questionIndex == 0) {
      if (lastQuestion != null){

        setLastQuestionWinners();

        lastQuestion.setDecider(true);
        lastDeciderSeq = lastQuestion.getSeq();
        lastQuestion = null;
        
        setWinnerPlayers(null);
        
        List<Player> clonedPlayers = new ArrayList<>();
        try {
          for (Player p : currentPlayers) {
            clonedPlayers.add((Player)p.clone());
          }
        } catch (CloneNotSupportedException ex) {
          LOGGER.error(ex.toString());
        }
        currentPlayers = clonedPlayers;

      }          
    }  
    else {
      setLastQuestionWinners();
      lastQuestion = recentQuestions.get(questionIndex - 1);
      setWinnerPlayers(lastQuestion);
    }  
  }

  public boolean isAddIndiscreet() {
    return addIndiscreet;
  }

  public void setAddIndiscreet(boolean addIndiscreet) {
    this.addIndiscreet = addIndiscreet;
  }

  public boolean isNoEquivocal() {
    return noEquivocal;
  }

  public void setNoEquivocal(boolean noEquivocal) {
    this.noEquivocal = noEquivocal;
  }

  public boolean isAdminPage() {
    return adminPage;
  }

  public void setAdminPage(boolean adminPage) {
    this.adminPage = adminPage;
  }

  public Part getFile() {
    return file;
  }

  public void setFile(Part file) {
    this.file = file;
  }

  public UploadedFile getInputfile() {
    return inputfile;
  }

  public void setInputfile(UploadedFile inputfile) {
    this.inputfile = inputfile;
  }

  public boolean isCanLoadFile() {
    return canLoadFile;
  }

  public void setCanLoadFile(boolean canLoadFile) {
    this.canLoadFile = canLoadFile;
  }

  public boolean isFullLoad() {
    return fullLoad;
  }

  public void setFullLoad(boolean fullLoad) {
    this.fullLoad = fullLoad;
  }

  public String getBgImage() {
    Category category = null;
    if (lastQuestion != null)
      category = lastQuestion.getCategory();

    String bgImage = BgImageManager.getBgImageFileName(category);
    LOGGER.trace("bgImage = " + bgImage);
    return bgImage;
  }

  public int getLastQuestionSeq() {
    return lastQuestionSeq;
  }

  public int getLastDeciderSeq() {
    return lastDeciderSeq;
  }

  public List<Player> getCurrentPlayers() {
    return currentPlayers;
  }

  public void setCurrentPlayers(List<Player> currentPlayers) {
    this.currentPlayers = currentPlayers;
  }

}

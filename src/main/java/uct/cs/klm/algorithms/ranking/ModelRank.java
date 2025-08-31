package uct.cs.klm.algorithms.ranking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.utils.ReasonerUtils;

/**
 * This class represents a ranked knowledge base.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelRank implements Cloneable
{
  // the rank number (index + 1) within the ranking
  private int _rankNumber;
  
  /** Represents rank formulas. */
  private KnowledgeBase _formulas;

  /**
   * Creates a new (empty) rank 0.
   */
  public ModelRank() {
    this(0, new KnowledgeBase());
  }
  
   public ModelRank(int rankNumber) {
    this(rankNumber, new KnowledgeBase());
  }
   
    public ModelRank(int rankNumber, KnowledgeBase formulas) {
    _formulas = new KnowledgeBase(formulas);
    _rankNumber = rankNumber;
  }

  /**
   * Creates a new rank given a rank number and a set of formulas.
   * 
   * @param rankNumber Rank number.
   * @param formulas   A set of formulas.
   */
  public ModelRank(int rankNumber, Collection<? extends PlFormula> formulas) {
    _formulas = new KnowledgeBase(formulas);
    _rankNumber = rankNumber;
  }

  /**
   * Create a rank (copy) from a given rank.
   * 
   * @param rank Ranked knowledge base.
   */
  public ModelRank(ModelRank rank) {
    _formulas = new KnowledgeBase(rank.getFormulas());
    _rankNumber = rank._rankNumber;
  }
  
   @Override
    public ModelRank clone() {
      try {
          return (ModelRank) super.clone();
      } catch (CloneNotSupportedException ex) {
          Logger.getLogger(ModelRank.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      return null;
    }
  
  public void addFormula(PlFormula formular) {
    _formulas.add(formular);
  }
  
    public void addFormulas(KnowledgeBase knowledgeBase) {
        if (knowledgeBase == null || knowledgeBase.isEmpty()) {
            return;
        }

        for (PlFormula formula : knowledgeBase) {
            addFormula(formula);
        }
    }
  
  public boolean isEmpty() {
    return _formulas.isEmpty();
  }
  
   public void removeFormulas(KnowledgeBase knowledgeBase) {
       
       if(_formulas.isEmpty() || knowledgeBase == null || knowledgeBase.isEmpty())
       {
           return;
       }
       
       for(PlFormula formula: knowledgeBase)
       {          
           removeFormula(formula);         
       }           
   }
   
  public boolean removeFormula(PlFormula formula) {
      
      if(_formulas.isEmpty())
       {
           return true;
       }
      
      if(_formulas.remove(formula))
      {
          return true;
      }
      
      if(_formulas.remove(ReasonerUtils.toMaterialisedFormula(formula)))
      {
          return true;
      }
      
      return _formulas.remove(ReasonerUtils.toDematerialisedFormula(formula));
  }
  

  /**
   * Get the rank number.
   * 
   * @return ModelRank number.
   */
  public int getRankNumber() {
    return _rankNumber;
  }

  /**
   * Set the rank number.
   * 
   * @param rankNumber ModelRank number.
   */
  public void setRankNumber(int rankNumber) {
    this._rankNumber = rankNumber;
  }

  /**
   * Get formulas from this rank as a .
     * @return 
   */
  public KnowledgeBase getFormulas() {
    return _formulas;
  }
  
}

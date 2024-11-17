package uct.cs.klm.algorithms.models;

import java.util.Collection;

import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class represents a defeasible knowledge base of propositional formulae.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za
 */

public class KnowledgeBase extends PlBeliefSet 
{
  /**
   * Creates new (empty) knowledge base.
   */
  public KnowledgeBase() 
  {
    super();
  }

  /**
   * Creates a new knowledge base with the given set of formulas.
   * 
   * @param formulas A set of formulas.
   */
  public KnowledgeBase(Collection<? extends PlFormula> formulas) 
  {
    super(formulas);
  }

  /**
   * Computes the union of this knowledge base and other knowledge base.
   * 
   * @param knowledgeBase Other knowledge base.
   * @return Knowledge base representing the union.
   */
  public KnowledgeBase union(KnowledgeBase knowledgeBase) 
  {
    KnowledgeBase result = new KnowledgeBase(this);
    result.addAll(knowledgeBase);
    return result;
  }

  /**
   * Computes union of this knowledge base with a collection of other knowledge
   * bases.
   * 
   * @param knowledgeBases Set of knowledge bases.
   * @return Knowledge base representing the union.
   */
  public KnowledgeBase union(
          Collection<KnowledgeBase> knowledgeBases) 
  {
    KnowledgeBase result = new KnowledgeBase();
    
    knowledgeBases.forEach(kb -> 
    {
      result.addAll(kb);
    });
    
    return result;
  }

  /**
   * Computes the intersection of this knowledge base and other knowledge
   * base.
   * 
   * @param knowledgeBase Other knowledge base.
   * @return Knowledge base representing the intersection.
   */
  public KnowledgeBase intersection(
          KnowledgeBase knowledgeBase) 
  {
    KnowledgeBase result = new KnowledgeBase();
    
    this.forEach(formula -> 
    {
      if (knowledgeBase.contains(formula)) 
      {
        result.add(formula);
      }
    });
    
    return result;
  }

  /**
   * Computes a set difference of this knowledge base and other knowledge base.
   * 
   * @param knowledgeBase other knowledge base.
   * @return Knowledge base representing the set difference.
   */
  public KnowledgeBase difference(KnowledgeBase knowledgeBase) 
  {
    KnowledgeBase result = new KnowledgeBase(this);
    result.removeAll(knowledgeBase);
    return result;
  }

  /**
   * Retrevies the antecedants of statements with implication.
   * 
   * @return Knowledge base representing the antecedants.
   */
  public KnowledgeBase antecedents() {
    KnowledgeBase antecedents = new KnowledgeBase();
    this.forEach(formula -> {
      if (formula instanceof Implication implication) {
        antecedents.add(implication.getFirstFormula());
      }
    });
    return antecedents;
  }

  /**
   * Convert the defeasible implication statements to classical implication.
   * 
   * @return Materialisation of this knowledge base.
   */
  public KnowledgeBase materialise() {
    KnowledgeBase result = new KnowledgeBase();
    this.forEach(formula -> {
      if (formula instanceof DefeasibleImplication defeasibleImplication) {
        result.add(new Implication(defeasibleImplication.getFormulas()));
      }
    });
    return result;
  }

  /**
   * Convert the classical implication statements to defeasible implication.
   * 
   * @return Dematerialisation of this knowledge base.
   */
  public KnowledgeBase dematerialise() {
    KnowledgeBase result = new KnowledgeBase();
    this.forEach(formula -> {
      if ((formula instanceof Implication implication) && !(formula instanceof DefeasibleImplication)) {
        result.add(new DefeasibleImplication(implication.getFormulas()));
      }
    });
    return result;
  }

  /**
   * Separates the knowledge base into defeasbile and classical statemetents.
   * 
   * @return KnowledgeBase array where index 0 is defeasible knowledge base and
   *         index 1 is classical knowledge base.
   */

  public KnowledgeBase[] separate() {
    KnowledgeBase defeasible = new KnowledgeBase();
    KnowledgeBase classical = new KnowledgeBase();
    this.forEach(formula -> {
      if (formula instanceof DefeasibleImplication) {
        defeasible.add(formula);
      } else {
        classical.add(formula);
      }
    });
    return new KnowledgeBase[] { defeasible, classical };
  }
  
  public KnowledgeBase remove(PlFormula formula) 
  {
    KnowledgeBase result = new KnowledgeBase(this);
    result.formulas.remove(formula);
    return result;
  }

  /**
   * Convert defeasible implication to classical implication.
   * 
   * @param formula Classical implication formula
   * @return Defeasible implication.
   */
  public static PlFormula materialise(PlFormula formula) {
    if (formula instanceof DefeasibleImplication defeasibleImplication) {
      return new Implication(defeasibleImplication.getFormulas());
    }
    return formula;
  }

  /**
   * Convert classical implication to defeasible implication.
   * 
   * @param formula Defeasible implication formula.
   * @return Classical implication.
   */
  public static PlFormula dematerialise(PlFormula formula) {
    if (formula instanceof Implication implication) {
      return new DefeasibleImplication(implication.getFormulas());
    }
    return formula;
  }
}
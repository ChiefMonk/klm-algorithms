package uct.cs.klm.algorithms.models;

import java.util.Collection;

import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class represents a ranked knowledge base.
 * 
 * @author Thabo Vincent Moloi
 */
public class Rank {
  /** Represents the rank number. */
  private int rankNumber;
  /** Represents rank formulas. */
  private KnowledgeBase formulas;

  /**
   * Creates a new (empty) rank 0.
   */
  public Rank() {
    this(0, new KnowledgeBase());
  }

  /**
   * Creates a new rank given a rank number and a set of formulas.
   * 
   * @param rankNumber Rank number.
   * @param formulas   A set of formulas.
   */
  public Rank(int rankNumber, Collection<? extends PlFormula> formulas) {
    this.formulas = new KnowledgeBase(formulas);
    this.rankNumber = rankNumber;
  }

  /**
   * Create a rank (copy) from a given rank.
   * 
   * @param rank Ranked knowledge base.
   */
  public Rank(Rank rank) {
    this.formulas = new KnowledgeBase(rank.formulas);
    this.rankNumber = rank.rankNumber;
  }

  /**
   * Get the rank number.
   * 
   * @return Rank number.
   */
  public int getRankNumber() {
    return this.rankNumber;
  }

  /**
   * Set the rank number.
   * 
   * @param rankNumber Rank number.
   */
  public void setRankNumber(int rankNumber) {
    this.rankNumber = rankNumber;
  }

  /**
   * Get formulas from this rank as a .
   */
  public KnowledgeBase getFormulas() {
    return formulas;
  }
}

package uct.cs.klm.algorithms.models;

import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.utils.Symbols;

/**
 * This class models defeasible implication of propositional logic.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public class DefeasibleImplication extends Implication {

  /**
   * Creates a new implication a~>b with the two given formulas
   * 
   * @param a A propositional formula.
   * @param b A propositional formula.
   */
  public DefeasibleImplication(PlFormula a, PlFormula b) {
    super(a, b);
  }

  /**
   * Creates new defeasible implication with given pair of formulas
   * 
   * @param formulas A pair of formulas.
   */
  public DefeasibleImplication(Pair<PlFormula, PlFormula> formulas) {
    super(formulas);
  }

  @Override
  public String toString() {
    return super.toString().replaceAll(Symbols.IMPLICATION, Symbols.DEFEASIBLE_IMPLICATION);
  }

}

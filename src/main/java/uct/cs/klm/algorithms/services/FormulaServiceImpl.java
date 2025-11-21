package uct.cs.klm.algorithms.services;

import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

import uct.cs.klm.algorithms.models.DefeasibleImplication;

/**
 * This class represents a formula service implementation for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

public class FormulaServiceImpl implements FormulaService {

  private PlFormula getDefault() {
    Proposition p = new Proposition("p");
    Proposition w = new Proposition("w");
    return new DefeasibleImplication(p, w);
  }

  @Override
  public PlFormula getQueryFormula() {
    return getDefault();
  }
}

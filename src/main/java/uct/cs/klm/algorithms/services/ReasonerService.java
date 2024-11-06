package uct.cs.klm.algorithms.services;

import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.models.BaseRank;
import uct.cs.klm.algorithms.models.Entailment;

public interface ReasonerService {
  public Entailment getEntailment(BaseRank baseRank, PlFormula queryFormula);
}

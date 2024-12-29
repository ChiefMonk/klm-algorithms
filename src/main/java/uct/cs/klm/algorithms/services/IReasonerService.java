package uct.cs.klm.algorithms.services;

import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.models.ModelEntailment;

public interface IReasonerService {
  public ModelEntailment getEntailment(ModelBaseRank baseRank, PlFormula queryFormula);
}

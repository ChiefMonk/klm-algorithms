package uct.cs.klm.algorithms.services;

import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.models.Entailment;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;
import uct.cs.klm.algorithms.models.RationalEntailment;

public class RationalReasonerImpl implements IReasonerService {
  private final SatReasoner reasoner;

  public RationalReasonerImpl() {
    SatSolver.setDefaultSolver(new Sat4jSolver());
    reasoner = new SatReasoner();
  }

  @Override
  public Entailment getEntailment(ModelBaseRank baseRank, PlFormula queryFormula) {
    long startTime = System.nanoTime();
   
    PlFormula negation = new Negation(((Implication) queryFormula).getFirstFormula());
    KnowledgeBase knowledgeBase = baseRank.getKnowledgeBase();
    ModelRankCollection baseRanking = baseRank.getRanking();
    ModelRankCollection removedRanking = new ModelRankCollection();

    KnowledgeBase union = new KnowledgeBase();
    baseRanking.forEach(rank -> {
      union.addAll(rank.getFormulas());
    });

    int i = 0;
    while (!union.isEmpty() && reasoner.query(union, negation) && i < baseRanking.size() - 1) {
      removedRanking.add(baseRanking.get(i));
      union.removeAll(baseRanking.get(i).getFormulas());
      i++;
    }

    boolean entailed = !union.isEmpty() && reasoner.query(union, queryFormula);
    long endTime = System.nanoTime();

    return new RationalEntailment.RationalEntailmentBuilder()
        .withKnowledgeBase(knowledgeBase)
        .withQueryFormula(queryFormula)
        .withBaseRanking(baseRanking)
        .withRemovedRanking(removedRanking)
        .withEntailed(entailed)
        .withTimeTaken((endTime - startTime) / 1_000_000_000.0)
        .build();
  }

}

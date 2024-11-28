package uct.cs.klm.algorithms.services;

import java.util.Arrays;

import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.models.Entailment;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.models.LexicalEntailment;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;

public class LexicalReasonerImpl implements IReasonerService {
  private final SatReasoner reasoner;

  public LexicalReasonerImpl() {
    SatSolver.setDefaultSolver(new Sat4jSolver());
    reasoner = new SatReasoner();
  }

  @Override
  public Entailment getEntailment(ModelBaseRank baseRank, PlFormula queryFormula) {
    long startTime = System.nanoTime();

    // Get inputs
    PlFormula negation = new Negation(((Implication) queryFormula).getFirstFormula());
    KnowledgeBase knowledgeBase = baseRank.getKnowledgeBase();
    ModelRankCollection baseRanking = baseRank.getRanking();
    ModelRankCollection removedRanking = new ModelRankCollection();
    ModelRankCollection weakenedRanking = new ModelRankCollection();

    KnowledgeBase union = new KnowledgeBase();
    baseRanking.forEach(rank -> {
      union.addAll(rank.getFormulas());
    });

    int i = 0;
    while (!union.isEmpty() && reasoner.query(union, negation) && i < baseRanking.size() - 1) {
      union.removeAll(baseRanking.get(i).getFormulas());

      int m = baseRanking.get(i).getFormulas().size() - 1;
      KnowledgeBase weakenedRank = new KnowledgeBase(Arrays.asList(weakenRank(baseRanking.get(i), m)));
      weakenedRanking.add(new ModelRank(i, weakenedRank));
      while (reasoner.query(union.union(weakenedRank), negation) && m > 0) {
        m--;
        weakenedRank = new KnowledgeBase(Arrays.asList(weakenRank(baseRanking.get(i), m)));
        weakenedRanking.add(new ModelRank(i, weakenedRank));
      }

      if (m == 0) {
        removedRanking.add(baseRanking.get(i));
      }
      union.addAll(weakenedRank);
      i++;
    }

    boolean entailed = !union.isEmpty() && reasoner.query(union, queryFormula);
    long endTime = System.nanoTime();

    return new LexicalEntailment.LexicalEntailmentBuilder()
        .withKnowledgeBase(knowledgeBase)
        .withQueryFormula(queryFormula)
        .withBaseRanking(baseRanking)
        .withRemovedRanking(removedRanking)
        .withWeakenedRanking(weakenedRanking)
        .withEntailed(entailed)
        .withTimeTaken((endTime - startTime) / 1_000_000_000.0)
        .build();
  }

  private Disjunction weakenRank(ModelRank rank, int size) {
    int n = rank.getFormulas().size();
    Object[] rankArray = rank.getFormulas().toArray();
    Disjunction weakenedRank = new Disjunction();
    for (int bitmask = 0; bitmask < (1 << n); bitmask++) {
      if (Integer.bitCount(bitmask) == size) {
        Conjunction conjunction = new Conjunction();
        for (int i = 0; i < n; i++) {
          if ((bitmask & (1 << i)) != 0) {
            conjunction.add((PlFormula) rankArray[i]);
          }
        }
        weakenedRank.add(conjunction);
      }
    }
    return weakenedRank;
  }

}

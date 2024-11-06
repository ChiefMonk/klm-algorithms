package uct.cs.klm.algorithms.services;

import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;

import uct.cs.klm.algorithms.models.BaseRank;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.models.Rank;
import uct.cs.klm.algorithms.models.Ranking;

public class BaseRankServiceImpl implements BaseRankService {
  private final SatReasoner reasoner;

  public BaseRankServiceImpl() {
    SatSolver.setDefaultSolver(new Sat4jSolver());
    reasoner = new SatReasoner();
  }

  @Override
  public BaseRank constructBaseRank(KnowledgeBase knowledgeBase) {
    // Start time
    long startTime = System.nanoTime();

    // Separate defeasible and classical statements
    KnowledgeBase[] kb = knowledgeBase.separate();
    KnowledgeBase defeasible = kb[0];
    KnowledgeBase classical = kb[1];

    // ranking and exceptionality sequence
    Ranking ranking = new Ranking();
    Ranking sequence = new Ranking();

    KnowledgeBase current = defeasible;
    KnowledgeBase previous = new KnowledgeBase();

    int i = 0;
    while (!previous.equals(current)) {
      previous = current;
      current = new KnowledgeBase();

      KnowledgeBase exceptionals = getExceptionals(previous, classical);

      Rank rank = new Rank();
      constructRank(rank, previous, current, exceptionals);

      if (!rank.getFormulas().isEmpty()) {
        rank.setRankNumber(i);
        ranking.add(rank);
      }
      sequence.addRank(previous.equals(current) ? Integer.MAX_VALUE : i, previous);
      i++;
    }
    ranking.addRank(Integer.MAX_VALUE, classical.union(current));

    long endTime = System.nanoTime();
    return new BaseRank(knowledgeBase, sequence, ranking, (endTime - startTime) / 1_000_000_000.0);
  }

  private KnowledgeBase getExceptionals(KnowledgeBase defeasible, KnowledgeBase classical) {
    KnowledgeBase exceptionals = new KnowledgeBase();
    KnowledgeBase union = defeasible.union(classical);
    defeasible.antecedents().parallelStream().forEach(antecedent -> {
      if (reasoner.query(union, new Negation(antecedent))) {
        synchronized (exceptionals) {
          exceptionals.add(antecedent);
        }
      }
    });
    return exceptionals;
  }

  private void constructRank(Rank rank, KnowledgeBase previous, KnowledgeBase current,
      KnowledgeBase exceptionals) {
    previous.parallelStream().forEach(formula -> {
      if (exceptionals.contains(((Implication) formula).getFormulas().getFirst())) {
        synchronized (current) {
          current.add(formula);
        }
      } else {
        synchronized (rank.getFormulas()) {
          rank.getFormulas().add(formula);
        }
      }
    });
  }
}

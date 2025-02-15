package uct.cs.klm.algorithms.ranking;

import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Tautology;

import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.utils.ReasonerUtils;
import uct.cs.klm.algorithms.utils.Symbols;

/**
 *
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 */
public final class BaseRankService implements IBaseRankService {

    private final SatReasoner _satReasoner;

    public BaseRankService() {
        SatSolver.setDefaultSolver(new Sat4jSolver());
        _satReasoner = new SatReasoner();
    }
    
  
    @Override
    public ModelBaseRank construct(KnowledgeBase knowledgeBase) {
        
        System.out.println("==>BaseRank Algorithm");
        // Start time
        long startTime = System.nanoTime();
              
        // Separate defeasible and classical statements       
        KnowledgeBase defeasibleStatements = knowledgeBase.getDefeasibleFormulas();
        KnowledgeBase classicalStatements = knowledgeBase.getClassicalFormulas();
             
        System.out.println(String.format("Defeasibles := %s", defeasibleStatements.toString()));
        System.out.println(String.format("Classical := %s", classicalStatements.toString()));
        System.out.println();
        
        // ranking and exceptionality sequence
        ModelRankCollection ranking = new ModelRankCollection();
        ModelRankCollection sequence = new ModelRankCollection();

        KnowledgeBase current = defeasibleStatements;
        KnowledgeBase previous = new KnowledgeBase();

        int i = 0;
        while (!previous.equals(current)) {
            previous = current;
            current = new KnowledgeBase();
            
            System.out.println(String.format("Previous := %s", previous)); 
            System.out.println(String.format("Current := %s", current)); 

            KnowledgeBase exceptionals = getExceptionalStatements(previous, classicalStatements);

            ModelRank rank = new ModelRank();
            constructRank(rank, previous, current, exceptionals);

            if (!rank.getFormulas().isEmpty()) {
                rank.setRankNumber(i);
                ranking.add(rank);
            }
            sequence.addRank(previous.equals(current) ? Symbols.INFINITY_RANK_NUMBER : i, previous);
            i++;
        }
        ranking.addRank(
                Symbols.INFINITY_RANK_NUMBER,  
                ReasonerUtils.toCombinedKnowledgeBases(classicalStatements, current));

        long endTime = System.nanoTime();
         return new ModelBaseRank(knowledgeBase, sequence, ranking, (endTime - startTime) / 1_000_000_000.0);
    }
   
    private KnowledgeBase getExceptionalStatements(
            KnowledgeBase defeasible,
            KnowledgeBase classical) {
        KnowledgeBase exceptionals = new KnowledgeBase();
        
        KnowledgeBase union = ReasonerUtils.toCombinedKnowledgeBases(defeasible, classical);
        
        defeasible.antecedents().parallelStream().forEach(antecedent -> {
            if (_satReasoner.query(union, new Negation(antecedent))) {
                synchronized (exceptionals) {
                    exceptionals.add(antecedent);
                }
            }
        });
        return exceptionals;
    }

    private void constructRank(
            ModelRank rank,
            KnowledgeBase previous,
            KnowledgeBase current,
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

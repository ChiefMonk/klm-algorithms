package uct.cs.klm.algorithms.ranking;

import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.models.KnowledgeBase;

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
    
    // @Override
    public ModelBaseRank construct2(KnowledgeBase knowledgeBase) {
        // Start time
        long startTime = System.nanoTime();

        ModelRankCollection rankCollection = new ModelRankCollection();
          
        // Separate defeasible and classical statements
       //KnowledgeBase[] kb = knowledgeBase.separate();
       //KnowledgeBase defeasibleStatements = kb[0];
       KnowledgeBase classicalStatements = new KnowledgeBase();
        
        KnowledgeBase currentMaterialisation = knowledgeBase;
        KnowledgeBase prevMaterialisation = new KnowledgeBase();
        
        int rankNumber = 0;
      
        while (!currentMaterialisation.equals(prevMaterialisation)) {
           
            prevMaterialisation = currentMaterialisation;
            currentMaterialisation = new KnowledgeBase();
            
            for (PlFormula f : prevMaterialisation) {
                System.out.println("PlFormula : " + f.toString());
                 
                if (f.toString().contains("=>")) {
                    if (_satReasoner.query(prevMaterialisation, new Negation(((Implication) f).getFormulas().getFirst()))) {
                        System.out.println("currentMaterialisation : " + f.toString());
                        currentMaterialisation.add(f);
                    }
                }
            }
            
            ModelRank newRank = new ModelRank();
            newRank.setRankNumber(rankNumber);
            
            for (PlFormula formular : prevMaterialisation) {
                
                if (!classicalStatements.contains(formular)) {
                    newRank.addFormula(formular);
                }
            }
            
            newRank.removeFormulas(currentMaterialisation);
            
            if (!newRank.isEmpty()) {                
                
                rankCollection.add(newRank);                
                System.out.println("Added rank " + Integer.toString(newRank.getRankNumber()));
                
            } else {
                classicalStatements.addAll(currentMaterialisation);
            }
            
            rankNumber++;
        }

        if(!classicalStatements.isEmpty())
        {
            rankCollection.add(new ModelRank(Integer.MAX_VALUE, classicalStatements));
        }
        
        System.out.println("Base Ranking of Knowledge Base:");
        
        for (ModelRank rank : rankCollection) {
            if (rank.getRankNumber() == Integer.MAX_VALUE) {
                System.out.println("Infinite Rank:" + rank.getFormulas().toString());
            } else {
                System.out.println("Rank " + rank.getRankNumber() + ":" + rank.getFormulas().toString());
            }
        }
       
        long endTime = System.nanoTime();
        return new ModelBaseRank(knowledgeBase, rankCollection, rankCollection, (endTime - startTime) / 1_000_000_000.0);
    }
   
    @Override
    public ModelBaseRank construct(KnowledgeBase knowledgeBase) {
        // Start time
        long startTime = System.nanoTime();

        // Separate defeasible and classical statements
        KnowledgeBase[] kb = knowledgeBase.separate();
        KnowledgeBase defeasible = kb[0];
        KnowledgeBase classical = kb[1];

        // ranking and exceptionality sequence
        ModelRankCollection ranking = new ModelRankCollection();
        ModelRankCollection sequence = new ModelRankCollection();

        KnowledgeBase current = defeasible;
        KnowledgeBase previous = new KnowledgeBase();

        int i = 0;
        while (!previous.equals(current)) {
            previous = current;
            current = new KnowledgeBase();

            KnowledgeBase exceptionals = getExceptionals(previous, classical);

            ModelRank rank = new ModelRank();
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
         return new ModelBaseRank(knowledgeBase, sequence, ranking, (endTime - startTime) / 1_000_000_000.0);
    }

    private KnowledgeBase getExceptionals(KnowledgeBase defeasible, KnowledgeBase classical) {
        KnowledgeBase exceptionals = new KnowledgeBase();
        KnowledgeBase union = defeasible.union(classical);
        defeasible.antecedents().parallelStream().forEach(antecedent -> {
            if (_satReasoner.query(union, new Negation(antecedent))) {
                synchronized (exceptionals) {
                    exceptionals.add(antecedent);
                }
            }
        });
        return exceptionals;
    }

    private void constructRank(ModelRank rank, KnowledgeBase previous, KnowledgeBase current,
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

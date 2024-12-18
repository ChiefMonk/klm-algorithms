package uct.cs.klm.algorithms.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.models.Entailment;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;
import uct.cs.klm.algorithms.models.RationalEntailment;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.utils.*;

public class RationalReasonerImpl extends KlmReasonerBase implements IReasonerService {

   public RationalReasonerImpl() {
    super();
  }

    @Override
    public Entailment getEntailment(ModelBaseRank baseRank, PlFormula queryFormula) {
        long startTime = System.nanoTime();
             
        System.out.println();
        System.out.println("==> Rational Closure Entailment");
        System.out.println(String.format("Query: %s", queryFormula));
         
        ArrayList<ModelRank> originalBaseRanking = (ArrayList<ModelRank>) baseRank.getRanking().stream()
        .sorted(Comparator.comparing(ModelRank::getRankNumber))
        .collect(Collectors.toList());
        
        ModelRankCollection baseRanking = new ModelRankCollection(originalBaseRanking);
        
        System.out.println("Ranking");             
        for(ModelRank rank : baseRanking)
        {
            System.out.println(String.format("%s:%s", rank.getRankNumber(), rank.getFormulas()));
        }
                       
        KnowledgeBase materialisedKnowledgeBase;      
        PlFormula negationOfAntecedent = new Negation(((Implication) queryFormula).getFirstFormula());
               
        ModelRankCollection removedRanking = new ModelRankCollection();             
             
        while (!baseRanking.isEmpty()) {
          
            materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(baseRanking);
              
            ModelRank currentRank = null;

            if (!baseRanking.isEmpty()) {
                currentRank = baseRanking.get(0);
            }
            
            if (currentRank == null) {
                break;
            }

           
            System.out.println();
            System.out.println(String.format("Processing Rank = %s:%s", currentRank.getRankNumber(), currentRank.getFormulas()));
            System.out.println(String.format("Checking if %s is entailed by %s",negationOfAntecedent, materialisedKnowledgeBase));               

            boolean isEntailed = _reasoner.query(materialisedKnowledgeBase, negationOfAntecedent);

            if (isEntailed) {
               
                System.out.println(String.format("YES: so we remove rank %s:%s", currentRank.getRankNumber(), currentRank.getFormulas()));             
                baseRanking.remove(0);                    
                removedRanking.add(currentRank);
                
            } else {
                
                System.out.println("NO: So we stop processing=" + materialisedKnowledgeBase);
                break;
                
            }
                                 
        }
        
        System.out.println();
        boolean isQueryEntailed = !baseRanking.isEmpty() && _reasoner.query(ReasonerUtils.toMaterialisedKnowledgeBase(baseRanking), ReasonerUtils.toMaterialisedFormula(queryFormula));
        KnowledgeBase entailmentKb = new KnowledgeBase();   
             
        if(isQueryEntailed)
        {
            entailmentKb = ReasonerUtils.toKnowledgeBase(baseRanking);
            System.out.println(String.format("RC Checking if %s entails %s = TRUE", ReasonerUtils.toKnowledgeBase(baseRanking), queryFormula));
        }
        else
        {
             System.out.println(String.format("RC Checking if %s entails %s = FALSE", ReasonerUtils.toKnowledgeBase(baseRanking), queryFormula));
        }
               
        var remainingRanking = ReasonerUtils.toRemainingEntailmentRanks(originalBaseRanking, removedRanking);
        System.out.println("Remaining Ranking:");    
        for(ModelRank rank : remainingRanking)
        {
               System.out.println(String.format("%s:%s", rank.getRankNumber(), rank.getFormulas()));
        }
                                  
        long endTime = System.nanoTime();

        return new RationalEntailment.RationalEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
                .withRemovedRanking(removedRanking)
                .withRemainingRanking(remainingRanking)
                .withEntailmentKnowledgeBase(entailmentKb)
                .withEntailed(isQueryEntailed)
                .withTimeTaken((endTime - startTime) / 1_000_000_000.0)
                .build();
    }

    public Entailment getEntailment2(ModelBaseRank baseRank, PlFormula queryFormula) {
        long startTime = System.nanoTime();

        ModelRankCollection baseRanking = baseRank.getRanking();
        PlFormula negation = new Negation(((Implication) queryFormula).getFirstFormula());
        KnowledgeBase knowledgeBase = baseRank.getKnowledgeBase();

        System.out.println("==>RationalReasoner");
        System.out.println(String.format("BaseRanking:\n%s", baseRank.toString()));
        System.out.println(String.format("KnowledgeBase: %s", knowledgeBase));
        System.out.println(String.format("Query: %s : %s", queryFormula, negation));

        ModelRankCollection removedRanking = new ModelRankCollection();

        KnowledgeBase union = new KnowledgeBase();

        baseRanking.forEach(rank -> {
            union.addAll(rank.getFormulas());
        });

        int i = 0;
        while (!union.isEmpty() && _reasoner.query(union, negation) && i < baseRanking.size() - 1) {
            removedRanking.add(baseRanking.get(i));
            union.removeAll(baseRanking.get(i).getFormulas());
            i++;
        }

        boolean entailed = !union.isEmpty() && _reasoner.query(union, queryFormula);
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

package uct.cs.klm.algorithms.rational;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.ranking.*;
import uct.cs.klm.algorithms.models.*;
import uct.cs.klm.algorithms.services.*;
import uct.cs.klm.algorithms.utils.*;

public class RationalReasonerImpl extends KlmReasonerBase implements IReasonerService {

    public RationalReasonerImpl() {
        super();
    }

    @Override
    public ModelEntailment getEntailment(
            ModelBaseRank baseRank,
            PlFormula queryFormula) {

        long startTime = System.nanoTime();
       
        System.out.println("==>Rational Closure Entailment");
        
         // get the negation of the antecedent of the query
        PlFormula negationOfAntecedent = new Negation(((Implication) queryFormula).getFirstFormula());
        
        ModelRankCollection baseRanking = baseRank.getRanking();
        Collections.sort(baseRanking, (o1, o2) -> Integer.compare(o1.getRankNumber(), o2.getRankNumber()));
         
        System.out.println("->BaseRank");             
        for(ModelRank rank : baseRanking)
        {
            System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }  
        
        System.out.println(String.format("->Query, α: %s", queryFormula));
        System.out.println(String.format("->Negation of α antecedent: %s", negationOfAntecedent));       
                    
        ModelRankCollection removedRanking = new ModelRankCollection();        
        KnowledgeBase materialisedKnowledgeBase;               

        int stepNumber = 1;
        while (!baseRanking.isEmpty()) {
            
            materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(baseRanking);
            
            ModelRank currentRank = baseRanking.get(0);
             
            System.out.println();           
            System.out.println(String.format("->Checking Entailment Step %s", stepNumber));   
             
            // if baseRank has only the infinity rank, then we stop processing
            if(currentRank.getRankNumber() == Symbols.INFINITY_RANK_NUMBER)
            {
                System.out.println("ONLY ∞ rank remains, so we stop processing with current K = " + materialisedKnowledgeBase);
                break; 
            }
                                                       
            System.out.println("  Current BaseRank");             
            for(ModelRank rank : baseRanking)
            {
                System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
            } 
            
            System.out.println(String.format("  Current KB: %s", materialisedKnowledgeBase));
            System.out.println(String.format("  Checking if %s is entailed by current KB", negationOfAntecedent));
           
            boolean isEntailed = _reasoner.query(materialisedKnowledgeBase, negationOfAntecedent);
            
            if(!isEntailed)
            {
                System.out.println("  NO its not, so we stop processing with current K = " + materialisedKnowledgeBase);
                break; 
            }
                                 
            System.out.println(String.format("  YES it is: so we remove rank %s: %s", currentRank.getRankNumber(), currentRank.getFormulas()));
            removedRanking.add(currentRank);    
            baseRanking.remove(0);  
            
            stepNumber++;
        }
        
        Collections.sort(baseRanking, (o1, o2) -> Integer.compare(o2.getRankNumber(), o1.getRankNumber()));

        System.out.println();
        System.out.println("->Remaining Ranks:");
        for (ModelRank rank : baseRanking) {
            System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

        var materialisedQueryFormula = ReasonerUtils.toMaterialisedFormula(queryFormula);
        materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(baseRanking);
        
        boolean isQueryEntailed = !baseRanking.isEmpty() && _reasoner.query(materialisedKnowledgeBase, materialisedQueryFormula);
        KnowledgeBase entailmentKb = new KnowledgeBase();
        
        var hasEntailed = "NO";
        if (isQueryEntailed) {
            entailmentKb = ReasonerUtils.toKnowledgeBase(baseRanking);
            hasEntailed = "YES";           
        }
        
        System.out.println(String.format("Finally checking if %s is entailed by %s", materialisedQueryFormula, materialisedKnowledgeBase));
        System.out.println(String.format("Is Entailed: %s", hasEntailed));       
            
        long endTime = System.nanoTime();

        return new ModelRationalClosureEntailment.RationalEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
                .withRemovedRanking(removedRanking)
                .withRemainingRanking(baseRanking)
                .withEntailmentKnowledgeBase(entailmentKb)
                .withEntailed(isQueryEntailed)
                .withTimeTaken((endTime - startTime) / 1_000_000_000.0)
                .build();
    }

    public ModelEntailment getEntailment2(ModelBaseRank baseRank, PlFormula queryFormula) {
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

        return new ModelRationalClosureEntailment.RationalEntailmentBuilder()
                .withKnowledgeBase(knowledgeBase)
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRanking)
                .withRemovedRanking(removedRanking)
                .withEntailed(entailed)
                .withTimeTaken((endTime - startTime) / 1_000_000_000.0)
                .build();
    }
}

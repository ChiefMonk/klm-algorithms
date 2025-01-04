package uct.cs.klm.algorithms.rational;

import java.util.Collections;

import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.models.ModelEntailment;
import uct.cs.klm.algorithms.models.ModelRationalClosureEntailment;
import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;
import uct.cs.klm.algorithms.services.IReasonerService;
import uct.cs.klm.algorithms.services.KlmReasonerBase;
import uct.cs.klm.algorithms.utils.DisplayUtils;
import uct.cs.klm.algorithms.utils.ReasonerUtils;
import uct.cs.klm.algorithms.utils.Symbols;

public class RationalReasonerImpl extends KlmReasonerBase implements IReasonerService {

    public RationalReasonerImpl() {
        super();
    }
    
      //@Override
      public ModelEntailment getEntailment4(
            ModelBaseRank baseRank,
            PlFormula queryFormula) {

        long startTime = System.nanoTime();

       System.out.println("==>Rational Closure Entailment");

        long endTime = System.nanoTime();

        return new ModelRationalClosureEntailment.RationalEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
                .withMiniBaseRanking(new ModelRankCollection())
                .withRemovedRanking(new ModelRankCollection())
                .withRemainingRanking(baseRank.getRanking())               
                .withEntailmentKnowledgeBase(new KnowledgeBase())
                .withEntailed(false)
                .withTimeTaken((endTime - startTime) / 1_000_000_000.0)
                .build();
    }


    @Override
    public ModelEntailment getEntailment(
            ModelBaseRank baseRank,
            PlFormula queryFormula) {

        long startTime = System.nanoTime();

        System.out.println("==>Rational Closure Entailment");

        // get the negation of the antecedent of the query
        PlFormula negationOfAntecedent = new Negation(((Implication) queryFormula).getFirstFormula());

        ModelRankCollection baseRankCollection = new ModelRankCollection(baseRank.getRanking());

        Collections.sort(baseRankCollection, (o1, o2) -> Integer.compare(o1.getRankNumber(), o2.getRankNumber()));

        System.out.println("->BaseRank");
        for (ModelRank rank : baseRankCollection) {
            System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

        System.out.println(String.format("->Query, α: %s", queryFormula));
        System.out.println(String.format("->Antecedent Negation of α: %s", negationOfAntecedent));

        ModelRankCollection removedRanking = new ModelRankCollection(); 
        
        int stepNumber = 1;
        while (true) {
            
            if(baseRankCollection.isEmpty())
            {
                break;
            }
            
            var materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection);

            var currentRank = baseRankCollection.get(0);

            System.out.println();
            System.out.println(String.format("->Checking Entailment Step %s", stepNumber++));

            // if baseRank has only the infinity rank, then we stop processing
            if (currentRank.getRankNumber() == Symbols.INFINITY_RANK_NUMBER) {
                System.out.println("ONLY ∞ rank remains, so we stop processing with current K = " + materialisedKnowledgeBase);
                break;
            }

            System.out.println("  Current BaseRank");
            for (ModelRank rank : baseRankCollection) {
                System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
            }

            System.out.println(String.format("  Current K: %s", materialisedKnowledgeBase));
            System.out.println(String.format("  Checking if %s is entailed by current K", negationOfAntecedent));

            boolean isNegationOfAntecedentEntailed = _reasoner.query(materialisedKnowledgeBase, negationOfAntecedent);

            if (!isNegationOfAntecedentEntailed) {
                System.out.println("  NO its not, so we stop rank removal process with current K = " + materialisedKnowledgeBase);
                break;
            }

            System.out.println(String.format("  YES it is, so we remove rank %s: %s", currentRank.getRankNumber(), currentRank.getFormulas()));
            removedRanking.add(currentRank);
            baseRankCollection.remove(0);         
        }

        Collections.sort(baseRankCollection, (o1, o2) -> Integer.compare(o2.getRankNumber(), o1.getRankNumber()));
        Collections.sort(removedRanking, (o1, o2) -> Integer.compare(o2.getRankNumber(), o1.getRankNumber()));

        System.out.println();
        System.out.println("->Remaining Ranks:");
        for (ModelRank rank : baseRankCollection) {
            System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }
        
        System.out.println("->Removed Ranks:");
        for (ModelRank rank : removedRanking) {
            System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

        var materialisedQueryFormula = ReasonerUtils.toMaterialisedFormula(queryFormula);
        var materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection);

        boolean isQueryEntailed = !baseRankCollection.isEmpty() && _reasoner.query(materialisedKnowledgeBase, materialisedQueryFormula);
        KnowledgeBase entailmentKb = new KnowledgeBase();

        var hasEntailed = "NO";
        if (isQueryEntailed) {
            entailmentKb = ReasonerUtils.toKnowledgeBase(baseRankCollection);
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
                .withRemainingRanking(baseRankCollection)
                .withEntailmentKnowledgeBase(entailmentKb)
                .withEntailed(isQueryEntailed)
                .withTimeTaken((endTime - startTime) / 1_000_000_000.0)
                .build();
    }

}

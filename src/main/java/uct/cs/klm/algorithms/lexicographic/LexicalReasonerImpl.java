package uct.cs.klm.algorithms.lexicographic;

import java.util.Collections;

import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.models.ModelEntailment;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.models.ModelLexicographicEntailment;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;
import uct.cs.klm.algorithms.services.IReasonerService;
import uct.cs.klm.algorithms.services.KlmReasonerBase;
import uct.cs.klm.algorithms.utils.DisplayUtils;
import uct.cs.klm.algorithms.utils.ReasonerUtils;
import uct.cs.klm.algorithms.utils.Symbols;

/**
 * <h1>DefeasibleParserHelper<\h1>
 * The Defeasible Parser Helper.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-07-03
 */
public class LexicalReasonerImpl extends KlmReasonerBase implements IReasonerService {

    public LexicalReasonerImpl() {
        super();
    }

    public ModelEntailment getEntailment7(
            ModelBaseRank baseRank,
            PlFormula queryFormula) {

        long startTime = System.nanoTime();

        System.out.println("==>Lexicographic Closure Entailment");

        long endTime = System.nanoTime();

        return new ModelLexicographicEntailment.ModelLexicographicEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
                .withMiniBaseRanking(new ModelRankCollection())
                .withRemovedRanking(new ModelRankCollection())
                .withRemainingRanking(baseRank.getRanking())
                .withWeakenedRanking(new ModelRankCollection())
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

        System.out.println("==>Lexicographic Closure Entailment");

        // get the negation of the antecedent of the query
        PlFormula negationOfAntecedent = new Negation(((Implication) queryFormula).getFirstFormula());

        ModelRankCollection baseRankCollection = new ModelRankCollection(baseRank.getRanking().clone());
        Collections.sort(baseRankCollection, (o1, o2) -> Integer.compare(o1.getRankNumber(), o2.getRankNumber()));

        System.out.println("->BaseRank");
        for (ModelRank rank : baseRankCollection) {
            System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

        System.out.println(String.format("->Query, α: %s", queryFormula));
        System.out.println(String.format("->Antecedent Negation of α: %s", negationOfAntecedent));

        var removedRanking = new ModelRankCollection();
        var weakenedRanking = new ModelRankCollection();
        var miniBaseRanking = new ModelRankCollection();

        int stepNumber = 1;
        while (true) {

            if (baseRankCollection.isEmpty()) {
                break;
            }

            var currentRank = baseRankCollection.get(0);

            var materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection);

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
            
            System.out.println(String.format("  YES it is, so we process mini-ranks within rank %s: %s", currentRank.getRankNumber(), currentRank.getFormulas()));
           
            var currentMiniRankCollection = ReasonerUtils.generateFormulaCombinations(currentRank);
            currentMiniRankCollection.sort((a, b) -> Integer.compare(b.getFormulas().size(), a.getFormulas().size()));

            for (ModelRank mini : currentMiniRankCollection) {
                mini.setRankNumber(currentRank.getRankNumber());
                miniBaseRanking.add(mini);
            }

            // remove the current rank from baserank
            baseRankCollection.remove(0);

            boolean stopProcessing = false;
            int miniStepNumber = 1;

            for (ModelRank currentMiniRank : currentMiniRankCollection) {

                if (currentMiniRank.isEmpty() || currentMiniRank.getFormulas().size() == currentRank.getFormulas().size()) {
                    continue;
                }

                var tempMiniBaseRank = new ModelRankCollection(baseRankCollection);

                currentMiniRank.setRankNumber(currentRank.getRankNumber());
                tempMiniBaseRank.add(currentMiniRank);

                Collections.sort(tempMiniBaseRank, (o1, o2) -> Integer.compare(o2.getRankNumber(), o1.getRankNumber()));

                System.out.println("  Current Temp MiniBaseRank");
                for (ModelRank rank : tempMiniBaseRank) {
                    System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
                }

                materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(tempMiniBaseRank);

                System.out.println(String.format("  Current K: %s", materialisedKnowledgeBase));
                System.out.println(String.format("  Checking if %s is entailed by current K", negationOfAntecedent));

                isNegationOfAntecedentEntailed = _reasoner.query(materialisedKnowledgeBase, negationOfAntecedent);

                if (!isNegationOfAntecedentEntailed) {
                    stopProcessing = true;

                    ModelRank remainingFormulaRank = new ModelRank(currentRank.getRankNumber(), currentMiniRank.getFormulas());
                    baseRankCollection.add(remainingFormulaRank);

                    weakenedRanking.add(currentRank);

                    var removedMiniRankFormalas = ReasonerUtils.removeFormulasFromKnowledgeBase(currentRank.getFormulas(), currentMiniRank.getFormulas());

                    if (!removedMiniRankFormalas.isEmpty()) {
                        removedRanking.add(new ModelRank(currentRank.getRankNumber(), removedMiniRankFormalas));
                    }

                    System.out.println("  NO its not, so we stop rank removal process with current K = " + ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection));
                    break;
                }

                System.out.println(String.format("  YES it is, so we ignore the mini rank %s: %s", miniStepNumber, currentMiniRank.getFormulas()));

            }

            if (stopProcessing) {
                break;
            }

            removedRanking.add(currentRank);
            weakenedRanking.add(currentRank);
        }

        Collections.sort(baseRankCollection, (o1, o2) -> Integer.compare(o2.getRankNumber(), o1.getRankNumber()));
        Collections.sort(removedRanking, (o1, o2) -> Integer.compare(o2.getRankNumber(), o1.getRankNumber()));
        Collections.sort(weakenedRanking, (o1, o2) -> Integer.compare(o2.getRankNumber(), o1.getRankNumber()));

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
               
        return new ModelLexicographicEntailment.ModelLexicographicEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
                .withMiniBaseRanking(miniBaseRanking)
                .withRemovedRanking(removedRanking)
                .withRemainingRanking(baseRankCollection)
                .withWeakenedRanking(weakenedRanking)
                .withEntailmentKnowledgeBase(entailmentKb)
                .withEntailed(isQueryEntailed)
                .withTimeTaken((endTime - startTime) / 1_000_000_000.0)
                .build();

        /*
          return new ModelLexicographicEntailment.ModelLexicographicEntailmentBuilder()
                .withKnowledgeBase(knowledgeBase)
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRanking)
                .withRemovedRanking(removedRanking)
                .withWeakenedRanking(weakenedRanking)
                .withEntailed(entailed)
                .withTimeTaken((endTime - startTime) / 1_000_000_000.0)
                .build();
         */
    }
}

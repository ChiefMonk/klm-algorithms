package uct.cs.klm.algorithms.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

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
import uct.cs.klm.algorithms.utils.ReasonerUtils;

public class LexicalReasonerImpl extends KlmReasonerBase implements IReasonerService {

    public LexicalReasonerImpl() {
        super();
    }

    @Override
    public Entailment getEntailment(ModelBaseRank baseRank, PlFormula queryFormula) {
        long startTime = System.nanoTime();

        System.out.println();
        System.out.println("==> Lexicographic Closure Entailment");
        System.out.println(String.format("Query: %s", queryFormula));

        ArrayList<ModelRank> originalBaseRanking = (ArrayList<ModelRank>) baseRank.getRanking().stream()
                .sorted(Comparator.comparing(ModelRank::getRankNumber))
                .collect(Collectors.toList());

        ModelRankCollection baseRanking = new ModelRankCollection(originalBaseRanking);

        System.out.println("Ranking");
        for (ModelRank rank : baseRanking) {
            System.out.println(String.format("%s:%s", rank.getRankNumber(), rank.getFormulas()));
        }

        KnowledgeBase combinedMaterialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(baseRanking);;
        PlFormula negationOfAntecedent = new Negation(((Implication) queryFormula).getFirstFormula());

        ModelRankCollection removedRanking = new ModelRankCollection();

        ModelRankCollection miniBaseRanking = new ModelRankCollection();

        for (ModelRank rank : originalBaseRanking) {

            //if (rank.getRankNumber() == Symbols.INFINITY_RANK_NUMBER) {
            //    miniBaseRanking.add(rank);
            //    continue;
            //}
            var miniRanks = ReasonerUtils.generateFormulaCombinations(rank);
            for (ModelRank mini : miniRanks) {
                mini.setRankNumber(rank.getRankNumber());
                miniBaseRanking.add(mini);
            }
        }

        boolean continueProcessing = true;

        while (!baseRanking.isEmpty()) {

            if (!continueProcessing) {
                break;
            }

            ModelRank currentRank = null;

            if (!baseRanking.isEmpty()) {
                currentRank = baseRanking.get(0);
            }

            if (currentRank == null) {
                break;
            }

            // remove all the formulas in currentRank from baseRanking;
            // removedRanking.add(currentRank);           
            baseRanking.remove(0);

            var currentRankBaseRanks = ReasonerUtils.generateFormulaCombinations(currentRank);

            for (int i = 0; i < currentRankBaseRanks.size(); i++) {

                var currentMiniBaseRank = currentRankBaseRanks.get(i);
                var currentMiniFormulas = currentMiniBaseRank.getFormulas();

                System.out.println();
                System.out.println(String.format("Processing Mini Rank = %s:%s:%s", currentRank.getRankNumber(), i, currentMiniFormulas));

                var materialisedCurrentKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(currentMiniFormulas);
                var materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(baseRanking);
                combinedMaterialisedKnowledgeBase = materialisedKnowledgeBase.union(materialisedCurrentKnowledgeBase);

                System.out.println(String.format("Checking if %s is entailed by %s", negationOfAntecedent, combinedMaterialisedKnowledgeBase));

                boolean isEntailed = _reasoner.query(combinedMaterialisedKnowledgeBase, negationOfAntecedent);

                if (isEntailed) {
                    System.out.println(String.format("YES: so we remove mini rank %s:%s:%s", currentRank.getRankNumber(), i, currentMiniFormulas));

                    if (removedRanking.isEmpty() || removedRanking.get(removedRanking.size() - 1).getRankNumber() != currentRank.getRankNumber()) {
                        removedRanking.add(new ModelRank(currentRank.getRankNumber()));
                    }

                    if (currentMiniFormulas.size() == 1) {
                        removedRanking.get(removedRanking.size() - 1).addFormulas(currentMiniFormulas);
                    }

                } else {
                    continueProcessing = false;
                    System.out.println("NO: So we stop processing=" + combinedMaterialisedKnowledgeBase);
                    break;

                }

            }
        }

        boolean isQueryEntailed = !baseRanking.isEmpty() && _reasoner.query(combinedMaterialisedKnowledgeBase, ReasonerUtils.toMaterialisedFormula(queryFormula));
        KnowledgeBase entailmentKb = new KnowledgeBase();

        if (isQueryEntailed) {
            entailmentKb = combinedMaterialisedKnowledgeBase;
            System.out.println(String.format("LC Checking if %s entails %s = TRUE", combinedMaterialisedKnowledgeBase, queryFormula));
        } else {
            System.out.println(String.format("LC Checking if %s entails %s = FALSE", combinedMaterialisedKnowledgeBase, queryFormula));
        }

        var remainingRanking = ReasonerUtils.toRemainingEntailmentRanks(originalBaseRanking, combinedMaterialisedKnowledgeBase);
        System.out.println("Remaining Ranking:");
        for (ModelRank rank : remainingRanking) {
            System.out.println(String.format("%s:%s", rank.getRankNumber(), rank.getFormulas()));
        }

        long endTime = System.nanoTime();

        return new LexicalEntailment.LexicalEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
                .withMiniBaseRanking(miniBaseRanking)
                .withRemovedRanking(removedRanking)
                .withRemainingRanking(remainingRanking)
                .withWeakenedRanking(remainingRanking)
                .withEntailmentKnowledgeBase(entailmentKb)
                .withEntailed(isQueryEntailed)
                .withTimeTaken((endTime - startTime) / 1_000_000_000.0)
                .build();

        /*
          return new LexicalEntailment.LexicalEntailmentBuilder()
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

    public Entailment getEntailment2(ModelBaseRank baseRank, PlFormula queryFormula) {
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
        while (!union.isEmpty() && _reasoner.query(union, negation) && i < baseRanking.size() - 1) {
            union.removeAll(baseRanking.get(i).getFormulas());

            int m = baseRanking.get(i).getFormulas().size() - 1;
            KnowledgeBase weakenedRank = new KnowledgeBase(Arrays.asList(weakenRank(baseRanking.get(i), m)));
            weakenedRanking.add(new ModelRank(i, weakenedRank));
            while (_reasoner.query(union.union(weakenedRank), negation) && m > 0) {
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

        boolean entailed = !union.isEmpty() && _reasoner.query(union, queryFormula);
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

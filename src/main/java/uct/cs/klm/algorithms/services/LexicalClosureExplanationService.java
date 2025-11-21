package uct.cs.klm.algorithms.services;

import uct.cs.klm.algorithms.models.ModelEntailment;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;
import uct.cs.klm.algorithms.rational.RCRankEntailmentCheck;
import uct.cs.klm.algorithms.rational.RationalClosureExplanation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class represents a lexical closure explanation service for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

public class LexicalClosureExplanationService implements IReasonerExplanationService<RationalClosureExplanation> {
    @Override
    public RationalClosureExplanation generateExplanation(ModelEntailment entailment) {
        ModelRankCollection baseRanking = entailment.getBaseRanking();
        baseRanking.sort(Comparator.comparingInt(ModelRank::getRankNumber));

        ModelRankCollection removedRanking = entailment.getRemovedRanking();
        removedRanking.sort(Comparator.comparingInt(ModelRank::getRankNumber));

        ModelRankCollection remainingRanking = entailment.getRemainingRanking();
        remainingRanking.sort(Comparator.comparingInt(ModelRank::getRankNumber));

        List<RCRankEntailmentCheck> checks = new ArrayList<>();
        boolean isConsistent = false;
        for (int i = 0, n = baseRanking.size(); i < n && !isConsistent; i++) {
            int finalI = i;
            isConsistent = removedRanking
                    .stream()
                    .noneMatch(rank -> rank.getRankNumber() ==  baseRanking.get(finalI).getRankNumber());

            RCRankEntailmentCheck check = new RCRankEntailmentCheck(new ModelRankCollection(baseRanking.subList(i, n)), isConsistent, entailment.getNegation());
            checks.add(check);
        }

        return new RationalClosureExplanation(
                entailment.getQueryFormula(),
                entailment.getKnowledgeBase(),
                entailment.getEntailed(),
                checks,
                baseRanking,
                removedRanking,
                remainingRanking,
                entailment.getJustification()
        );

    }
}

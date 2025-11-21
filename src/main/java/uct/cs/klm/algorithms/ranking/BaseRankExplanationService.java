package uct.cs.klm.algorithms.ranking;

import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.utils.Symbols;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class represents a base rank explanation service for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public class BaseRankExplanationService implements IBaseRankExplanationService {
    @Override
    public BaseRankExplanation generateExplanation(ModelBaseRank baseRank) {
        KnowledgeBase knowledgeBase = baseRank.getKnowledgeBase();

        ModelRankCollection ranks = baseRank.getRanking();
        ranks.sort(Comparator.comparingInt(ModelRank::getRankNumber));

        ModelRankCollection sequence = baseRank.getSequence();
        sequence.sort(Comparator.comparingInt(ModelRank::getRankNumber));

        ModelRank infiniteRank = ranks.stream().filter(rank -> rank.getRankNumber() == Symbols.INFINITY_RANK_NUMBER).findFirst().orElse(null);

        if (infiniteRank != null) {
            for(ModelRank sequenceElement: sequence) {
                sequenceElement.addFormulas(infiniteRank.getFormulas());
            }
        }

        if (!sequence.isEmpty() && !sequence.getLast().isEmpty()) {
            ModelRank lastElement = new ModelRank(sequence.getLast());
            sequence.add(lastElement);
        }

        List<SequenceElement> sequenceElements = getSequenceElements(sequence, ranks);
        return new BaseRankExplanation(knowledgeBase, sequenceElements, ranks);
    }

    private static List<SequenceElement> getSequenceElements(ModelRankCollection sequence, ModelRankCollection ranks) {
        List<SequenceElement> sequenceElements = new ArrayList<>();

        for (int i = 0, n = sequence.size(); i < n; i++) {
            List<SequenceElementCheck> checks = new ArrayList<>();
            boolean isLastElement = i == n - 1;

            int m = ranks.size();
            for (int j = Math.min(i, m - 1); j < m; j++) {
                for (PlFormula formula: ranks.get(j).getFormulas()) {
                    boolean isExceptional = sequence.get(i).getRankNumber() == ranks.get(j).getRankNumber() &&
                            sequence.get(i).getRankNumber() < Symbols.INFINITY_RANK_NUMBER;
                    PlFormula antecedentNegation = new Negation(((Implication) formula).getFirstFormula());

                    checks.add(new SequenceElementCheck(sequence.get(i).getRankNumber(), antecedentNegation, formula, isExceptional));
                }
            }

            sequenceElements.add(new SequenceElement(sequence.get(i).getRankNumber(), sequence.get(i).getFormulas(), checks, isLastElement));
        }
        return sequenceElements;
    }
}

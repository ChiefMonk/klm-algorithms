package uct.cs.klm.algorithms.rational;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;

import java.util.List;

/**
 * This class represents a rational closure rank entailment check for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

public record RCRankEntailmentCheck(
        ModelRankCollection ranks,
        boolean isConsistent,
        PlFormula antecedentNegation
) {
    @JsonProperty
    public KnowledgeBase knowledgeBase() {
        KnowledgeBase union = new KnowledgeBase();
        for (ModelRank rank : ranks) {
            union.addAll(rank.getFormulas());
        }
        return union;
    }

    @JsonProperty
    public ModelRank removedRank() {
        if (!isConsistent) {
            return ranks.stream().findFirst().orElse(null);
        }
        return null;
    }
}

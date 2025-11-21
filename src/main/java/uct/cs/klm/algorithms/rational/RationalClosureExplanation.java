package uct.cs.klm.algorithms.rational;

import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;

import java.util.List;

/**
 * This class represents a rational closure explanation for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public record RationalClosureExplanation(
        PlFormula queryFormula,
        KnowledgeBase knowledgeBase,
        boolean isEntailed,
        List<RCRankEntailmentCheck> checks,
        ModelRankCollection baseRanking,
        ModelRankCollection removedRanking,
        ModelRankCollection remainingRanking,
        List<KnowledgeBase> justification
) {     
}

package uct.cs.klm.algorithms.rational;

import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;

import java.util.List;

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

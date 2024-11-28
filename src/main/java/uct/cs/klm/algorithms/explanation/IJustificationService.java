package uct.cs.klm.algorithms.explanation;

import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;

/**
 *
 * @author Chipo Hamayobe
 */
public interface IJustificationService 
{
    public KnowledgeBase computeJustification(           
            ModelRankCollection remainingRanking, 
            PlFormula query);
}

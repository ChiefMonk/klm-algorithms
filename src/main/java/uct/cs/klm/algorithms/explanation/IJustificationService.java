package uct.cs.klm.algorithms.explanation;

import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.models.Ranking;

/**
 *
 * @author Chipo Hamayobe
 */
public interface IJustificationService 
{
    public KnowledgeBase computeJustification(           
            Ranking remainingRanking, 
            PlFormula query);
}

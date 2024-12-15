package uct.cs.klm.algorithms.explanation;

import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.models.KnowledgeBase;

/**
 *
 * @author Chipo Hamayobe
 */
public interface IJustificationService 
{   
    public KnowledgeBase computeJustification(           
            KnowledgeBase remainingKnowledgeBase, 
            PlFormula query);
}

package uct.cs.klm.algorithms.explanation;

import java.util.ArrayList;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.ModelRank;

/**
 *
 * @author Chipo Hamayobe
 */
public interface IJustificationService 
{   
    public ArrayList<KnowledgeBase> computeAllJustifications(     
            ModelRank infinityRank,
            KnowledgeBase remainingKnowledgeBase, 
            PlFormula query,
            boolean convertDefeasible);
}

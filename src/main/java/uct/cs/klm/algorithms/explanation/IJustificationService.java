package uct.cs.klm.algorithms.explanation;

import java.util.ArrayList;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.ModelRank;

/**
 * This interface represents a justification service for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public interface IJustificationService 
{   
    public ArrayList<KnowledgeBase> computeAllJustifications(     
            ModelRank infinityRank,
            KnowledgeBase remainingKnowledgeBase, 
            PlFormula query,
            boolean convertDefeasible);
}

package uct.cs.klm.algorithms.relevant;

import java.util.ArrayList;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.enums.ReasonerType;
import uct.cs.klm.algorithms.explanation.IJustificationService;
import uct.cs.klm.algorithms.explanation.JustificationServiceBase;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.ModelRank;


/**
 * This class represents a basic relevant justification service for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public class BasicRelevantJustificationService extends JustificationServiceBase implements IJustificationService 
{   
    public BasicRelevantJustificationService()
    {
        super();
    }
    
    @Override
    public ArrayList<KnowledgeBase> computeAllJustifications(
            ModelRank infinityRank,
            KnowledgeBase remainingKnowledgeBase, 
            PlFormula queryFormula,
            boolean convertDefeasible) 
    {   
        return super.computeAllJustifications(
                infinityRank,
                ReasonerType.BasicRelevantClosure,
                remainingKnowledgeBase, 
                queryFormula,
                convertDefeasible);            
    }
}

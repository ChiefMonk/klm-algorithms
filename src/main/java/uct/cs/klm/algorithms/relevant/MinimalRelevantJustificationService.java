package uct.cs.klm.algorithms.relevant;

import java.util.ArrayList;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.enums.ReasonerType;
import uct.cs.klm.algorithms.explanation.IJustificationService;
import uct.cs.klm.algorithms.explanation.JustificationServiceBase;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.ModelRank;


/**
 *
 * @author Chipo Hamayobe
 */
public class MinimalRelevantJustificationService extends JustificationServiceBase implements IJustificationService 
{   
    public MinimalRelevantJustificationService()
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
                ReasonerType.MinimalRelevantClosure,
                remainingKnowledgeBase, 
                queryFormula,
                convertDefeasible);            
    }
}

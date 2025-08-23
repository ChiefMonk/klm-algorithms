package uct.cs.klm.algorithms.relevant;

import java.util.ArrayList;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.enums.ReasonerType;
import uct.cs.klm.algorithms.explanation.IJustificationService;
import uct.cs.klm.algorithms.explanation.JustificationServiceBase;
import uct.cs.klm.algorithms.models.KnowledgeBase;


/**
 *
 * @author Chipo Hamayobe
 */
public class BasicRelevantJustificationService extends JustificationServiceBase implements IJustificationService 
{   
    public BasicRelevantJustificationService()
    {
        super();
    }
    
    @Override
    public ArrayList<KnowledgeBase> computeJustification(
            KnowledgeBase remainingKnowledgeBase, 
            PlFormula queryFormula) 
    {   
        return super.computeJustification(
                ReasonerType.BasicRelevantClosure,
                remainingKnowledgeBase, 
                queryFormula);            
    }
}

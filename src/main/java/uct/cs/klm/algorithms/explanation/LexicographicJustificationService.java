package uct.cs.klm.algorithms.explanation;

import java.util.ArrayList;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.enums.ReasonerType;
import uct.cs.klm.algorithms.models.KnowledgeBase;


/**
 *
 * @author Chipo Hamayobe
 */
public class LexicographicJustificationService extends JustificationServiceBase implements IJustificationService 
{   
    public LexicographicJustificationService()
    {
        super();
    }
    
    @Override
    public ArrayList<KnowledgeBase> computeJustification(
            KnowledgeBase remainingKnowledgeBase, 
            PlFormula queryFormula) 
    {   
        return super.computeJustification(
                ReasonerType.RationalClosure,
                remainingKnowledgeBase, 
                queryFormula);            
    }
}
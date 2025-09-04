package uct.cs.klm.algorithms.lexicographic;

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
public class LexicographicClosureJustificationService extends JustificationServiceBase implements IJustificationService 
{   
    public LexicographicClosureJustificationService()
    {
        super();
    }
    
    @Override
    public ArrayList<KnowledgeBase> computeAllJustifications(
            ModelRank infinityRank,
            KnowledgeBase remainingKnowledgeBase, 
            PlFormula queryFormula) 
    {   
        return super.computeAllJustifications(
                infinityRank,
                ReasonerType.LexicographicClosure,
                remainingKnowledgeBase, 
                queryFormula);            
    }
}
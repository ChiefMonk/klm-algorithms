package uct.cs.klm.algorithms.relevant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.enums.ReasonerType;
import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.models.ModelEntailment;
import uct.cs.klm.algorithms.services.IReasonerService;

/**
 * This class represents a minimal relevant reasoner implementation for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

public class MinimalRelevantReasonerImpl extends RelevantClosureEntailmentBase implements IReasonerService {

    private static final Logger _logger = LoggerFactory.getLogger(MinimalRelevantReasonerImpl.class);

    public MinimalRelevantReasonerImpl() {
        super();
    }

    @Override
    public ModelEntailment getEntailment(
            ModelBaseRank baseRank,
            PlFormula queryFormula) {

        _logger.debug("==>Minimal Relevant Closure Entailment");
      
        return super.determineEntailment(ReasonerType.MinimalRelevantClosure,baseRank, queryFormula);
      
    }       
}

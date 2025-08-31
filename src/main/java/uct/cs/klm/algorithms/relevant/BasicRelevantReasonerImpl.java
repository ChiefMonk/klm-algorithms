package uct.cs.klm.algorithms.relevant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.models.ModelEntailment;
import uct.cs.klm.algorithms.services.IReasonerService;

public class BasicRelevantReasonerImpl extends RelevantClosureEntailmentBase implements IReasonerService {

    private static final Logger _logger = LoggerFactory.getLogger(BasicRelevantReasonerImpl.class);

    public BasicRelevantReasonerImpl() {
        super();
    }

    @Override
    public ModelEntailment getEntailment(
            ModelBaseRank baseRank,
            PlFormula queryFormula) {

        _logger.debug("==>Basic Closure Entailment");
      
        return super.determineEntailment(baseRank, queryFormula);
      
    }    
}

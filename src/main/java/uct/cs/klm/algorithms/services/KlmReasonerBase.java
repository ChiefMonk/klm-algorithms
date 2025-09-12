package uct.cs.klm.algorithms.services;

import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.utils.ReasonerUtils;

/**
 *
 * @author ChipoHamayobe
 */
public abstract class KlmReasonerBase {

    protected final SatReasoner _reasoner;

    public KlmReasonerBase() {
        SatSolver.setDefaultSolver(new Sat4jSolver());
        _reasoner = new SatReasoner();
    }

    protected boolean doesInfinityRankEntailQuery(ModelRank rank, PlFormula queryFormula) {

        PlFormula materialisedQueryFormula = ReasonerUtils.toMaterialisedFormula(queryFormula);
        var materialisedKB = ReasonerUtils.toMaterialisedKnowledgeBase(rank);

        return _reasoner.query(materialisedKB, materialisedQueryFormula);
    }
}

package uct.cs.klm.algorithms.lexicographic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.models.*;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;
import uct.cs.klm.algorithms.services.IReasonerService;
import uct.cs.klm.algorithms.services.KlmReasonerBase;
import uct.cs.klm.algorithms.utils.*;

/**
 * <h1>DefeasibleParserHelper<\h1>
 * The Defeasible Parser Helper.
 *
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public class LexicographicClosureReasonerImpl extends KlmReasonerBase implements IReasonerService {

    private static final Logger _logger = LoggerFactory.getLogger(LexicographicClosureReasonerImpl.class);

    public LexicographicClosureReasonerImpl() {
        super();
    }

    @Override
    public ModelEntailment getEntailment(ModelBaseRank baseRank, PlFormula queryFormula) {

        _logger.debug("==> Lexicographic Closure Entailment");

        long startTime = System.nanoTime();

        PlFormula negationOfAntecedent = new Negation(((Implication) queryFormula).getFirstFormula());
        PlFormula materialisedQueryFormula = ReasonerUtils.toMaterialisedFormula(queryFormula);

        ModelRankCollection baseRankCollection = new ModelRankCollection(baseRank.getRanking().clone());
        Collections.sort(baseRankCollection, (o1, o2) -> Integer.compare(o1.getRankNumber(), o2.getRankNumber()));

        ModelRankCollection relevantRanking = baseRank.getRanking().getRankingCollectonExceptInfinity();
        ModelRank nonRelevantRanking = baseRank.getRanking().getInfinityRank();
        List<KnowledgeBase> relevantPowersets = ReasonerUtils.toPowerSetOrdered(relevantRanking);
        relevantPowersets.add(baseRank.getRanking().getInfinityRank().getFormulas());

        _logger.debug(String.format("->Query: %s", queryFormula));
        _logger.debug(String.format("->Query Antecedent Negation: %s", negationOfAntecedent));

        var materialisedKb = ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection);

        _logger.debug(String.format("-> Checking if full KB is consistent with query %s", materialisedKb));

        boolean continueProcessing = true;
        boolean isQueryEntailed = false;
        boolean isNegationEntailed = _reasoner.query(materialisedKb, negationOfAntecedent);

        if (isNegationEntailed) {
            DisplayUtils.LogDebug(_logger, String.format("=> YES - NegationOfAntecedent:Entailed; We skip and consider the relevant subsets"));
        } else {
            _logger.debug("  NOT - NegationOfAntecedent:Entailed; We checking if materialisedKB entails query");
            isQueryEntailed = _reasoner.query(materialisedKb, materialisedQueryFormula);

            if (isQueryEntailed) {
                continueProcessing = false;
            } else {
                _logger.debug("  But the query is not entailed by the remaining statements");
            }
        }

        if (!continueProcessing) {

            return CreateResponse(
                    baseRank,
                    queryFormula,
                    materialisedKb,
                    relevantPowersets,
                    isQueryEntailed,
                    startTime);
        }

        int counter = 1;
        for (KnowledgeBase powerKb : relevantPowersets) {

            if (!continueProcessing) {
                break;
            }

            DisplayUtils.LogDebug(_logger, String.format("=> Powerset %s := %s", counter, powerKb));

            var combinedKb = ReasonerUtils.toCombinedKnowledgeBases(nonRelevantRanking.getFormulas(), powerKb);

            materialisedKb = ReasonerUtils.toMaterialisedKnowledgeBase(combinedKb);

            DisplayUtils.LogDebug(_logger, String.format("=> Materialised KB := %s", materialisedKb));

            isNegationEntailed = _reasoner.query(materialisedKb, negationOfAntecedent);

            if (isNegationEntailed) {
                DisplayUtils.LogDebug(_logger, String.format("=> YES - NegationOfAntecedent:Entailed; We skip and move next subset: %s", powerKb));
            } else {
                _logger.debug("  NOT - NegationOfAntecedent:Entailed; We checking if materialisedKB entails query");
                isQueryEntailed = _reasoner.query(materialisedKb, materialisedQueryFormula);
                if (isQueryEntailed) {
                    continueProcessing = false;
                } else {
                    _logger.debug("  But the query is not entailed by the remaining statements");
                }
            }

            counter++;
        }

        return CreateResponse(
                baseRank,
                queryFormula,
                materialisedKb,
                relevantPowersets,
                isQueryEntailed,
                startTime);

    }

    private ModelEntailment CreateResponse(
            ModelBaseRank baseRank,
            PlFormula queryFormula,
            KnowledgeBase materialisedKb,
            List<KnowledgeBase> powersets,
            boolean isQueryEntailed,
            long startTime) {

        ModelRankCollection remainingRanking = ReasonerUtils.toRanksFromKnowledgeBase(baseRank, materialisedKb, false);
        ModelRankCollection removedRanking = ReasonerUtils.toRanksFromKnowledgeBase(baseRank, remainingRanking.getKnowledgeBase(), true);

        if (!isQueryEntailed) {
            var infinityRank = baseRank.getRanking().clone().getInfinityRank();
            isQueryEntailed = doesInfinityRankEntailQuery(infinityRank, queryFormula);

            if (isQueryEntailed) {
                remainingRanking = new ModelRankCollection(infinityRank);
                removedRanking = baseRank.getRanking().getRankingCollectonExcept(Symbols.INFINITY_RANK_NUMBER);
            }
        }

        if (isQueryEntailed) {
            _logger.debug(String.format("-> Entailment:YES : %s entails %s", materialisedKb, queryFormula));
        } else {
            _logger.debug(String.format("-> Entailment:NO : %s does not entail %s", materialisedKb, queryFormula));
        }

        ArrayList<ModelRankResponse> powersetRanking = ReasonerUtils.toResponseRanks(baseRank, powersets);
        
        var finalTime = ReasonerUtils.ToTimeDifference(startTime, System.nanoTime());

        return new ModelLexicographicEntailment.ModelLexicographicEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
                .withRemovedRanking(removedRanking)
                .withRemainingRanking(remainingRanking)
                .withWeakenedRanking(new ModelRankCollection())
                .withPowersetRanking(powersetRanking)
                .withEntailmentKnowledgeBase(remainingRanking.getKnowledgeBase())
                .withEntailed(isQueryEntailed)
                .withTimeTaken(finalTime)
                .build();
    }
}

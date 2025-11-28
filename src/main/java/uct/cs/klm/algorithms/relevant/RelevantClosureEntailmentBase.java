package uct.cs.klm.algorithms.relevant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.enums.ReasonerType;
import uct.cs.klm.algorithms.explanation.IJustificationService;

import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.models.ModelEntailment;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.models.ModelRankResponse;
import uct.cs.klm.algorithms.models.ModelRelevantClosureEntailment;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.services.KlmReasonerBase;
import uct.cs.klm.algorithms.utils.DisplayUtils;
import uct.cs.klm.algorithms.utils.ReasonerFactory;
import uct.cs.klm.algorithms.utils.ReasonerUtils;
import uct.cs.klm.algorithms.utils.Symbols;

/**
 * This class represents a relevant closure entailment base for a given query.
 *
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public abstract class RelevantClosureEntailmentBase extends KlmReasonerBase {

    private static final Logger _logger = LoggerFactory.getLogger(RelevantClosureEntailmentBase.class);

    public RelevantClosureEntailmentBase() {
        super();
    }

    protected ModelEntailment determineEntailment(
            ReasonerType reasonerType,
            ModelBaseRank baseRank,
            PlFormula queryFormula) {

        long startTime = System.nanoTime();

        PlFormula negationOfAntecedent = new Negation(((Implication) queryFormula).getFirstFormula());
        PlFormula materialisedQueryFormula = ReasonerUtils.toMaterialisedFormula(queryFormula);

        ModelRankCollection baseRankCollection = new ModelRankCollection(baseRank.getRanking().clone());
        Collections.sort(baseRankCollection, (o1, o2) -> Integer.compare(o1.getRankNumber(), o2.getRankNumber()));

        _logger.debug(String.format("->Query: %s", queryFormula));
        _logger.debug(String.format("->Query Antecedent Negation: %s", negationOfAntecedent));

        int consistentRank = 1;
        ModelRelevanceResult relevanceResult = GetRelevantRanks(reasonerType, baseRankCollection, negationOfAntecedent);
        ModelRankCollection relevantRanking = relevanceResult.getCorrectRelevantRanking();
        ModelRankCollection relevantRankingAll = relevanceResult.getRelevantRanking();
        ModelRankCollection irrelevantRanking = relevanceResult.getCorrectIrrelevantRanking();
        ModelRankCollection irrelevantRankingAll = relevanceResult.getIrrelevantRanking();

        ModelRankCollection nonRelevantRanking = ReasonerUtils.toRanksFromKnowledgeBase(baseRank, relevantRanking.getKnowledgeBase(), true);

        List<KnowledgeBase> relevantPowersets = ReasonerUtils.toPowerSetOrdered(relevantRanking);

        _logger.debug("R+All: {}", relevantRankingAll.getKnowledgeBase());
        _logger.debug("R+: {}", relevantRanking.getKnowledgeBase());

        _logger.debug("->R+ Ranking");
        for (ModelRank rank : relevantRanking) {
            _logger.debug(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

        _logger.debug("R-All: {}", irrelevantRankingAll.getKnowledgeBase());
        _logger.debug("R-: {}", irrelevantRanking.getKnowledgeBase());

        _logger.debug("->R- Ranking");
        for (ModelRank rank : irrelevantRanking) {
            _logger.debug(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

        _logger.debug("->Non RelevantRanking All");
        for (ModelRank rank : nonRelevantRanking) {
            _logger.debug(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

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
                    relevantRanking,
                    baseRankCollection,
                    relevantPowersets,
                    consistentRank,
                    relevanceResult.getRelevantKnowledgeBase(),
                    relevanceResult.getJustification(),
                    isQueryEntailed,
                    startTime);
        }

        relevantPowersets.add(baseRank.getRanking().getInfinityRank().getFormulas());

        consistentRank = 0;
        for (KnowledgeBase powerKb : relevantPowersets) {

            if (!continueProcessing) {
                break;
            }

            DisplayUtils.LogDebug(_logger, String.format("=> Powerset %s := %s", consistentRank + 1, powerKb));

            var combinedKb = ReasonerUtils.toCombinedKnowledgeBases(nonRelevantRanking.getKnowledgeBase(), powerKb);

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

            consistentRank++;
        }

        return CreateResponse(
                baseRank,
                queryFormula,
                materialisedKb,
                relevantRanking,
                irrelevantRankingAll,
                relevantPowersets,
                consistentRank,
                relevanceResult.getRelevantKnowledgeBase(),
                relevanceResult.getJustification(),
                isQueryEntailed,
                startTime);

    }

    private ModelEntailment CreateResponse(
            ModelBaseRank baseRank,
            PlFormula queryFormula,
            KnowledgeBase materialisedKb,
            ModelRankCollection relevantRanking,
            ModelRankCollection irrelevantRanking,
            List<KnowledgeBase> powersets,
            int consistentRank,
            KnowledgeBase relevantKnowledgeBase,
            ArrayList<KnowledgeBase> relevantJustification,
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
            } else {
                if (consistentRank == powersets.size()) {
                    consistentRank = 0;
                }
            }
        }

        if (isQueryEntailed) {
            _logger.debug(String.format("-> Entailment:YES : %s entails %s", materialisedKb, queryFormula));
        } else {
            _logger.debug(String.format("-> Entailment:NO : %s does not entail %s", materialisedKb, queryFormula));
        }

        ArrayList<ModelRankResponse> powersetRanking = ReasonerUtils.toResponseRanks(baseRank, irrelevantRanking, powersets);

        _logger.debug(String.format("-> Powerset Ranking"));
        for (var k : powersetRanking) {
            _logger.debug(String.format("=> %s: %s", k.rankNumber(), k.formulas()));
        }

        var finalTime = ReasonerUtils.ToTimeDifference(startTime, System.nanoTime());

        return new ModelRelevantClosureEntailment.ModelRelevantClosureEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBaseKb())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
                .withRemovedRanking(removedRanking)
                .withRemainingRanking(remainingRanking)
                .withRelevantRankCollection(relevantRanking)
                .withIrrelevantRankCollection(irrelevantRanking)
                .withConsistentRank(consistentRank)
                .withPowersetRanking(powersetRanking)
                .withEntailmentKnowledgeBase(remainingRanking.getKnowledgeBase())
                .withRelevantJustification(relevantJustification)
                .withRelevantKnowledgeBase(relevantKnowledgeBase)
                .withEntailed(isQueryEntailed)
                .withTimeTaken(finalTime)
                .build();
    }

    private ModelRelevanceResult GetRelevantRanks(
            ReasonerType reasonerType,
            ModelRankCollection baseRankCollection,
            PlFormula negationOfAntecedent) {

        var originalKb = baseRankCollection.getKnowledgeBase();

        IJustificationService justificationService = ReasonerFactory.createJustification(reasonerType);
        var justificationCollection = justificationService.computeAllJustifications(
                baseRankCollection.getInfinityRank(),
                originalKb,
                negationOfAntecedent,
                false);

        KnowledgeBase incosistentKb = new KnowledgeBase();

        KnowledgeBase relevantKb = new KnowledgeBase();

        for (var justificationEntry : justificationCollection) {

            relevantKb.addKnowledgeBase(justificationEntry);

            for (var formula : justificationEntry) {

                var formulaMaterialised = ReasonerUtils.toMaterialisedFormula(formula);

                if (!incosistentKb.contains(formulaMaterialised)) {
                    incosistentKb.add(formulaMaterialised);
                }
            }
        }

        KnowledgeBase miniKb = new KnowledgeBase();

        if (reasonerType == ReasonerType.MinimalRelevantClosure) {
            int counter = 0;
            for (int i = 0; i < justificationCollection.size(); i++) {

                var rank = baseRankCollection.getRank(counter);
                var rankNumber = -1;

                for (var formula : justificationCollection.get(i)) {

                    var deMaterialised = ReasonerUtils.toDematerialisedFormula(formula);

                    if (rank != null && rank.getFormulas().contains(deMaterialised)) {
                        if (rankNumber == -1) {
                            rankNumber = rank.getRankNumber();
                        }

                        if (rank.getRankNumber() == rankNumber) {
                            miniKb.add(deMaterialised);
                        }
                    }
                }

                if (rankNumber == -1) {
                    i = i - 1;
                    counter++;
                }
            }

            _logger.debug(String.format("   Mini := %s", miniKb.getFormulas()));
        }

        ModelRankCollection resultIncosistentRank = new ModelRankCollection();
        ModelRankCollection resultRelevantRank = new ModelRankCollection();
        ModelRankCollection resultIrrelevantRank = new ModelRankCollection();

        for (var currentRank : baseRankCollection) {

            var rankNumber = currentRank.getRankNumber();

            var incosistentRank = new ModelRank(rankNumber);
            var addIncosistentRank = false;

            var irrelevantRank = new ModelRank(rankNumber);
            boolean addIrrelevantRank = false;

            var relevantRank = new ModelRank(rankNumber);
            boolean addRelevantRank = false;

            for (var formula : currentRank.getFormulas()) {

                var formulaMaterialised = ReasonerUtils.toMaterialisedFormula(formula);

                if (incosistentKb.contains(formulaMaterialised)) {
                    addIncosistentRank = true;
                    incosistentRank.addFormula(formula);
                }

                if (reasonerType == ReasonerType.MinimalRelevantClosure) {
                    if (ReasonerUtils.toMaterialisedKnowledgeBase(miniKb).contains(formulaMaterialised)) {
                        addRelevantRank = true;
                        relevantRank.addFormula(formula);
                    } else {
                        addIrrelevantRank = true;
                        irrelevantRank.addFormula(formula);
                    }

                } else {
                    if (incosistentKb.contains(formulaMaterialised)) {
                        addRelevantRank = true;
                        relevantRank.addFormula(formula);
                    } else {
                        addIrrelevantRank = true;
                        irrelevantRank.addFormula(formula);
                    }
                }

            }

            if (addIncosistentRank) {
                resultIncosistentRank.add(incosistentRank);
            }

            if (addRelevantRank) {
                resultRelevantRank.add(relevantRank);
            }

            if (addIrrelevantRank) {
                resultIrrelevantRank.add(irrelevantRank);
            }
        }

        _logger.debug(String.format("   I := %s", resultIncosistentRank.getKnowledgeBase().getFormulas()));
        _logger.debug(String.format("   R := %s", resultRelevantRank.getKnowledgeBase().getFormulas()));
        _logger.debug(String.format("   R- := %s", resultIrrelevantRank.getKnowledgeBase().getFormulas()));

        return new ModelRelevanceResult(
                resultIncosistentRank,
                resultRelevantRank,
                resultIrrelevantRank,
                relevantKb,
                justificationCollection);
    }
}

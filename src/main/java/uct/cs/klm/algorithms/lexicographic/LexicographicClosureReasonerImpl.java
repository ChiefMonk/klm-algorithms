package uct.cs.klm.algorithms.lexicographic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
 * @since 2024-07-03
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
                    isQueryEntailed,
                    startTime);
        }

        ModelRankCollection relevantRanking = baseRank.getRanking().getRankingCollectonExceptInfinity();
        ModelRank nonRelevantRanking = baseRank.getRanking().getInfinityRank();

        List<KnowledgeBase> relevantPowersets = ReasonerUtils.toPowerSetOrdered(relevantRanking);
        relevantPowersets.add(baseRank.getRanking().getInfinityRank().getFormulas());

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
                isQueryEntailed,
                startTime);

    }

    private ModelEntailment CreateResponse(
            ModelBaseRank baseRank,
            PlFormula queryFormula,
            KnowledgeBase materialisedKb,
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

        var finalTime = ReasonerUtils.ToTimeDifference(startTime, System.nanoTime());

        return new ModelLexicographicEntailment.ModelLexicographicEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
                .withRemovedRanking(removedRanking)
                .withRemainingRanking(remainingRanking)
                .withWeakenedRanking(new ModelRankCollection())
                .withEntailmentKnowledgeBase(remainingRanking.getKnowledgeBase())
                .withEntailed(isQueryEntailed)
                .withTimeTaken(finalTime)
                .build();
    }

    public ModelEntailment getEntailment9(ModelBaseRank baseRank, PlFormula queryFormula) {
        long startTime = System.nanoTime();
        _logger.debug("==> Lexicographic Closure Entailment");

        // Compute the negation of the antecedent of the query.
        PlFormula negationOfAntecedent = new Negation(((Implication) queryFormula).getFirstFormula());
        PlFormula materialisedQueryFormula = ReasonerUtils.toMaterialisedFormula(queryFormula);
        boolean isQueryEntailed = false;

        _logger.debug("-> BaseRank");
        for (ModelRank rank : baseRank.getRanking()) {
            _logger.debug("   {}:{}", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas());
        }
        _logger.debug("-> Query, \u0391: {}", queryFormula);
        _logger.debug("-> Antecedent Negation of \u0391: {}", negationOfAntecedent);

        ModelRankCollection otherRankCollection = new ModelRankCollection();
        ModelRank infinityRank = new ModelRank();

        for (ModelRank rank : baseRank.getRanking()) {
            if (rank.getRankNumber() == Symbols.INFINITY_RANK_NUMBER) {
                infinityRank = rank;
            } else {
                otherRankCollection.add(rank);
            }
        }

        otherRankCollection.sort(Comparator.comparingInt(ModelRank::getRankNumber));

        ModelRankCollection removedRanking = new ModelRankCollection();
        ModelRankCollection weakenedRanking = new ModelRankCollection();
        ModelRankCollection miniBaseRanking = new ModelRankCollection();
        ModelRankCollection remainingRanking = new ModelRankCollection();

        ModelRankCollection higherRanks = new ModelRankCollection();
        List<List<PlFormula>> previousPowersets = new ArrayList<>();

        KnowledgeBase materialisedKB = new KnowledgeBase();

        ModelRankCollection allRankCollection = new ModelRankCollection(otherRankCollection.clone());

        boolean stopNow = false;
        for (ModelRank currentRank : allRankCollection) {
            if (stopNow) {
                break;
            }

            if (currentRank.isEmpty()) {
                continue;
            }
            int rankNumber = currentRank.getRankNumber();
            DisplayUtils.LogDebug(_logger, String.format("Rank := %s: %s", rankNumber, currentRank.getFormulas()));

            previousPowersets = previousPowersets.stream().distinct().collect(Collectors.toList());
            Map.Entry<ModelRankCollection, List<List<PlFormula>>> powersetTurple = ReasonerUtils.powersetIterative(otherRankCollection, rankNumber, previousPowersets);

            higherRanks = powersetTurple.getKey();
            var currentPowerset = powersetTurple.getValue();

            int counter = 0;
            for (var ff : currentPowerset) {
                DisplayUtils.LogDebug(_logger, String.format("PowerSet := %s-%s: %s", rankNumber, counter, ff));
                counter++;
            }

            previousPowersets = ReasonerUtils.AddToList(previousPowersets, currentPowerset);

            for (var powerset : currentPowerset) {

                DisplayUtils.LogDebug(_logger, String.format("PowerSet := %s: %s", rankNumber, powerset));

                //DisplayUtils.LogDebug(_logger, String.format("Current KB %s: %s\n %s\n %s", rankNumber, infinityRank, higherRanks, powerset)); 
                // Materialise the knowledge base from the current ranking collection.
                var currentKb = ReasonerUtils.toKnowledgeBase(infinityRank, higherRanks, powerset, null);
                materialisedKB = ReasonerUtils.toMaterialisedKnowledgeBase(currentKb);

                previousPowersets.add(ReasonerUtils.toFormulaList(currentKb));
                previousPowersets.add(powerset);

                DisplayUtils.LogDebug(_logger, String.format("MaterialisedKB := %s: %s", rankNumber, materialisedKB));

                boolean isNegationEntailed = _reasoner.query(materialisedKB, negationOfAntecedent);
                if (isNegationEntailed) {
                    _logger.debug("  YES-NegationOfAntecedent:Entailed; We continue to NEXT powersetEntry");
                } else {
                    _logger.debug("  NOT-NegationOfAntecedent:Entailed; We checking if materialisedKB entails query");
                    isQueryEntailed = _reasoner.query(materialisedKB, materialisedQueryFormula);
                    stopNow = true;
                    if (isQueryEntailed) {
                        _logger.debug("  YES-Query:Entailed; We STOP and EXIT");
                    } else {
                        _logger.debug("  NO-Query:Entailed; We continue to NEXT powersetEntry");
                    }

                }

                if (stopNow) {
                    break;
                }
            }

            if (stopNow) {
                break;
            }
        }

        if (isQueryEntailed) {
            remainingRanking = ReasonerUtils.toRanksFromKnowledgeBase(baseRank, materialisedKB, false);
            removedRanking = ReasonerUtils.toRanksFromKnowledgeBase(baseRank, remainingRanking.getKnowledgeBase(), true);
        }

        if (!isQueryEntailed) {
            isQueryEntailed = doesInfinityRankEntailQuery(infinityRank, queryFormula);

            if (isQueryEntailed) {
                remainingRanking = new ModelRankCollection(infinityRank);
                removedRanking = baseRank.getRanking().getRankingCollectonExcept(Symbols.INFINITY_RANK_NUMBER);
            }
        }

        var finalTime = ReasonerUtils.ToTimeDifference(startTime, System.nanoTime());

        return new ModelLexicographicEntailment.ModelLexicographicEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
                //.withMiniBaseRanking(miniBaseRanking)
                .withRemovedRanking(removedRanking)
                .withRemainingRanking(remainingRanking)
                .withWeakenedRanking(weakenedRanking)
                .withEntailmentKnowledgeBase(remainingRanking.getKnowledgeBase())
                .withEntailed(isQueryEntailed)
                .withTimeTaken(finalTime)
                .build();
    }
}

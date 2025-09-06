package uct.cs.klm.algorithms.relevant;

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
import uct.cs.klm.algorithms.enums.ReasonerType;
import uct.cs.klm.algorithms.explanation.IJustificationService;

import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.models.ModelEntailment;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.models.ModelRelevantClosureEntailment;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.services.KlmReasonerBase;
import uct.cs.klm.algorithms.utils.DisplayUtils;
import uct.cs.klm.algorithms.utils.ReasonerFactory;
import uct.cs.klm.algorithms.utils.ReasonerUtils;
import uct.cs.klm.algorithms.utils.Symbols;

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

        System.out.println(String.format("->Query: %s", queryFormula));
        System.out.println(String.format("->Query Antecedent Negation: %s", negationOfAntecedent));

        var materialisedKB = ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection);

        ModelRankCollection removedRanking = new ModelRankCollection();
        ModelRankCollection remainingRanking = baseRankCollection;
        ModelRankCollection relevantRanking = new ModelRankCollection();
        ModelRankCollection irrelevantRanking = baseRankCollection;

        var isQueryEntailed = false;
        boolean continueProcessing = true;

        boolean isNegationEntailed = _reasoner.query(materialisedKB, negationOfAntecedent);
        if (isNegationEntailed) {
            _logger.debug("  YES-NegationOfAntecedent:Entailed; We continue to NEXT powersetEntry");
        } else {
            _logger.debug("  NOT-NegationOfAntecedent:Entailed; We checking if materialisedKB entails query");
            isQueryEntailed = _reasoner.query(materialisedKB, materialisedQueryFormula);

            if (isQueryEntailed) {
                continueProcessing = false;
                _logger.debug(" => YES: We STOP and EXIT");
            } else {
                _logger.debug(" => NO: We Continue");
            }
        }

        DisplayUtils.LogDebug(_logger, String.format("Checking if Query %s is entailed by KB = %s", materialisedQueryFormula, materialisedKB));

        if (isQueryEntailed) {
            _logger.debug(" => YES: We STOP and EXIT");
        } else {

            var relevanceResult = GetRelevantRanks(reasonerType, baseRankCollection, negationOfAntecedent);
            relevantRanking = relevanceResult.getCorrectRelevantRanking();
            irrelevantRanking = relevanceResult.getCorrectIrrelevantRanking();
            var irrelevantKb = irrelevantRanking.getKnowledgeBase();

            continueProcessing = !relevantRanking.isEmpty();

            _logger.debug("R+All: {}", relevanceResult.getRelevantRanking().getKnowledgeBase());
            _logger.debug("R+: {}", relevantRanking.getKnowledgeBase());

            System.out.println("->R+ Ranking");
            for (ModelRank rank : relevantRanking) {
                System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
            }

            _logger.debug("R-All: {}", relevanceResult.getIrrelevantRanking().getKnowledgeBase());
            _logger.debug("R-: {}", irrelevantRanking.getKnowledgeBase());

            System.out.println("->R- Ranking");
            for (ModelRank rank : irrelevantRanking) {
                System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
            }

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
            ModelRankCollection allRankCollection = new ModelRankCollection(otherRankCollection.clone());

            List<List<PlFormula>> previousPowersets = new ArrayList<>();

            ModelRankCollection higherRanks = new ModelRankCollection();

            while (continueProcessing) {
                for (ModelRank currentRank : allRankCollection) {

                    if (!continueProcessing) {
                        break;
                    }

                    int rankNumber = currentRank.getRankNumber();
                    var relevantRank = relevantRanking.getRank(rankNumber);                  
                   
                    if (relevantRank == null || relevantRank.isEmpty()) {
                        continue;
                    }

                    if (currentRank == null || currentRank.isEmpty()) {
                        continue;
                    }
                    
                    var powerKnowledgeBase = ReasonerUtils.removeFormulasFromKnowledgeBase(
                            relevantRanking.getKnowledgeBase(),
                            relevantRank.getFormulas());

                    DisplayUtils.LogDebug(_logger, String.format("Current Rank := %s: %s", rankNumber, currentRank.getFormulas()));
                    DisplayUtils.LogDebug(_logger, String.format("Relevant Rank := %s: %s", rankNumber, relevantRank.getFormulas()));

                    previousPowersets = previousPowersets.stream().distinct().collect(Collectors.toList());

                    Map.Entry<ModelRankCollection, List<List<PlFormula>>> powersetTurple = ReasonerUtils.powersetIterative(relevantRanking, rankNumber, previousPowersets);

                    higherRanks = powersetTurple.getKey();
                    var currentPowerset = powersetTurple.getValue();

                    DisplayUtils.LogDebug(_logger, String.format("HigherRanks := %s: %s", rankNumber, higherRanks.getKnowledgeBase().getFormulas()));

                    int counter = 0;
                    for (var ff : currentPowerset) {
                        DisplayUtils.LogDebug(_logger, String.format("Current Powerset := %s-%s: %s", rankNumber, counter, ff));
                        counter++;
                    }

                    /*
                    counter = 0;
                    for (var ff : previousPowersets) {
                        DisplayUtils.LogDebug(_logger, String.format("Previous Powersets := %s-%s: %s", rankNumber, counter, ff));
                        counter++;
                    }
                     */
                    currentPowerset.add(new ArrayList<>());  
                    
                    for (var powerset : currentPowerset) {

                        DisplayUtils.LogDebug(_logger, String.format("=> Current Powerset Set Before := %s: %s", rankNumber, powerset));

                        if (!powerset.isEmpty()) {
                            previousPowersets.add(powerset);

                            var powerKb = new KnowledgeBase(powerKnowledgeBase);
                            powerKb.addAll(powerset);

                            previousPowersets.add(ReasonerUtils.toFormulaList(powerKb));
                            DisplayUtils.LogDebug(_logger, String.format("=> Current Combined Powerset := %s: %s", rankNumber, powerKb));
                        }

                        DisplayUtils.LogDebug(_logger, String.format("Current Powerset Set After := %s: %s", rankNumber, powerset));

                        // Materialise the knowledge base from the current ranking collection.
                        var currentKb = ReasonerUtils.toKnowledgeBase(infinityRank, higherRanks, powerset, irrelevantKb);
                        materialisedKB = ReasonerUtils.toMaterialisedKnowledgeBase(currentKb);

                        DisplayUtils.LogDebug(_logger, String.format("=> Materialised KB := %s: %s", rankNumber, materialisedKB));

                        isNegationEntailed = _reasoner.query(materialisedKB, negationOfAntecedent);
                        if (isNegationEntailed) {
                            DisplayUtils.LogDebug(_logger, String.format("=> YES-NegationOfAntecedent:Entailed; We discard and move NEXT: %s", powerset));                           
                        } else {
                            _logger.debug("  NOT-NegationOfAntecedent:Entailed; We checking if materialisedKB entails query");
                            isQueryEntailed = _reasoner.query(materialisedKB, materialisedQueryFormula);
                            continueProcessing = false;
                            if (isQueryEntailed) {
                                _logger.debug(" => YES: We STOP and EXIT");
                            } else {
                                _logger.debug(" => NO: We Continue");
                            }
                        }

                        if (!continueProcessing) {
                            break;
                        }
                    }

                    if (!continueProcessing) {
                        break;
                    }

                }

                if (!continueProcessing) {
                    break;
                }
                
                 _logger.debug("=> FINAL IsQueryEntailed Check with irrelevantKb");
                materialisedKB = ReasonerUtils.toMaterialisedKnowledgeBase(irrelevantKb);
                isQueryEntailed = _reasoner.query(materialisedKB, materialisedQueryFormula);
                continueProcessing = false;
            }

        }

        KnowledgeBase entailmentKb = new KnowledgeBase();
        String hasEntailed = "NO";
        if (isQueryEntailed) {
            entailmentKb = materialisedKB;
            hasEntailed = "YES";
        }

        var finalTime = ReasonerUtils.ToTimeDifference(startTime, System.nanoTime());

        if (_logger.isDebugEnabled()) {
            _logger.debug("Finally checking if {} is entailed by {}", materialisedQueryFormula, materialisedKB);
            _logger.debug("Is Entailed: {} in {}", hasEntailed, finalTime);
        }

        long endTime = System.nanoTime();

        return new ModelRelevantClosureEntailment.ModelRelevantClosureEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
                .withRemovedRanking(new ModelRankCollection())
                .withRemainingRanking(baseRankCollection)
                .withRelevantRankCollection(relevantRanking)
                .withIrrelevantRankCollection(irrelevantRanking)
                .withEntailmentKnowledgeBase(entailmentKb)
                .withEntailed(isQueryEntailed)
                .withTimeTaken((endTime - startTime) / 1_000_000_000.0)
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

        for (var justificationEntry : justificationCollection) {
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

                    if (rank.getFormulas().contains(deMaterialised)) {
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

            System.out.println(String.format("   Mini := %s", miniKb.getFormulas()));
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

        System.out.println(String.format("   I := %s", resultIncosistentRank.getKnowledgeBase().getFormulas()));
        System.out.println(String.format("   R := %s", resultRelevantRank.getKnowledgeBase().getFormulas()));
        System.out.println(String.format("   R- := %s", resultIrrelevantRank.getKnowledgeBase().getFormulas()));

        return new ModelRelevanceResult(resultIncosistentRank, resultRelevantRank, resultIrrelevantRank);
    }
}

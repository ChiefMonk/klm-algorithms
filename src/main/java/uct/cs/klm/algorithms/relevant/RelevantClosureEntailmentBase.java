package uct.cs.klm.algorithms.relevant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
            ModelBaseRank baseRank,
            PlFormula queryFormula) {

        long startTime = System.nanoTime();

        System.out.println("==>Relevant Closure Entailment");

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

            var relevanceResult = GetRelevantRanks(baseRankCollection, negationOfAntecedent);
            relevantRanking = relevanceResult.getCorrectRelevantRanking();
            irrelevantRanking = relevanceResult.getCorrectIrrelevantRanking();

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
            KnowledgeBase previousIrrelevantRankFormalas = new KnowledgeBase();

            ModelRankCollection higherRanks = new ModelRankCollection();

            while (continueProcessing) {
                for (ModelRank currentRank : allRankCollection) {

                    if (!continueProcessing) {
                        break;
                    }

                    int rankNumber = currentRank.getRankNumber();
                    var relevantRank = relevantRanking.getRank(rankNumber);
                    var irrelevantRank = ReasonerUtils.removeFormulasFromRank(currentRank, relevantRank.getFormulas());

                    previousIrrelevantRankFormalas.addAll(irrelevantRank.getFormulas());

                    if (currentRank.isEmpty() || relevantRank.isEmpty()) {
                        continue;
                    }

                    DisplayUtils.LogDebug(_logger, String.format("Current Rank := %s: %s", rankNumber, currentRank.getFormulas()));
                    DisplayUtils.LogDebug(_logger, String.format("Relevant Rank := %s: %s", rankNumber, relevantRank.getFormulas()));
                    DisplayUtils.LogDebug(_logger, String.format("Irrelevant Rank := %s: %s", rankNumber, irrelevantRank.getFormulas()));
                    DisplayUtils.LogDebug(_logger, String.format("All Irrelevant Formalas. := %s: %s", rankNumber, previousIrrelevantRankFormalas.getFormulas()));

                    Map.Entry<ModelRankCollection, List<List<PlFormula>>> powersetTurple = ReasonerUtils.powersetIterative(relevantRanking, rankNumber, previousPowersets);

                    higherRanks = powersetTurple.getKey();
                    var currentPowerset = powersetTurple.getValue();

                    int counter = 0;
                    for (var ff : currentPowerset) {
                        DisplayUtils.LogDebug(_logger, String.format("PowerSet := %s-%s: %s", rankNumber, counter, ff));
                        counter++;
                    }

                    ReasonerUtils.AddToList(previousPowersets, currentPowerset);

                    for (var powerset : currentPowerset) {

                        DisplayUtils.LogDebug(_logger, String.format("Current Set Before := %s: %s", rankNumber, powerset));
                        powerset.addAll(previousIrrelevantRankFormalas.getFormulas());
                        DisplayUtils.LogDebug(_logger, String.format("Current Set After := %s: %s", rankNumber, powerset));

                        // Materialise the knowledge base from the current ranking collection.
                        var currentKb = ReasonerUtils.toKnowledgeBase(infinityRank, higherRanks, powerset);
                        materialisedKB = ReasonerUtils.toMaterialisedKnowledgeBase(currentKb);

                        previousPowersets.add(ReasonerUtils.toFormulaList(currentKb));

                        DisplayUtils.LogDebug(_logger, String.format("MaterialisedKB := %s: %s", rankNumber, materialisedKB));

                        isNegationEntailed = _reasoner.query(materialisedKB, negationOfAntecedent);
                        if (isNegationEntailed) {
                            _logger.debug("  YES-NegationOfAntecedent:Entailed; We continue to NEXT powersetEntry");
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
                .withEntailed(false)
                .withTimeTaken((endTime - startTime) / 1_000_000_000.0)
                .build();
    }

    private ModelRelevanceResult GetRelevantRanks(
            ModelRankCollection baseRankCollection,
            PlFormula negationOfAntecedent) {

        var originalKb = baseRankCollection.getKnowledgeBase();

        IJustificationService justification = ReasonerFactory.createJustification(ReasonerType.BasicRelevantClosure);
        var justificationCollection = justification.computeJustification(originalKb, negationOfAntecedent);

        KnowledgeBase relevantKb = new KnowledgeBase();

        for (var justificationEntry : justificationCollection) {
            for (var formula : justificationEntry) {

                var formulaMaterialised = ReasonerUtils.toMaterialisedFormula(formula);

                if (!relevantKb.contains(formulaMaterialised)) {
                    relevantKb.add(formulaMaterialised);
                }
            }
        }

        ModelRankCollection resultRelevantRank = new ModelRankCollection();
        ModelRankCollection resultIrrelevantRank = new ModelRankCollection();

        for (var currentRank : baseRankCollection) {

            var rankNumber = currentRank.getRankNumber();

            var irrelevantRank = new ModelRank(rankNumber);
            boolean addIrrelevantRank = false;

            var relevantRank = new ModelRank(rankNumber);
            boolean addRelevantRank = false;

            for (var formula : currentRank.getFormulas()) {

                var formulaMaterialised = ReasonerUtils.toMaterialisedFormula(formula);

                if (relevantKb.contains(formulaMaterialised)) {
                    addRelevantRank = true;
                    relevantRank.addFormula(formula);
                } else {
                    addIrrelevantRank = true;
                    irrelevantRank.addFormula(formula);
                }

            }

            if (addRelevantRank) {
                resultRelevantRank.add(relevantRank);
            }

            if (addIrrelevantRank) {
                resultIrrelevantRank.add(irrelevantRank);
            }
        }

        System.out.println(String.format("   R := %s", resultRelevantRank.getKnowledgeBase().getFormulas()));
        System.out.println(String.format("   R- := %s", resultIrrelevantRank.getKnowledgeBase().getFormulas()));

        return new ModelRelevanceResult(resultRelevantRank, resultIrrelevantRank);
    }

    //@Override
    public ModelEntailment getEntailment3(
            ModelBaseRank baseRank,
            PlFormula queryFormula) {

        long startTime = System.nanoTime();

        System.out.println("==>Relevant Closure Entailment");

        // get the negation of the antecedent of the query
        PlFormula negationOfAntecedent = new Negation(((Implication) queryFormula).getFirstFormula());

        ModelRankCollection baseRankCollection = new ModelRankCollection(baseRank.getRanking().clone());
        Collections.sort(baseRankCollection, (o1, o2) -> Integer.compare(o1.getRankNumber(), o2.getRankNumber()));

        System.out.println("->BaseRank");
        for (ModelRank rank : baseRankCollection) {
            System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

        System.out.println(String.format("->Query, \u0391: %s", queryFormula));
        System.out.println(String.format("->Antecedent Negation of \u0391: %s", negationOfAntecedent));

        var relevantFormulas = GetRelevantRanks(baseRankCollection, negationOfAntecedent);

        var removedRanking = new ModelRankCollection();
        var weakenedRanking = new ModelRankCollection();
        var miniBaseRanking = new ModelRankCollection();

        int stepNumber = 1;
        while (true) {

            if (baseRankCollection.isEmpty()) {
                break;
            }

            var currentRank = baseRankCollection.get(0);

            var materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection);

            System.out.println();
            System.out.println(String.format("->Checking Entailment Step %s", stepNumber++));

            // if baseRank has only the infinity rank, then we stop processing
            if (currentRank.getRankNumber() == Symbols.INFINITY_RANK_NUMBER) {
                System.out.println("ONLY ∞ rank remains, so we stop processing with current K = " + materialisedKnowledgeBase);
                break;
            }

            System.out.println("  Current BaseRank");
            for (ModelRank rank : baseRankCollection) {
                System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
            }

            System.out.println(String.format("  Current K: %s", materialisedKnowledgeBase));
            System.out.println(String.format("  Checking if %s is entailed by current K", negationOfAntecedent));

            boolean isNegationOfAntecedentEntailed = _reasoner.query(materialisedKnowledgeBase, negationOfAntecedent);

            if (!isNegationOfAntecedentEntailed) {
                System.out.println("  NO its not, so we stop rank removal process with current K = " + materialisedKnowledgeBase);
                break;
            }

            System.out.println(String.format("  YES it is, so we process mini-ranks within rank %s: %s", currentRank.getRankNumber(), currentRank.getFormulas()));

            var currentMiniRankCollection = ReasonerUtils.generateFormulaCombinations(currentRank, true);
            currentMiniRankCollection.sort((a, b) -> Integer.compare(a.getFormulas().size(), b.getFormulas().size()));

            for (ModelRank mini : currentMiniRankCollection) {
                mini.setRankNumber(currentRank.getRankNumber());
                miniBaseRanking.add(mini);
            }

            // remove the current rank from baserank
            baseRankCollection.remove(0);

            boolean stopProcessing = false;
            int miniStepNumber = 1;

            for (ModelRank currentMiniRank : currentMiniRankCollection) {

                if (currentMiniRank.isEmpty() || currentMiniRank.getFormulas().size() == currentRank.getFormulas().size()) {
                    continue;
                }

                var tempMiniBaseRank = new ModelRankCollection(baseRankCollection);

                currentMiniRank.setRankNumber(currentRank.getRankNumber());
                tempMiniBaseRank.add(currentMiniRank);

                Collections.sort(tempMiniBaseRank, (o1, o2) -> Integer.compare(o2.getRankNumber(), o1.getRankNumber()));

                System.out.println("  Current Temp MiniBaseRank");
                for (ModelRank rank : tempMiniBaseRank) {
                    System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
                }

                materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(tempMiniBaseRank);

                System.out.println(String.format("  Current K: %s", materialisedKnowledgeBase));
                System.out.println(String.format("  Checking if %s is entailed by current K", negationOfAntecedent));

                isNegationOfAntecedentEntailed = _reasoner.query(materialisedKnowledgeBase, negationOfAntecedent);

                if (!isNegationOfAntecedentEntailed) {
                    stopProcessing = true;

                    ModelRank remainingFormulaRank = new ModelRank(currentRank.getRankNumber(), currentMiniRank.getFormulas());
                    baseRankCollection.add(remainingFormulaRank);

                    weakenedRanking.add(currentRank);

                    var removedMiniRankFormalas = ReasonerUtils.removeFormulasFromKnowledgeBase(currentRank.getFormulas(), currentMiniRank.getFormulas());

                    if (!removedMiniRankFormalas.isEmpty()) {
                        removedRanking.add(new ModelRank(currentRank.getRankNumber(), removedMiniRankFormalas));
                    }

                    System.out.println("  NO its not, so we stop rank removal process with current K = " + ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection));
                    break;
                }

                System.out.println(String.format("  YES it is, so we ignore the mini rank %s: %s", miniStepNumber, currentMiniRank.getFormulas()));

            }

            if (stopProcessing) {
                break;
            }

            removedRanking.add(currentRank);
            weakenedRanking.add(currentRank);
        }

        Collections.sort(baseRankCollection, (o1, o2) -> Integer.compare(o2.getRankNumber(), o1.getRankNumber()));
        Collections.sort(removedRanking, (o1, o2) -> Integer.compare(o2.getRankNumber(), o1.getRankNumber()));
        Collections.sort(weakenedRanking, (o1, o2) -> Integer.compare(o2.getRankNumber(), o1.getRankNumber()));

        System.out.println();
        System.out.println("->Remaining Ranks:");
        for (ModelRank rank : baseRankCollection) {
            System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

        System.out.println("->Removed Ranks:");
        for (ModelRank rank : removedRanking) {
            System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

        var materialisedQueryFormula = ReasonerUtils.toMaterialisedFormula(queryFormula);
        var materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection);

        boolean isQueryEntailed = !baseRankCollection.isEmpty() && _reasoner.query(materialisedKnowledgeBase, materialisedQueryFormula);
        KnowledgeBase entailmentKb = new KnowledgeBase();

        var hasEntailed = "NO";
        if (isQueryEntailed) {
            entailmentKb = ReasonerUtils.toKnowledgeBase(baseRankCollection);
            hasEntailed = "YES";
        }

        System.out.println(String.format("Finally checking if %s is entailed by %s", materialisedQueryFormula, materialisedKnowledgeBase));
        System.out.println(String.format("Is Entailed: %s", hasEntailed));

        long endTime = System.nanoTime();

        System.out.println();
        System.out.println("->Original BaseRank Ranks:");
        for (ModelRank rank : baseRank.getRanking()) {
            System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

        return new ModelRelevantClosureEntailment.ModelRelevantClosureEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
                .withRemovedRanking(removedRanking)
                .withRemainingRanking(baseRankCollection)
                .withRelevantRankCollection(weakenedRanking)
                .withIrrelevantRankCollection(weakenedRanking)
                .withEntailmentKnowledgeBase(entailmentKb)
                .withEntailed(isQueryEntailed)
                .withTimeTaken((endTime - startTime) / 1_000_000_000.0)
                .build();

    }

    private ModelRankCollection GetRelevantRanks2(
            ModelRankCollection baseRankCollection,
            PlFormula negationOfAntecedent) {

        ArrayList<ModelRank> justificationList = new ArrayList<>();

        var formulaCombinationCollection = ReasonerUtils.generateFormulaCombinations(baseRankCollection, false);

        for (ModelRank rank : formulaCombinationCollection) {

            if (rank.isEmpty()) {
                continue;
            }

            var materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(rank);

            System.out.println(String.format("Does %s entail %s", materialisedKnowledgeBase, negationOfAntecedent));

            boolean isNegationOfAntecedentEntailed = _reasoner.query(materialisedKnowledgeBase, negationOfAntecedent);

            if (!isNegationOfAntecedentEntailed) {
                System.out.println("  NO its not, so we ignore it");
                continue;
            }

            System.out.println(String.format("  YES it is, so we include it to the list: %s", rank.getFormulas()));

            justificationList.add(rank);
        }

        System.out.println();
        System.out.println("->Justification List:");
        for (ModelRank rank : justificationList) {
            System.out.println(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

        IJustificationService justification = ReasonerFactory.createJustification(ReasonerType.BasicRelevantClosure);
        var justificationCollection = justification.computeJustification(baseRankCollection.getKnowledgeBase(), negationOfAntecedent);

        KnowledgeBase kb = new KnowledgeBase();

        for (var justificationEntry : justificationCollection) {

            for (var formula : justificationEntry) {

                if (!kb.contains(formula)) {
                    kb.add(formula);
                }

            }
        }

        return new ModelRankCollection(justificationList);
    }

}

package uct.cs.klm.algorithms.ranking;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.utils.DisplayUtils;
import uct.cs.klm.algorithms.utils.ReasonerUtils;
import uct.cs.klm.algorithms.utils.Symbols;

/**
 *
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 *
 * Key optimizations: - Reduced logging (remove or gate debug prints for
 * production). - Use of stream collectors to partition formulas. - Pre-caching
 * exceptional formulas in a HashSet for fast membership tests. - Submission of
 * parallel tasks to a custom ForkJoinPool.
 */
public final class BaseRankService implements IBaseRankService {

    private static final Logger _logger = LoggerFactory.getLogger(BaseRankService.class);

    // the SatReasoner holder
    private final SatReasoner _satReasoner;

    // Use a custom thread pool with optimal size
    private final ForkJoinPool _customThreadPool;

    public BaseRankService() {
        SatSolver.setDefaultSolver(new Sat4jSolver());
        _satReasoner = new SatReasoner();

        // Use number of available processors for optimal parallel processing
        int processors = Runtime.getRuntime().availableProcessors();
        _customThreadPool = new ForkJoinPool(processors);
    }

    @Override
    public ModelBaseRank construct(KnowledgeBase knowledgeBase) {

        // Start time
        var startTime = System.nanoTime();

        DisplayUtils.LogDebug(_logger, "==>BaseRank Algorithm");

        // Separate defeasible and classical statements            
        var defeasibleStatements = knowledgeBase.getDefeasibleFormulas();
        var classicalStatements = knowledgeBase.getClassicalFormulas();

        if (_logger.isDebugEnabled()) {
            _logger.debug(String.format("Defeasible Statements := %s", defeasibleStatements));
            _logger.debug(String.format("Classical Statements := %s", classicalStatements));
            _logger.debug(String.format("Signature := %s", knowledgeBase.getSignature()));
        }

        // ranking and exceptionality sequence
        var baseRanking = new ModelRankCollection();
        var sequence = new ModelRankCollection();

        var currentKnowledgeBase = defeasibleStatements;
        var previousKnowledgeBase = new KnowledgeBase();

        int rankNumber = 0;

        // Loop until the defeasible knowledge base stabilizes.
        while (!previousKnowledgeBase.equals(currentKnowledgeBase)) {

            previousKnowledgeBase = currentKnowledgeBase;
            currentKnowledgeBase = new KnowledgeBase();

            if (_logger.isDebugEnabled()) {
                _logger.debug(String.format("Previous := %s", previousKnowledgeBase));
                _logger.debug(String.format("Current := %s", currentKnowledgeBase));
            }

            // Compute the exceptional statements for this iteration.
            var exceptionalStatements = getExceptionalStatements(
                    previousKnowledgeBase,
                    classicalStatements,
                    rankNumber);

            // Partition the formulas into those that are exceptional (to be used in the next KB)
            // and those that are not (which form the current rank).
            ModelRank currentRank = constructRank(
                    rankNumber,
                    previousKnowledgeBase,
                    currentKnowledgeBase,
                    exceptionalStatements);

            if (!currentRank.isEmpty()) {
                baseRanking.add(currentRank);
            }
            sequence.addRank(
                    previousKnowledgeBase.equals(currentKnowledgeBase)
                    ? Symbols.INFINITY_RANK_NUMBER
                    : rankNumber, previousKnowledgeBase);
            rankNumber++;
        }

        // Assign all classical and (remaining) defeasible formulas to the INFINITY rank.
        baseRanking.addRank(
                Symbols.INFINITY_RANK_NUMBER,
                ReasonerUtils.toCombinedKnowledgeBases(classicalStatements, currentKnowledgeBase));

        var finalTime = ReasonerUtils.ToTimeDifference(startTime, System.nanoTime());

        DisplayUtils.LogDebug(_logger, String.format("FINAL BaseRank := %s\n%s", finalTime, baseRanking));

        return new ModelBaseRank(
                knowledgeBase,
                sequence,
                baseRanking,
                finalTime);
    }

    private KnowledgeBase getExceptionalStatements(
            KnowledgeBase defeasible,
            KnowledgeBase classical,
            int rankNumber) {

        DisplayUtils.LogDebug(_logger, String.format("=>Get Exceptional Statements_%s", rankNumber));

        // Combine defeasible and classical knowledge bases.
        var combinedKb = ReasonerUtils.toCombinedKnowledgeBases(defeasible, classical);
        var materialisedKb = ReasonerUtils.toMaterialisedKnowledgeBase(combinedKb);
        var antecedents = ReasonerUtils.getAntecedentFormulas(defeasible);

        if (_logger.isDebugEnabled()) {
            _logger.debug(String.format("  KnowledgeBase_%s := %s", rankNumber, materialisedKb));
            _logger.debug(String.format("  Signature_%s := %s", rankNumber, materialisedKb.getSignature()));
            _logger.debug(String.format("  Antecedents_%s := %s", rankNumber, antecedents));
        }

        // Use the custom thread pool and stream filtering to determine exceptionals.
        try {
            Set<PlFormula> exceptionalSet = _customThreadPool.submit(()
                    -> antecedents.parallelStream()
                            .filter(antecedent -> _satReasoner.query(materialisedKb, new Negation(antecedent)))
                            .collect(Collectors.toSet())
            ).get();

            KnowledgeBase exceptionals = new KnowledgeBase();
            exceptionals.addAll(exceptionalSet);

            DisplayUtils.LogDebug(_logger, String.format("  Exceptionals_%s := %s", rankNumber, exceptionals.toString()));

            return exceptionals;
        } catch (InterruptedException | ExecutionException ex) {
            _logger.error("Error in getExceptionalStatements", ex);
            throw new RuntimeException("Error in getExceptionalStatements", ex);
        }
    }

    private ModelRank constructRank(
            int rankNumber,
            KnowledgeBase previousKnowledgeBase,
            KnowledgeBase nextKnowledgeBase,
            KnowledgeBase exceptionals) {

        // Cache exceptionals in a plain HashSet for fast lookups.
        final Set<PlFormula> exceptionalCache = new HashSet<>(exceptionals);

        try {
            // Partition the formulas: those whose antecedent is in exceptionals
            // (they go to nextKnowledgeBase) and the rest (they form the current rank).
            Map<Boolean, Set<PlFormula>> partitioned = _customThreadPool.submit(()
                    -> previousKnowledgeBase.parallelStream().collect(
                            Collectors.partitioningBy(
                                    formula -> {
                                        // Cast is safe if formulas in defeasible KB are always implications.
                                        PlFormula antecedent = ((Implication) formula).getFormulas().getFirst();
                                        return exceptionalCache.contains(antecedent);
                                    },
                                    Collectors.toSet()
                            )
                    )
            ).get();

            // Add formulas that will continue to the next round.
            nextKnowledgeBase.addAll(partitioned.get(true));

            ModelRank currentRank = new ModelRank(rankNumber);
            // Formulas not exceptional become part of the current rank.
            partitioned.get(false).forEach(currentRank::addFormula);

            return currentRank;
        } catch (InterruptedException | ExecutionException ex) {
            _logger.error("Error in constructRank", ex);
            throw new RuntimeException("Error in constructRank", ex);
        }
    }

    // Clean up resources on close
    public void close() {
        if (_customThreadPool != null) {
            _customThreadPool.shutdown();
        }
    }
}

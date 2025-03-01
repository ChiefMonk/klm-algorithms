package uct.cs.klm.algorithms.rational;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.models.ModelEntailment;
import uct.cs.klm.algorithms.models.ModelRationalClosureEntailment;
import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;
import uct.cs.klm.algorithms.services.IReasonerService;
import uct.cs.klm.algorithms.services.KlmReasonerBase;
import uct.cs.klm.algorithms.utils.DisplayUtils;
import uct.cs.klm.algorithms.utils.ReasonerUtils;
import uct.cs.klm.algorithms.utils.Symbols;

public class RationalReasonerImpl extends KlmReasonerBase implements IReasonerService {

    private static final Logger _logger = LoggerFactory.getLogger(RationalReasonerImpl.class);
    
    public RationalReasonerImpl() {
        super();
    }
    
      

    @Override
    public ModelEntailment getEntailment(
            ModelBaseRank baseRank,
            PlFormula queryFormula) {

        long startTime = System.nanoTime();
                 
        DisplayUtils.LogDebug(_logger, "==> Rational Closure Entailment Algorithm");       
             
         // Get the negation of the antecedent from the query.
        PlFormula negationOfAntecedent = new Negation(((Implication) queryFormula).getFirstFormula());
        
         // Create a sorted copy (ascending) of the base ranking.
        ModelRankCollection baseRankCollection = new ModelRankCollection(baseRank.getRanking());
        baseRankCollection.sort(Comparator.comparingInt(ModelRank::getRankNumber));
               
        if (_logger.isDebugEnabled()) {            
            _logger.debug("->BaseRank");
            for (ModelRank rank : baseRankCollection) {
                _logger.debug(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
            }

            _logger.debug(String.format("->Query, α: %s", queryFormula));
            _logger.debug(String.format("->Antecedent Negation of α: %s", negationOfAntecedent));
        }

        // Instead of removing the first element repeatedly (which is inefficient on ArrayLists),
        // we determine an index (removalBoundary) up to which ranks should be removed.
        int removalBoundary = 0;
        int stepNumber = 1;
        
        while (removalBoundary < baseRankCollection.size()) {
            
            // Create a view of the remaining ranks.
            var currentRemaining = baseRankCollection.subList(removalBoundary, baseRankCollection.size());
            var materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(currentRemaining);
            
            ModelRank currentRank = currentRemaining.get(0);
            
            // Stop if the current rank is the infinity rank.
            if (currentRank.getRankNumber() == Symbols.INFINITY_RANK_NUMBER) {
                _logger.debug("  Because current rank is ∞; stopping with current K = {}", materialisedKnowledgeBase);
                break;
            }
                                 
           if (_logger.isDebugEnabled()) {
                _logger.debug("-> Checking Entailment Step {}", stepNumber++);
                _logger.debug("  Current BaseRank:");
                for (ModelRank rank : currentRemaining) {
                    _logger.debug("   {}:{}", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas());
                }
                _logger.debug("  Current K: {}", materialisedKnowledgeBase);
                _logger.debug("  Checking if {} is entailed by current K", negationOfAntecedent);
            }

            boolean isNegationEntailed = _reasoner.query(materialisedKnowledgeBase, negationOfAntecedent);
             
            if (!isNegationEntailed) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("  NO, it's not entailed; stopping rank removal with current K = {}", materialisedKnowledgeBase);
                }
                break;
            }
            
             // Stop if the current rank is the infinity rank.
            if (currentRank.getRankNumber() == Symbols.INFINITY_RANK_NUMBER) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("  YES, it is entailed but current rank is ∞; stopping with current K = {}", materialisedKnowledgeBase);
                }
                break;
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("  YES, it is entailed; marking rank {}: {} for removal", currentRank.getRankNumber(), currentRank.getFormulas());
            }
            removalBoundary++;       
        }

       // Partition the original collection into 'removed' and 'remaining' rankings.
        ModelRankCollection removedRanking = new ModelRankCollection();
        ModelRankCollection remainingRanking = new ModelRankCollection();
        for (int i = 0; i < baseRankCollection.size(); i++) {
            if (i < removalBoundary) {
                removedRanking.add(baseRankCollection.get(i));
            } else {
                remainingRanking.add(baseRankCollection.get(i));
            }
        }
       
       // Sort both collections in descending order.
        remainingRanking.sort((r1, r2) -> Integer.compare(r2.getRankNumber(), r1.getRankNumber()));
        removedRanking.sort((r1, r2) -> Integer.compare(r2.getRankNumber(), r1.getRankNumber()));
        
        if (_logger.isDebugEnabled()) {
            _logger.debug("-> Remaining Ranks:");
            for (ModelRank rank : remainingRanking) {
                _logger.debug("   {}:{}", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas());
            }
            _logger.debug("-> Removed Ranks:");
            for (ModelRank rank : removedRanking) {
                _logger.debug("   {}:{}", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas());
            }
        }

        // Prepare for the final query: materialise the query and the knowledge base.
        var materialisedQueryFormula = ReasonerUtils.toMaterialisedFormula(queryFormula);
        var finalMaterialisedKB = ReasonerUtils.toMaterialisedKnowledgeBase(remainingRanking);
        

        // The query is entailed only if the negation is not entailed and the query is entailed.
        boolean isQueryEntailed = _reasoner.query(finalMaterialisedKB, materialisedQueryFormula);
        
        
      KnowledgeBase entailmentKb = new KnowledgeBase();
        String hasEntailed = "NO";
        if (isQueryEntailed) {
            entailmentKb = ReasonerUtils.toKnowledgeBase(remainingRanking);
            hasEntailed = "YES";
        }
        
        var finalTime = ReasonerUtils.ToTimeDifference(startTime, System.nanoTime());
        
        if (_logger.isDebugEnabled()) {
            _logger.debug("Finally checking if {} is entailed by {}", materialisedQueryFormula, finalMaterialisedKB);
            _logger.debug("Is Entailed: {} in {}", hasEntailed, finalTime);
        }                   

        return new ModelRationalClosureEntailment.RationalEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
                .withRemovedRanking(removedRanking)
                .withRemainingRanking(remainingRanking)
                .withEntailmentKnowledgeBase(entailmentKb)
                .withEntailed(isQueryEntailed)
                .withTimeTaken(finalTime)
                .build();
    }

     
    public ModelEntailment getEntailment2(
            ModelBaseRank baseRank,
            PlFormula queryFormula) {

        long startTime = System.nanoTime();
                 
        DisplayUtils.LogDebug(_logger, "==> Rational Closure Entailmentm");       
             

         // Get the negation of the antecedent from the query.
        PlFormula negationOfAntecedent = new Negation(((Implication) queryFormula).getFirstFormula());
        

        ModelRankCollection baseRankCollection = new ModelRankCollection(baseRank.getRanking());

        Collections.sort(baseRankCollection, (o1, o2) -> Integer.compare(o1.getRankNumber(), o2.getRankNumber()));

        _logger.debug("->BaseRank");
        for (ModelRank rank : baseRankCollection) {
            _logger.debug(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

        _logger.debug(String.format("->Query, α: %s", queryFormula));
        _logger.debug(String.format("->Antecedent Negation of α: %s", negationOfAntecedent));

        ModelRankCollection removedRanking = new ModelRankCollection(); 
        
        boolean isNegationOfAntecedentEntailed = true;
        
        int stepNumber = 1;
        while (true) {
            
            if(baseRankCollection.isEmpty())
            {
                break;
            }
            
            var materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection);

            var currentRank = baseRankCollection.get(0);
           
            _logger.debug(String.format("->Checking Entailment Step %s", stepNumber++));          

            _logger.debug("  Current BaseRank");
            for (ModelRank rank : baseRankCollection) {
                _logger.debug(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
            }

            _logger.debug(String.format("  Current K: %s", materialisedKnowledgeBase));
            _logger.debug(String.format("  Checking if %s is entailed by current K", negationOfAntecedent));

            isNegationOfAntecedentEntailed = _reasoner.query(materialisedKnowledgeBase, negationOfAntecedent);

            if (!isNegationOfAntecedentEntailed) {
                _logger.debug("  NO its not, so we stop rank removal process with current K = " + materialisedKnowledgeBase);
                break;
            }
            
             // if baseRank has only the infinity rank, then we stop processing
            if (currentRank.getRankNumber() == Symbols.INFINITY_RANK_NUMBER) {
                _logger.debug("YES it is, and since ONLY ∞ rank remains, so we stop processing with current K = " + materialisedKnowledgeBase);
                break;
            }

            _logger.debug(String.format("  YES it is, so we remove rank %s: %s", currentRank.getRankNumber(), currentRank.getFormulas()));
            removedRanking.add(currentRank);
            baseRankCollection.remove(0);         
        }

        Collections.sort(baseRankCollection, (o1, o2) -> Integer.compare(o2.getRankNumber(), o1.getRankNumber()));
        Collections.sort(removedRanking, (o1, o2) -> Integer.compare(o2.getRankNumber(), o1.getRankNumber()));
       
        _logger.debug("->Remaining Ranks:");
        for (ModelRank rank : baseRankCollection) {
            _logger.debug(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }
        
        _logger.debug("->Removed Ranks:");
        for (ModelRank rank : removedRanking) {
            _logger.debug(String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

        var materialisedQueryFormula = ReasonerUtils.toMaterialisedFormula(queryFormula);
        var materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection);

        boolean isQueryEntailed = !isNegationOfAntecedentEntailed && _reasoner.query(materialisedKnowledgeBase, materialisedQueryFormula);
        
        KnowledgeBase entailmentKb = new KnowledgeBase();

        var hasEntailed = "NO";
        if (isQueryEntailed) {
            entailmentKb = ReasonerUtils.toKnowledgeBase(baseRankCollection);
            hasEntailed = "YES";
        }

        var finalTime = ReasonerUtils.ToTimeDifference(startTime, System.nanoTime());
        
        _logger.debug(String.format("Finally checking if %s is entailed by %s", materialisedQueryFormula, materialisedKnowledgeBase));
        _logger.debug(String.format("Is Entailed: %s in %s", hasEntailed, finalTime));                                 

        return new ModelRationalClosureEntailment.RationalEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
                .withRemovedRanking(removedRanking)
                .withRemainingRanking(baseRankCollection)
                .withEntailmentKnowledgeBase(entailmentKb)
                .withEntailed(isQueryEntailed)
                .withTimeTaken(finalTime)
                .build();
    }
    
}

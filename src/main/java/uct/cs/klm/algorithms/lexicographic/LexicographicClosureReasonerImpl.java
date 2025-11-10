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
import uct.cs.klm.algorithms.models.ModelEntailment;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;
import uct.cs.klm.algorithms.services.IReasonerService;
import uct.cs.klm.algorithms.services.KlmReasonerBase;
import uct.cs.klm.algorithms.utils.DisplayUtils;
import uct.cs.klm.algorithms.utils.ReasonerUtils;
import uct.cs.klm.algorithms.utils.Symbols;

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
            }
            else {
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
            if(stopNow) {
                break;
            }
            
            if(currentRank.isEmpty()) {
                continue;
            }
            int rankNumber = currentRank.getRankNumber();
            DisplayUtils.LogDebug(_logger, String.format("Rank := %s: %s", rankNumber, currentRank.getFormulas()));   
                
            previousPowersets = previousPowersets.stream().distinct().collect(Collectors.toList());
            Map.Entry<ModelRankCollection, List<List<PlFormula>>> powersetTurple = ReasonerUtils.powersetIterative(otherRankCollection, rankNumber, previousPowersets);  
            
            higherRanks = powersetTurple.getKey();
            var currentPowerset = powersetTurple.getValue();
                       
            int counter = 0;
            for(var ff : currentPowerset){
                DisplayUtils.LogDebug(_logger, String.format("PowerSet := %s-%s: %s", rankNumber, counter, ff));
                counter++;
            }
            
            previousPowersets = ReasonerUtils.AddToList(previousPowersets, currentPowerset);
                       
            for(var powerset : currentPowerset){
                
                DisplayUtils.LogDebug(_logger, String.format("PowerSet := %s: %s", rankNumber, powerset));  
                
                //DisplayUtils.LogDebug(_logger, String.format("Current KB %s: %s\n %s\n %s", rankNumber, infinityRank, higherRanks, powerset)); 
                
                // Materialise the knowledge base from the current ranking collection.
                var currentKb =  ReasonerUtils.toKnowledgeBase(infinityRank, higherRanks, powerset, null); 
                materialisedKB = ReasonerUtils.toMaterialisedKnowledgeBase(currentKb); 
                
                previousPowersets.add(ReasonerUtils.toFormulaList(currentKb));
                previousPowersets.add(powerset);
                
                DisplayUtils.LogDebug(_logger, String.format("MaterialisedKB := %s: %s", rankNumber, materialisedKB));   
                
                boolean isNegationEntailed = _reasoner.query(materialisedKB, negationOfAntecedent);
                if (isNegationEntailed) {
                    _logger.debug("  YES-NegationOfAntecedent:Entailed; We continue to NEXT powersetEntry");                
                }
                else {
                    _logger.debug("  NOT-NegationOfAntecedent:Entailed; We checking if materialisedKB entails query");                       
                     isQueryEntailed = _reasoner.query(materialisedKB, materialisedQueryFormula);
                     stopNow = true;
                     if(isQueryEntailed) {                          
                          _logger.debug("  YES-Query:Entailed; We STOP and EXIT");       
                     }
                     else{
                          _logger.debug("  NO-Query:Entailed; We continue to NEXT powersetEntry");   
                     }
                         
                }
                
                if(stopNow) {
                    break;
                }
            }
            
            if(stopNow) {
                break;
            }                                                
        }
              
        if(isQueryEntailed)
        {     
            remainingRanking = ReasonerUtils.toRanksFromKnowledgeBase(baseRank, materialisedKB, false);           
            removedRanking = ReasonerUtils.toRanksFromKnowledgeBase(baseRank, remainingRanking.getKnowledgeBase(), true);
        }
                              
        if(!isQueryEntailed)
        {           
            isQueryEntailed = doesInfinityRankEntailQuery(infinityRank, queryFormula);
            
            if(isQueryEntailed)
            {                           
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

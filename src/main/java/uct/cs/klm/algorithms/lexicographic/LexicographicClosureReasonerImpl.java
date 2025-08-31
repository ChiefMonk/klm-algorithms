package uct.cs.klm.algorithms.lexicographic;

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
                    
            Map.Entry<ModelRankCollection, List<List<PlFormula>>> powersetTurple = ReasonerUtils.powersetIterative(otherRankCollection, rankNumber, previousPowersets);  
            
            higherRanks = powersetTurple.getKey();
            var currentPowerset = powersetTurple.getValue();
                       
            int counter = 0;
            for(var ff : currentPowerset){
                DisplayUtils.LogDebug(_logger, String.format("PowerSet := %s-%s: %s", rankNumber, counter, ff));
                counter++;
            }
            
            ReasonerUtils.AddToList(previousPowersets, currentPowerset);
                       
            for(var powerset : currentPowerset){
                
                DisplayUtils.LogDebug(_logger, String.format("PowerSet := %s: %s", rankNumber, powerset));  
                
                //DisplayUtils.LogDebug(_logger, String.format("Current KB %s: %s\n %s\n %s", rankNumber, infinityRank, higherRanks, powerset)); 
                
                // Materialise the knowledge base from the current ranking collection.
                var currentKb =  ReasonerUtils.toKnowledgeBase(infinityRank, higherRanks, powerset); 
                materialisedKB = ReasonerUtils.toMaterialisedKnowledgeBase(currentKb); 
                
                previousPowersets.add(ReasonerUtils.toFormulaList(currentKb));
                
                DisplayUtils.LogDebug(_logger, String.format("MaterialisedKB := %s: %s", rankNumber, materialisedKB));   
                
                boolean isNegationEntailed = _reasoner.query(materialisedKB, negationOfAntecedent);
                if (isNegationEntailed) {
                    _logger.debug("  YES-NegationOfAntecedent:Entailed; We continue to NEXT powersetEntry");                
                }
                else {
                    _logger.debug("  NOT-NegationOfAntecedent:Entailed; We checking if materialisedKB entails query");                       
                     isQueryEntailed = _reasoner.query(materialisedKB, materialisedQueryFormula);
                     
                     if(isQueryEntailed) {
                            stopNow = true;
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

        long endTime = System.nanoTime();
        double timeTaken = (endTime - startTime) / 1_000_000_000.0;
        
        if(!isQueryEntailed)
        {
            materialisedKB = new KnowledgeBase();
        }
               
        return new ModelLexicographicEntailment.ModelLexicographicEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
                //.withMiniBaseRanking(miniBaseRanking)
                .withRemovedRanking(removedRanking)
                .withRemainingRanking(baseRank.getRanking())
                .withWeakenedRanking(weakenedRanking)
                .withEntailmentKnowledgeBase(materialisedKB)
                .withEntailed(isQueryEntailed)
                .withTimeTaken(timeTaken)
                .build();
    }
      
    public ModelEntailment getEntailmentC(ModelBaseRank baseRank, PlFormula queryFormula) {
        long startTime = System.nanoTime();
        _logger.debug("==> Lexicographic Closure Entailment");

        // Compute the negation of the antecedent of the query.
        PlFormula negationOfAntecedent = new Negation(((Implication) queryFormula).getFirstFormula());

        // Clone and sort the base ranking in ascending order.
        ModelRankCollection baseRankCollection = new ModelRankCollection(baseRank.getRanking().clone());
        baseRankCollection.sort(Comparator.comparingInt(ModelRank::getRankNumber));

        _logger.debug("-> BaseRank");
        for (ModelRank rank : baseRankCollection) {
            _logger.debug("   {}:{}", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas());
        }
        _logger.debug("-> Query, α: {}", queryFormula);
        _logger.debug("-> Antecedent Negation of α: {}", negationOfAntecedent);

        ModelRankCollection removedRanking = new ModelRankCollection();
        ModelRankCollection weakenedRanking = new ModelRankCollection();
        ModelRankCollection miniBaseRanking = new ModelRankCollection();

        int stepNumber = 1;
        outerLoop:
        while (!baseRankCollection.isEmpty()) {
            ModelRank currentRank = baseRankCollection.get(0);
            
            // Materialise the knowledge base from the current ranking collection.
            KnowledgeBase materialisedKB = ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection);
            
              // Stop if the current rank is the infinity rank.
            if (currentRank.getRankNumber() == Symbols.INFINITY_RANK_NUMBER) {
                _logger.debug("  Because current rank is ∞; stopping with current K = {}", materialisedKB);
                break;
            }

            _logger.debug("-> Checking Entailment Step {}", stepNumber++);
            _logger.debug("  Current BaseRank:");
            for (ModelRank rank : baseRankCollection) {
                _logger.debug("   {}:{}", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas());
            }
            _logger.debug("  Current K: {}", materialisedKB);
            _logger.debug("  Checking if {} is entailed by current K", negationOfAntecedent);

            boolean isNegationEntailed = _reasoner.query(materialisedKB, negationOfAntecedent);
            if (!isNegationEntailed) {
                _logger.debug("  NO, not entailed; stopping rank removal process with current K = {}", materialisedKB);
                break;
            }

            _logger.debug("  YES, entailed; processing mini-ranks within rank {}: {}", 
                    currentRank.getRankNumber(), currentRank.getFormulas());
            // Generate mini-rank combinations and sort them descending by the number of formulas.
            ModelRankCollection currentMiniRankCollection = ReasonerUtils.generateFormulaCombinations(currentRank, true);
            currentMiniRankCollection.sort((a, b) -> Integer.compare(b.getFormulas().size(), a.getFormulas().size()));

            for (ModelRank mini : currentMiniRankCollection) {
                mini.setRankNumber(currentRank.getRankNumber());
                miniBaseRanking.add(mini);
            }

            // Remove the current rank from the base ranking.
            baseRankCollection.remove(0);
            boolean stopProcessing = false;
            int miniStepNumber = 1;
            for (ModelRank currentMiniRank : currentMiniRankCollection) {
                // If the mini-rank is empty or equal to the current rank, skip it.
                if (currentMiniRank.isEmpty() || 
                        currentMiniRank.getFormulas().size() == currentRank.getFormulas().size()) {
                    continue;
                }
                // Build a temporary collection combining the remaining base ranks with the current mini-rank.
                ModelRankCollection tempMiniBaseRank = new ModelRankCollection(baseRankCollection);
                currentMiniRank.setRankNumber(currentRank.getRankNumber());
                tempMiniBaseRank.add(currentMiniRank);
                tempMiniBaseRank.sort((r1, r2) -> Integer.compare(r2.getRankNumber(), r1.getRankNumber()));

                _logger.debug("  Current Temp MiniBaseRank:");
                for (ModelRank rank : tempMiniBaseRank) {
                    _logger.debug("   {}:{}", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas());
                }

                materialisedKB = ReasonerUtils.toMaterialisedKnowledgeBase(tempMiniBaseRank);
                _logger.debug("  Current K: {}", materialisedKB);
                _logger.debug("  Checking if {} is entailed by current K", negationOfAntecedent);

                isNegationEntailed = _reasoner.query(materialisedKB, negationOfAntecedent);
                if (!isNegationEntailed) {
                    stopProcessing = true;
                    // Add remaining formulas as a new rank.
                    ModelRank remainingFormulaRank = new ModelRank(currentRank.getRankNumber(), currentMiniRank.getFormulas());
                    baseRankCollection.add(remainingFormulaRank);
                    weakenedRanking.add(currentRank);

                    var removedMiniRankFormulas = ReasonerUtils.removeFormulasFromKnowledgeBase(
                            currentRank.getFormulas(), currentMiniRank.getFormulas());
                    if (!removedMiniRankFormulas.isEmpty()) {
                        removedRanking.add(new ModelRank(currentRank.getRankNumber(), removedMiniRankFormulas));
                    }
                    _logger.debug("  NO, not entailed; stopping rank removal process with current K = {}",
                            ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection));
                    break;
                }
                _logger.debug("  YES, entailed; ignoring mini rank {}: {}", miniStepNumber++, currentMiniRank.getFormulas());
            }
            if (stopProcessing) {
                break outerLoop;
            }
            removedRanking.add(currentRank);
            weakenedRanking.add(currentRank);
        }

        // Sort the resulting collections in descending order.
        baseRankCollection.sort((r1, r2) -> Integer.compare(r2.getRankNumber(), r1.getRankNumber()));
        removedRanking.sort((r1, r2) -> Integer.compare(r2.getRankNumber(), r1.getRankNumber()));
        weakenedRanking.sort((r1, r2) -> Integer.compare(r2.getRankNumber(), r1.getRankNumber()));

        _logger.debug("-> Remaining Ranks:");
        for (ModelRank rank : baseRankCollection) {
            _logger.debug("   {}:{}", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas());
        }
        _logger.debug("-> Removed Ranks:");
        for (ModelRank rank : removedRanking) {
            _logger.debug("   {}:{}", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas());
        }

        PlFormula materialisedQueryFormula = ReasonerUtils.toMaterialisedFormula(queryFormula);
       var materialisedKB = ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection);
        boolean isQueryEntailed = !baseRankCollection.isEmpty() &&
                _reasoner.query(materialisedKB, materialisedQueryFormula);
        
        KnowledgeBase entailmentKb = new KnowledgeBase();
        String hasEntailed = "NO";
        if (isQueryEntailed) {
            entailmentKb = ReasonerUtils.toKnowledgeBase(baseRankCollection);
            hasEntailed = "YES";
        }
        _logger.debug("Finally checking if {} is entailed by {}", materialisedQueryFormula, materialisedKB);
        _logger.debug("Is Entailed: {}", hasEntailed);

        long endTime = System.nanoTime();
        double timeTaken = (endTime - startTime) / 1_000_000_000.0;
               
        return new ModelLexicographicEntailment.ModelLexicographicEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
               // .withMiniBaseRanking(miniBaseRanking)
                .withRemovedRanking(removedRanking)
                .withRemainingRanking(baseRankCollection)
                .withWeakenedRanking(weakenedRanking)
                .withEntailmentKnowledgeBase(entailmentKb)
                .withEntailed(isQueryEntailed)
                .withTimeTaken(timeTaken)
                .build();
    }
    
    // @Override
    public ModelEntailment getEntailment5(
            ModelBaseRank baseRank,
            PlFormula queryFormula) {

        long startTime = System.nanoTime();

         DisplayUtils.LogDebug(_logger, "==>Lexicographic Closure Entailment");

        // get the negation of the antecedent of the query
        PlFormula negationOfAntecedent = new Negation(((Implication) queryFormula).getFirstFormula());

        ModelRankCollection baseRankCollection = new ModelRankCollection(baseRank.getRanking().clone());
        Collections.sort(baseRankCollection, (o1, o2) -> Integer.compare(o1.getRankNumber(), o2.getRankNumber()));

        DisplayUtils.LogDebug(_logger, "->BaseRank");
        for (ModelRank rank : baseRankCollection) {
             DisplayUtils.LogDebug(_logger, String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

         DisplayUtils.LogDebug(_logger, String.format("->Query, \u0391: %s", queryFormula));
         DisplayUtils.LogDebug(_logger, String.format("->Antecedent Negation of \u0391: %s", negationOfAntecedent));

        var removedRanking = new ModelRankCollection();
        var weakenedRanking = new ModelRankCollection();
        var miniBaseRanking = new ModelRankCollection();

        boolean isNegationOfAntecedentEntailed = true;
        int stepNumber = 1;
        while (true) {

            if (baseRankCollection.isEmpty()) {
                break;
            }

            var currentRank = baseRankCollection.get(0);

            var materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection);
           
             DisplayUtils.LogDebug(_logger, String.format("->Checking Entailment Step %s", stepNumber++));           

            DisplayUtils.LogDebug(_logger, "  Current BaseRank");
            for (ModelRank rank : baseRankCollection) {
                 DisplayUtils.LogDebug(_logger, String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
            }

             DisplayUtils.LogDebug(_logger, String.format("  Current K: %s", materialisedKnowledgeBase));
             DisplayUtils.LogDebug(_logger, String.format("  Checking if %s is entailed by current K", negationOfAntecedent));

            isNegationOfAntecedentEntailed = _reasoner.query(materialisedKnowledgeBase, negationOfAntecedent);

            if (!isNegationOfAntecedentEntailed) {
                 DisplayUtils.LogDebug(_logger, "  NO its not, so we stop rank removal process with current K = " + materialisedKnowledgeBase);
                break;
            }
            
             // Stop if the current rank is the infinity rank.
            if (currentRank.getRankNumber() == Symbols.INFINITY_RANK_NUMBER) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("  YES, it is entailed but current rank is ∞; stopping with current K = {}", materialisedKnowledgeBase);
                }
                break;
            }
            
             DisplayUtils.LogDebug(_logger, String.format("  YES it is, so we process mini-ranks within rank %s: %s", currentRank.getRankNumber(), currentRank.getFormulas()));
           
            var currentMiniRankCollection = ReasonerUtils.generateFormulaCombinations(currentRank, true);
            currentMiniRankCollection.sort((a, b) -> Integer.compare(b.getFormulas().size(), a.getFormulas().size()));

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

                 DisplayUtils.LogDebug(_logger, "  Current Temp MiniBaseRank");
                for (ModelRank rank : tempMiniBaseRank) {
                     DisplayUtils.LogDebug(_logger, String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
                }

                materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(tempMiniBaseRank);

                 DisplayUtils.LogDebug(_logger, String.format("  Current K: %s", materialisedKnowledgeBase));
                 DisplayUtils.LogDebug(_logger, String.format("  Checking if %s is entailed by current K", negationOfAntecedent));

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

                     DisplayUtils.LogDebug(_logger, "  NO its not, so we stop rank removal process with current K = " + ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection));
                    break;
                }

                 DisplayUtils.LogDebug(_logger, String.format("  YES it is, so we ignore the mini rank %s: %s", miniStepNumber, currentMiniRank.getFormulas()));

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
        
        DisplayUtils.LogDebug(_logger, "->Remaining Ranks:");
        for (ModelRank rank : baseRankCollection) {
             DisplayUtils.LogDebug(_logger, String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

         DisplayUtils.LogDebug(_logger, "->Removed Ranks:");
        for (ModelRank rank : removedRanking) {
             DisplayUtils.LogDebug(_logger, String.format("   %s:%s", DisplayUtils.toRankNumberString(rank.getRankNumber()), rank.getFormulas()));
        }

        var materialisedQueryFormula = ReasonerUtils.toMaterialisedFormula(queryFormula);
        var materialisedKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(baseRankCollection);

        boolean isQueryEntailed = !baseRankCollection.isEmpty() 
                && !isNegationOfAntecedentEntailed 
                && _reasoner.query(materialisedKnowledgeBase, materialisedQueryFormula);
        
        KnowledgeBase entailmentKb = new KnowledgeBase();

        var hasEntailed = "NO";
        if (isQueryEntailed) {
            entailmentKb = ReasonerUtils.toKnowledgeBase(baseRankCollection);
            hasEntailed = "YES";
        }

         DisplayUtils.LogDebug(_logger, String.format("Finally checking if %s is entailed by %s", materialisedQueryFormula, materialisedKnowledgeBase));
         DisplayUtils.LogDebug(_logger, String.format("Is Entailed: %s", hasEntailed));

        long endTime = System.nanoTime();
               
        return new ModelLexicographicEntailment.ModelLexicographicEntailmentBuilder()
                .withKnowledgeBase(baseRank.getKnowledgeBase())
                .withQueryFormula(queryFormula)
                .withBaseRanking(baseRank.getRanking())
               // .withMiniBaseRanking(miniBaseRanking)
                .withRemovedRanking(removedRanking)
                .withRemainingRanking(baseRankCollection)
                .withWeakenedRanking(weakenedRanking)
                .withEntailmentKnowledgeBase(entailmentKb)
                .withEntailed(isQueryEntailed)
                .withTimeTaken((endTime - startTime) / 1_000_000_000.0)
                .build();
    }
          
}

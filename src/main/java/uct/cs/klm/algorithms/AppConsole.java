package uct.cs.klm.algorithms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.ranking.IBaseRankService;
import uct.cs.klm.algorithms.enums.ReasonerType;
import uct.cs.klm.algorithms.explanation.IJustificationService;
import uct.cs.klm.algorithms.generators.*;
import uct.cs.klm.algorithms.models.*;
import uct.cs.klm.algorithms.ranking.*;
import uct.cs.klm.algorithms.services.IReasonerService;
import uct.cs.klm.algorithms.utils.DefeasibleParser;
import uct.cs.klm.algorithms.utils.DisplayUtils;
import uct.cs.klm.algorithms.utils.ReasonerFactory;

/**
 *
 * @author ChipoHamayobe
 */
public class AppConsole {

    private static final Logger _logger = LoggerFactory.getLogger(AppConsole.class);

    private static final DefeasibleParser parser = new DefeasibleParser();   
    private static final IBaseRankService baseRankService = new BaseRankService();

    public static void main(String[] args) throws IOException, ParserException, Exception {
       
        _logger.debug("");
        DisplayUtils.LogDebug(_logger,"AppConsole initialized.");                 
         
        try {
            if(args.length == 0)
            {
                args = new String[2];
                args[0] = "kb4.txt";
                //args[1] = "p ~> w";
                args[1] = "s~>m";
            }
            
            if (args == null || args.length != 2) {
                throw new Exception("Please specify the knowledge base file path and the query string e.g kb.txt 's ~> w'");
            }
            
            
            String kbFilePath = args[0];                        
            String queryString = args[1];

            var knowledgeBase = parser.parseFormulasFromFile(kbFilePath);
            var queryFormula = parser.parseFormula(queryString);
                       
            DisplayUtils.LogDebug(_logger,String.format("K := %s", knowledgeBase));
            DisplayUtils.LogDebug(_logger,String.format("Signature := %s", knowledgeBase.getSignature()));
            DisplayUtils.LogDebug(_logger,String.format("Query := %s", queryFormula));           
            
            ModelBaseRank baseRank = baseRankService.construct(knowledgeBase);
           
            /*
           for(ModelRank rank : baseRank.getRanking()){
                String rankNumber = String.valueOf(rank.getRankNumber());      
                var formulas =  rank.getFormulas();
                DisplayUtils.LogDebug(_logger, String.format("Rank := %s: %s", rankNumber, formulas));
              
                var fList = ReasonerUtils.powersetIterative(rank, false);
              
                int counter = 0;
                for(var ff : fList){
                    DisplayUtils.LogDebug(_logger, String.format("PowerSet := %s: %s", counter, ff));
                    counter++;
                }
           }
            */
                      
                                                  
           ExecuteResoner("brelc", baseRank, knowledgeBase, queryFormula);           

        } catch (IOException ex) {            
            _logger.error("Error message", ex);           
        } catch (Exception ex) {
             _logger.error("Error message", ex);      
        }
    }
    
    /**
     * Example usage of the Random Knowledge Base Generator.
     * @return 
     */
    public static boolean GenerateKnowledgeBaseGenerator() {

        try {
            // Create a generator with propositions a through j
            RandomKnowledgeBaseGenerator generator = new RandomKnowledgeBaseGenerator("abcdefghijklmnopgrs");

            // Generate a knowledge base with 50 defeasible formulas, 10 classical formulas, max depth 3
            KnowledgeBase knolewdgeBase = generator.generateKnowledgeBase(50, 10, 3, 100);

            // Print the generated knowledge base
            DisplayUtils.LogDebug(_logger, "=== GENERATED KNOWLEDGE BASE ===");
            DisplayUtils.LogDebug(_logger, "Classical formulas:");

            if (_logger.isDebugEnabled()) {
                int cc = 1;
                for (var formula : knolewdgeBase.getClassicalFormulas()) {
                    _logger.debug(String.format("   %s: %s", cc, formula));

                    cc++;
                }
            }

            DisplayUtils.LogDebug(_logger, "\nDefeasible formulas:");

            if (_logger.isDebugEnabled()) {
                int cd = 1;
                for (var formula : knolewdgeBase.getDefeasibleFormulas()) {
                    _logger.debug(String.format("   %s: %s", cd, formula));

                    cd++;
                }
            }

            DisplayUtils.LogDebug(_logger, "\nTotal: "
                    + (knolewdgeBase.getClassicalFormulas().size() + knolewdgeBase.getDefeasibleFormulas().size())
                    + " formulas");

            var queryFormula = parser.parseFormula("a ~> (b &&c)");

            DisplayUtils.LogDebug(_logger, String.format("K := %s", knolewdgeBase));
            DisplayUtils.LogDebug(_logger, String.format("Query := %s", queryFormula));

            ModelBaseRank baseRank = baseRankService.construct(knolewdgeBase);

            ExecuteResoner("lexicographic", baseRank, knolewdgeBase, queryFormula);

            return true;

        } catch (IOException ex) {
            _logger.error("Error message", ex);
        } catch (Exception ex) {
            _logger.error("Error message", ex);
        }

        return true;
    }

    
    private static void ExecuteResoner(String reasonerTypeString, ModelBaseRank baseRank,  KnowledgeBase knolewdgeBase, PlFormula queryFormula)
    {
       
            ReasonerType reasonerType = ReasonerFactory.createReasonerType(reasonerTypeString);
            IReasonerService reasoner = ReasonerFactory.createEntailment(reasonerType);
            
            ModelEntailment entailment = reasoner.getEntailment(baseRank, queryFormula);
            
            DisplayUtils.LogDebug(_logger, String.format("Entailment := %s, %s", entailment.getEntailed(), entailment.getEntailmentKnowledgeBase()));            
            IJustificationService justification = ReasonerFactory.createJustification(reasonerType);
            var justificationKb = justification.computeJustification(entailment.getEntailmentKnowledgeBase(), queryFormula);
            
            DisplayUtils.LogDebug(_logger,(String.format("Does %s entail %s", queryFormula,knolewdgeBase ))); 
            
            if(!justificationKb.isEmpty())
            {
                DisplayUtils.LogDebug(_logger,(String.format("Justification := %s", justificationKb.get(0)))); 
            }  

    }    
}

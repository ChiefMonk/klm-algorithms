package uct.cs.klm.algorithms;

import uct.cs.klm.algorithms.ranking.IBaseRankService;
import java.io.IOException;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.enums.ReasonerType;
import uct.cs.klm.algorithms.explanation.IJustificationService;

import uct.cs.klm.algorithms.models.*;
import uct.cs.klm.algorithms.ranking.*;
import uct.cs.klm.algorithms.services.IReasonerService;
import uct.cs.klm.algorithms.utils.DefeasibleParser;
import uct.cs.klm.algorithms.utils.ReasonerFactory;

/**
 *
 * @author ChipoHamayobe
 */
public class AppConsole {

    private static final DefeasibleParser parser = new DefeasibleParser();   
    private static final IBaseRankService baseRankService = new BaseRankService();

    public static void main(String[] args) throws IOException, ParserException, Exception {
       
        try {
            if(args.length == 0)
            {
                args = new String[2];
                args[0] = "kb3.txt";
                args[1] = "u ~> b";
            }
            
            if (args == null || args.length != 2) {
                throw new Exception("Please specify the knowledge base file path and the query string e.g kb.txt 's ~> w'");
            }
            
            
            String kbFilePath = args[0];                        
            String queryString = args[1];

            var knolewdgeBase = parser.parseFormulasFromFile(kbFilePath);
            var queryFormula = parser.parseFormula(queryString);
                       
            System.out.println(String.format("K := %s", knolewdgeBase));
            System.out.println(String.format("Query := %s", queryFormula));           
            
            ModelBaseRank baseRank = baseRankService.construct(knolewdgeBase);
            
            System.out.println(String.format("BaseRank := \n %s", baseRank)); 
                     
           //ExecuteResoner("rational", baseRank, knolewdgeBase, queryFormula);           

        } catch (IOException e) {
            System.out.println();
            System.out.println(String.format("An error occured: %s", e));
            System.out.println();
        } catch (Exception e) {
            System.out.println();
            System.out.println(String.format("An error occured: %s", e));
            System.out.println();
        }
    }
    
    
    private static void ExecuteResoner(String reasonerTypeString, ModelBaseRank baseRank,  KnowledgeBase knolewdgeBase, PlFormula queryFormula)
    {
       
            ReasonerType reasonerType = ReasonerFactory.createReasonerType(reasonerTypeString);
            IReasonerService reasoner = ReasonerFactory.createEntailment(reasonerType);
            
            ModelEntailment entailment = reasoner.getEntailment(baseRank, queryFormula);
            
            System.out.println(String.format("Entailment := %s, %s", entailment.getEntailed(), entailment.getEntailmentKnowledgeBase()));  
            
           // IJustificationService justification = ReasonerFactory.createJustification(reasonerType);
           // var justificationKb = justification.computeJustification(entailment.getEntailmentKnowledgeBase(), queryFormula);
            
           // System.out.println(String.format("Does %s entail %s", queryFormula,knolewdgeBase )); 
            
           // if(!justificationKb.isEmpty())
           // {
             //   System.out.println(String.format("Justification := %s", justificationKb.get(0))); 
          //  }
            System.out.println();
    }

    private static void consoleWriteLine(String heading, String message) {
        System.out.println();
        System.out.println(String.format("===== %s =====", heading));
        System.out.println(message);
    }

    private static void consoleWriteLine(String heading, Exception e) {
        System.out.println();
        System.out.println(String.format("An error occured: %s", e));
        System.out.println();
    }

}

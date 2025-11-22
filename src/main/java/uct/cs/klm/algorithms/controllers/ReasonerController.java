package uct.cs.klm.algorithms.controllers;

import uct.cs.klm.algorithms.lexicographic.ModelLexicographicEntailment;
import uct.cs.klm.algorithms.rational.ModelRationalClosureEntailment;
import uct.cs.klm.algorithms.rational.RationalClosureExplanationService;
import uct.cs.klm.algorithms.relevant.MinimalRelevantClosureExplanationService;
import uct.cs.klm.algorithms.relevant.BasicRelevantClosureExplanationService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.enums.ReasonerType;
import uct.cs.klm.algorithms.explanation.IJustificationService;
import uct.cs.klm.algorithms.models.*;
import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.services.*;
import uct.cs.klm.algorithms.utils.*;
import uct.cs.klm.algorithms.services.IReasonerService;

/**
 * This class represents a reasoner controller for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

public class ReasonerController {
    private static final Logger logger = LoggerFactory.getLogger(ReasonerController.class);

    public static void getEntailment(Context context) {
        
        System.out.println();
        //System.out.println("==> Reasoner Controller");  
        
        ReasonerType reasonerType = ReasonerFactory.createReasonerType(context.pathParam("reasoner"));
        String query = context.pathParam("queryFormula");

        try {

            ModelBaseRank baseRankParam = context.bodyAsClass(ModelBaseRank.class);
            ModelBaseRank baseRank = new ModelBaseRank(baseRankParam);
                                        
            DefeasibleParser parser = new DefeasibleParser();
            PlFormula queryFormula = parser.parseFormula(query);
            
          
            IReasonerService reasoner = ReasonerFactory.createEntailment(reasonerType);
            ModelEntailment entailment = reasoner.getEntailment(baseRank, queryFormula);

            IJustificationService justification = ReasonerFactory.createJustification(reasonerType);

            long startTime = System.nanoTime();
            
            var justificationKb = justification.computeAllJustifications(
                    entailment.getBaseRanking().getInfinityRank(), 
                    entailment.getEntailmentKnowledgeBase(), 
                    queryFormula,
                    true);
            
            double finalTime = ReasonerUtils.ToTimeDifference(startTime, System.nanoTime());

            entailment.setJustification(justificationKb);
            entailment.setJustificationTime(finalTime);

            context.status(200);
            context.json(entailment);

        } catch (IllegalArgumentException e) {

            System.out.println(String.format("An error occured: %s", e));
            e.printStackTrace();

            context.status(400);
            context.json(new ModelErrorResponse(400, "Bad Request", "Invalid reasoner: " + reasonerType));
        } catch (Exception e) {

            System.out.println(String.format("An error occured: %s", e));
            e.printStackTrace();

            context.status(400);
            context.json(new ModelErrorResponse(400, "Bad Request", "Invalid query formula: " + query));
        }
    }

    public static void getExplanation(Context context) {

        System.out.println();
        //System.out.println("==> Reasoner Controller");

        ReasonerType reasonerType = ReasonerFactory.createReasonerType(context.pathParam("reasoner"));

        try {
            ModelEntailment entailment = switch (reasonerType) {
                case ReasonerType.RationalClosure ->  context.bodyAsClass(ModelRationalClosureEntailment.class);
                case ReasonerType.LexicographicClosure ->  context.bodyAsClass(ModelLexicographicEntailment.class);
                case ReasonerType.BasicRelevantClosure ->  context.bodyAsClass(ModelRelevantClosureEntailment.class);
                case ReasonerType.MinimalRelevantClosure ->  context.bodyAsClass(ModelRelevantClosureEntailment.class);
                default -> throw new IllegalArgumentException("Invalid reasoner");
            };

            var reasonerService = switch (reasonerType) {
                case ReasonerType.RationalClosure ->  new RationalClosureExplanationService();
                case ReasonerType.LexicographicClosure ->  new LexicalClosureExplanationService();
                case ReasonerType.BasicRelevantClosure ->  new BasicRelevantClosureExplanationService();
                case ReasonerType.MinimalRelevantClosure ->  new MinimalRelevantClosureExplanationService();
                default -> throw new IllegalArgumentException("Invalid reasoner");
            };

            var explanation = reasonerService.generateExplanation(entailment);

            context.status(200);
            context.json(explanation);

        } catch (IllegalArgumentException e) {
            System.out.printf("An error occurred: %s%n", e);
            context.status(400);
            context.json(new ModelErrorResponse(400, "Bad Request", "Invalid reasoner: " + reasonerType));
        }
    }
}

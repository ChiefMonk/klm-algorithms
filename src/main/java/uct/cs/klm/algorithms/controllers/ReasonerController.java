package uct.cs.klm.algorithms.controllers;

import io.javalin.http.Context;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.enums.ReasonerType;
import uct.cs.klm.algorithms.explanation.IJustificationService;
import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.models.*;
import uct.cs.klm.algorithms.utils.*;
import uct.cs.klm.algorithms.services.IReasonerService;

public class ReasonerController {

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
            var justificationKb = justification.computeJustification(entailment.getEntailmentKnowledgeBase(), queryFormula);

            entailment.setJustification(justificationKb);

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
}

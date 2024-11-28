package uct.cs.klm.algorithms.controllers;

import org.tweetyproject.logics.pl.syntax.PlFormula;

import io.javalin.http.Context;
import uct.cs.klm.algorithms.enums.ReasonerType;
import uct.cs.klm.algorithms.explanation.IJustificationService;
import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.models.Entailment;
import uct.cs.klm.algorithms.models.ErrorResponse;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;
import uct.cs.klm.algorithms.utils.ReasonerFactory;
import uct.cs.klm.algorithms.utils.DefeasibleParser;
import uct.cs.klm.algorithms.services.IReasonerService;
import uct.cs.klm.algorithms.utils.DisplayUtils;

public class ReasonerController {

    public static void getEntailment(Context ctx) {
        ReasonerType reasonerType = ReasonerFactory.createReasonerType(ctx.pathParam("reasoner"));
        String formula = ctx.pathParam("queryFormula");

        System.out.println("ReasonerType: " + reasonerType);
        System.out.println("Formula: " + formula);          

        try {
            DefeasibleParser parser = new DefeasibleParser();
            PlFormula queryFormula = parser.parseFormula(formula);
            //System.out.println(DisplayUtils.printJustificationAsCSV(entailment.getKnowledgeBase()));
              
            ModelBaseRank baseRank = ctx.bodyAsClass(ModelBaseRank.class);
            
            ModelBaseRank baseRankCopy = new ModelBaseRank(baseRank);

            IReasonerService reasoner = ReasonerFactory.createEntailment(reasonerType);
            Entailment entailment = reasoner.getEntailment(baseRankCopy, queryFormula);

            System.out.println(DisplayUtils.printJustificationAsCSV(entailment.getKnowledgeBase()));

            ModelRankCollection ranks = entailment.getEntailmentRanking();

            System.out.println(DisplayUtils.printJustificationAsCSV(ranks.getKnowledgeBase()));

            IJustificationService justification = ReasonerFactory.createJustification(reasonerType);
            KnowledgeBase justificationKb = justification.computeJustification(entailment.getEntailmentRanking(), queryFormula);

            entailment.setJustification(justificationKb);

            ctx.status(200);
            ctx.json(entailment);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            
            ctx.status(400);
            ctx.json(new ErrorResponse(400, "Bad Request", "Invalid reasoner: " + reasonerType));
        } catch (Exception e) {
            
           e.printStackTrace();
             
            ctx.status(400);
            ctx.json(new ErrorResponse(400, "Bad Request", "Invalid query formula: " + formula));
        }
    }
}

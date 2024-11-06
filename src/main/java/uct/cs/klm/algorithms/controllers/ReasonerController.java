package uct.cs.klm.algorithms.controllers;

import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.models.BaseRank;
import uct.cs.klm.algorithms.models.ErrorResponse;
import uct.cs.klm.algorithms.services.ReasonerFactory;
import uct.cs.klm.algorithms.services.ReasonerService;
import uct.cs.klm.algorithms.utils.DefeasibleParser;

import io.javalin.http.Context;

public class ReasonerController {

  public static void getEntailment(Context ctx) {
    String reasonerType = ctx.pathParam("reasoner");
    String formula = ctx.pathParam("queryFormula");

    try {
      DefeasibleParser parser = new DefeasibleParser();
      PlFormula queryFormula = parser.parseFormula(formula);
      BaseRank baseRank = ctx.bodyAsClass(BaseRank.class);
      BaseRank baseRankCopy = new BaseRank(baseRank);
      ReasonerService reasoner = ReasonerFactory.createReasoner(reasonerType);
      ctx.status(200);
      ctx.json(reasoner.getEntailment(baseRankCopy, queryFormula));

    } catch (IllegalArgumentException e) {
      ctx.status(400);
      ctx.json(new ErrorResponse(400, "Bad Request", "Invalid reasoner: " + reasonerType));
    } catch (Exception e) {
      ctx.status(400);
      ctx.json(new ErrorResponse(400, "Bad Request", "Invalid query formula: " + formula));
    }
  }

}

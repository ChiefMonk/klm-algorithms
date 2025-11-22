package uct.cs.klm.algorithms.controllers;

import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.models.DefeasibleImplication;
import uct.cs.klm.algorithms.models.ModelErrorResponse;
import uct.cs.klm.algorithms.services.FormulaServiceImpl;
import uct.cs.klm.algorithms.utils.DefeasibleParser;
import uct.cs.klm.algorithms.services.FormulaService;

import io.javalin.http.Context;

/**
 * This class represents a formula controller for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

public class FormulaController 
{
  private final static FormulaService formulaService = new FormulaServiceImpl();

  public static void getQueryFormula(Context ctx) {
    Map<String, PlFormula> response = new HashMap<>();
    response.put("queryFormula", formulaService.getQueryFormula());
    ctx.status(200);
    ctx.json(response);
  }

  public static void createQueryFormula(Context ctx) {
    String formula = ctx.pathParam("queryFormula");
    DefeasibleParser parser = new DefeasibleParser();

    try {
      PlFormula queryFormula = parser.parseFormula(formula);

      if (queryFormula instanceof DefeasibleImplication) {
        Map<String, PlFormula> response = new HashMap<>();
        response.put("queryFormula", queryFormula);
        ctx.status(200);
        ctx.json(response);

      } else {
        ctx.status(400);
        ctx.json(new ModelErrorResponse(400, "Bad Request", "Formula is not defeasible implication."));
      }
    } catch (Exception e) {
      System.out.println(String.format("An error occured: %s", e));
       e.printStackTrace();
      ctx.status(400);
      ctx.json(new ModelErrorResponse(400, "Bad Request", "Invalid formula: " + formula + "."));
    }
  }
}

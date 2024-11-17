package uct.cs.klm.algorithms.controllers;

import org.tweetyproject.logics.pl.syntax.PlFormula;

import io.javalin.http.Context;
import uct.cs.klm.algorithms.enums.ReasonerType;
import uct.cs.klm.algorithms.models.BaseRank;
import uct.cs.klm.algorithms.models.ErrorResponse;
import uct.cs.klm.algorithms.utils.ReasonerFactory;
import uct.cs.klm.algorithms.utils.DefeasibleParser;
import uct.cs.klm.algorithms.services.IReasonerService;

public class ReasonerController 
{
  public static void getEntailment(Context ctx) 
  {
    ReasonerType reasonerType = ReasonerFactory.createReasonerType(ctx.pathParam("reasoner"));
    String formula = ctx.pathParam("queryFormula");
    
    System.out.println("ReasonerType: " + reasonerType);
    System.out.println("Formula: " + formula);

    try 
    {       
      DefeasibleParser parser = new DefeasibleParser();
      PlFormula queryFormula = parser.parseFormula(formula);
      BaseRank baseRank = ctx.bodyAsClass(BaseRank.class);
      BaseRank baseRankCopy = new BaseRank(baseRank);
      IReasonerService reasoner = ReasonerFactory.createEntailment(reasonerType);
      ctx.status(200);
      ctx.json(reasoner.getEntailment(baseRankCopy, queryFormula));

    } 
    catch (IllegalArgumentException e) 
    {
      ctx.status(400);
      ctx.json(new ErrorResponse(400, "Bad Request", "Invalid reasoner: " + reasonerType));
    } 
    catch (Exception e) 
    {
      ctx.status(400);
      ctx.json(new ErrorResponse(400, "Bad Request", "Invalid query formula: " + formula));
    }
  }
  
  public static void getExplanation(Context ctx) 
  {
    ReasonerType reasonerType = ReasonerFactory.createReasonerType(ctx.pathParam("reasoner"));
    String formula = ctx.pathParam("queryFormula");
    
    System.out.println("ReasonerType: " + reasonerType);
    System.out.println("Formula: " + formula);

    try 
    {       
      DefeasibleParser parser = new DefeasibleParser();
      PlFormula queryFormula = parser.parseFormula(formula);
      BaseRank baseRank = ctx.bodyAsClass(BaseRank.class);
      BaseRank baseRankCopy = new BaseRank(baseRank);
      IReasonerService reasoner = ReasonerFactory.createEntailment(reasonerType);
      ctx.status(200);
      ctx.json(reasoner.getEntailment(baseRankCopy, queryFormula));

    } 
    catch (IllegalArgumentException e) 
    {
      ctx.status(400);
      ctx.json(new ErrorResponse(400, "Bad Request", "Invalid reasoner: " + reasonerType));
    } 
    catch (Exception e) 
    {
      ctx.status(400);
      ctx.json(new ErrorResponse(400, "Bad Request", "Invalid query formula: " + formula));
    }
  }
}

package uct.cs.klm.algorithms.controllers;

import uct.cs.klm.algorithms.models.ModelErrorResponse;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.BaseRankService;

import io.javalin.http.Context;
import uct.cs.klm.algorithms.ranking.IBaseRankService;

public class BaseRankController {
  private static final IBaseRankService baseRankService = new BaseRankService();

  public static void getBaseRank(Context ctx) {
    try {
      KnowledgeBase kb = ctx.bodyAsClass(KnowledgeBase.class);
      ctx.status(200);
      ctx.json(baseRankService.construct(kb));
    } catch (Exception e) {
        
    System.out.println(String.format("An error occured: %s", e));
          e.printStackTrace();
      ctx.status(400);
      ctx.json(new ModelErrorResponse(400, "Bad Request", "The knowledge base is invalid"));
    }
  }
}

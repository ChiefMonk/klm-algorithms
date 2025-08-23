package uct.cs.klm.algorithms.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uct.cs.klm.algorithms.models.ModelErrorResponse;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.*;

import io.javalin.http.Context;

public class BaseRankController {
  private static final Logger logger = LoggerFactory.getLogger(BaseRankController.class);
  private static final IBaseRankService baseRankService = new BaseRankService();
  private static final IBaseRankExplanationService baseRankExplanationService = new BaseRankExplanationService();

  public static void getBaseRank(Context ctx) {
    try {
      KnowledgeBase kb = ctx.bodyAsClass(KnowledgeBase.class);
      ctx.status(200);
      ctx.json(baseRankService.construct(kb));
    } catch (Exception e) {
      logger.error("An error occurred", e);
      ctx.status(400);
      ctx.json(new ModelErrorResponse(400, "Bad Request", "The knowledge base is invalid"));
    }
  }

  public static void generateBaseRankExplanation(Context ctx) {
    try {
      ModelBaseRank baseRank =  ctx.bodyAsClass(ModelBaseRank.class);
      ctx.status(200);
      ctx.json(baseRankExplanationService.generateExplanation(baseRank));
    } catch (Exception e) {
      logger.error("An error occurred", e);
      ctx.status(400);
      ctx.json(new ModelErrorResponse(400, "Bad Request", "The base rank is invalid"));
    }
  }
}

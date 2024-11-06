package uct.cs.klm.algorithms;

import uct.cs.klm.algorithms.config.ObjectMapperConfig;
import uct.cs.klm.algorithms.controllers.BaseRankController;
import uct.cs.klm.algorithms.controllers.FormulaController;
import uct.cs.klm.algorithms.controllers.KnowledgeBaseController;
import uct.cs.klm.algorithms.controllers.ReasonerController;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

public class App {
  public static void main(String[] args) {

    Javalin app = Javalin.create(config -> {
      config.jsonMapper(new JavalinJackson(ObjectMapperConfig.createObjectMapper(), true));
      config.staticFiles.add("/web");
      // config.staticFiles.add("/"); // Other static assets, external to the ReactJS
      config.spaRoot.addFile("/", "/web/index.html"); // Catch-all route for the single-page application
    });
    app.start(8080);

    // app before
    app.before(ctx -> ctx.header("Access-Control-Allow-Credentials", "true"));

    // Routes
    app.get("/api/query-formula", FormulaController::getQueryFormula);
    app.post("/api/query-formula/{queryFormula}", FormulaController::createQueryFormula);
    app.get("/api/knowledge-base", KnowledgeBaseController::getKnowledgeBase);
    app.post("/api/knowledge-base", KnowledgeBaseController::createKb);
    app.post("/api/knowledge-base/file", KnowledgeBaseController::createKbFromFile);
    app.post("/api/base-rank", BaseRankController::getBaseRank);
    app.post("/api/entailment/{reasoner}/{queryFormula}", ReasonerController::getEntailment);
  }
}

package uct.cs.klm.algorithms;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.slf4j.LoggerFactory;
import uct.cs.klm.algorithms.config.ObjectMapperConfig;
import uct.cs.klm.algorithms.controllers.*;

public class App {
  
  private static final org.slf4j.Logger _logger = LoggerFactory.getLogger(App.class);
 
  public static void main(String[] args) {
   
    _logger.info("Application started.");
    _logger.debug("Debugging application.");
    _logger.error("An error occurred.");

    Javalin app = Javalin.create(config -> {
      config.jsonMapper(new JavalinJackson(ObjectMapperConfig.createObjectMapper(), true));
      config.staticFiles.add("/web");
      // config.staticFiles.add("/"); // Other static assets, external to the ReactJS
      config.spaRoot.addFile("/", "/web/index.html"); // Catch-all route for the single-page application
    });
    app.start(8080);

    // app before
    app.before(ctx -> ctx.header("Access-Control-Allow-Credentials", "true"));

    // query    
    app.get("/api/queries/get-formula", FormulaController::getQueryFormula);    
    app.post("/api/queries/create-formula/{queryFormula}", FormulaController::createQueryFormula);
    
     // knowledge-base    
    app.get("/api/knowledge-base/get-default", KnowledgeBaseController::getDefaultKnowledgeBase);
    app.post("/api/knowledge-base/generate", KnowledgeBaseController::generateKnowledgeBase);
    app.post("/api/knowledge-base/get-signature", KnowledgeBaseController::getKnowledgeBaseSignature);
    app.post("/api/knowledge-base/create-from-input", KnowledgeBaseController::createInputKnowledgeBase);
    app.post("/api/knowledge-base/create-from-file", KnowledgeBaseController::createFileKnowledgeBase);
    
    // base-rank    
    app.post("/api/base-rank", BaseRankController::getBaseRank);
    app.post("/api/base-rank-explanation", BaseRankController::generateBaseRankExplanation);

    // entailment 
    app.post("/api/entailment/{reasoner}/{queryFormula}", ReasonerController::getEntailment);
    
    // explanation 
    app.post("/api/explanation/{reasoner}", ReasonerController::getExplanation);

    // Evaluation
    app.post("/api/evaluation", EvaluationController::getEvaluation);
    app.post("/api/evaluation/import", EvaluationController::importEvaluation);
    app.post("/api/evaluation/export", EvaluationController::exportEvaluation);
  }
}

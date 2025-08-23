package uct.cs.klm.algorithms.controllers;

import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uct.cs.klm.algorithms.models.*;
import uct.cs.klm.algorithms.services.*;
import java.io.IOException;

public class EvaluationController {
    private static final Logger logger = LoggerFactory.getLogger(EvaluationController.class);

    private static final EvaluationService evaluationService = new EvaluationServiceImpl();

    public static void getEvaluation(Context ctx) {
        try {
            EvaluationQuery query = ctx.bodyAsClass(EvaluationQuery.class);
            EvaluationModel data = evaluationService.evaluate(query);
            ctx.json(data);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            ctx.status(500).json(new ModelErrorResponse(500, "Internal Server Error", "Evaluation failed."));
        }
    }

    public static void importEvaluation(Context ctx) {
        var uploadedFile = ctx.uploadedFile("file");

        if (uploadedFile == null) {
            ctx.status(400).json(new ModelErrorResponse(400, "Bad Request", "No file uploaded"));
            return;
        }

        try {
            EvaluationModel evaluationModel = evaluationService.importEvaluation(uploadedFile.content());
            ctx.status(200).json(evaluationModel);

        }
        catch (IOException exception) {
            logger.error(exception.getMessage(), exception);
            ctx.status(400).json(new ModelErrorResponse(400, "Bad Request", "Evaluation import failed. Please ensure the file is in the correct format."));
        }
    }

    public static void exportEvaluation(Context ctx) {
        try {
            EvaluationModel evaluationModel = ctx.bodyAsClass(EvaluationModel.class);
            var exportEntry = evaluationService.exportEvaluation(evaluationModel);
            String filename = exportEntry.getKey();
            String content = exportEntry.getValue();

            ctx.header("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
            ctx.contentType("application/json");
            ctx.result(content);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            ctx.status(500).json(new ModelErrorResponse(500, "Internal Server Error", "Evaluation export failed."));
        }
    }
}

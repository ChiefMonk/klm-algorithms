package uct.cs.klm.algorithms.controllers;

import java.util.List;

import uct.cs.klm.algorithms.models.KbGenerationInput;
import uct.cs.klm.algorithms.models.ModelErrorResponse;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.services.KnowledgeBaseServiceImpl;
import uct.cs.klm.algorithms.utils.DefeasibleParser;

import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uct.cs.klm.algorithms.services.IKnowledgeBaseService;

public class KnowledgeBaseController {

    private static final Logger _logger = LoggerFactory.getLogger(KnowledgeBaseController.class);
    private final static IKnowledgeBaseService kbService = new KnowledgeBaseServiceImpl();

    public static void getDefaultKnowledgeBase(Context ctx) {
        ctx.status(200);
        ctx.json(kbService.getKnowledgeBase());
    }

    public static void generateKnowledgeBase(Context ctx) {
        KbGenerationInput kbGenerationInput = ctx.bodyAsClass(KbGenerationInput.class);
        ctx.status(200);
        ctx.json(kbService.generateKnowledgeBase(kbGenerationInput));
    }

    public static void createInputKnowledgeBase(Context ctx) {
        try {
            KnowledgeBase kb = ctx.bodyAsClass(KnowledgeBase.class);
            ctx.status(200);
            ctx.json(kb);
        } catch (Exception ex) {

            _logger.error("Error in createInputKnowledgeBase", ex);

            ctx.status(400);
            ctx.json(new ModelErrorResponse(400, "Bad Request", "The knowledge base is invalid."));
        }
    }

    public static void createFileKnowledgeBase(Context ctx) {
        List<UploadedFile> files = ctx.uploadedFiles("file");
        try {
            if (!files.isEmpty()) {
                UploadedFile file = files.get(0);
                DefeasibleParser parser = new DefeasibleParser();
                KnowledgeBase kb = parser.parseInputStream(file.content());
                ctx.status(200);
                ctx.json(kb);
            } else {
                ctx.status(400);
                ctx.json(new ModelErrorResponse(400, "Bad Request", "No file uploaded"));
            }
        } catch (Exception ex) {

            _logger.error("Error in createFileKnowledgeBase", ex);

            ctx.status(400);
            ctx.json(new ModelErrorResponse(400, "Bad Request", "The knowledge base is invalid."));
        }
    }

    public static void getKnowledgeBaseSignature(Context ctx) {
        try {
            KnowledgeBase kb = ctx.bodyAsClass(KnowledgeBase.class);
            ctx.status(200);
            ctx.json(kb.getSignature());
        } catch (Exception ex) {

            _logger.error("Error in createInputKnowledgeBase", ex);

            ctx.status(400);
            ctx.json(new ModelErrorResponse(400, "Bad Request", "The knowledge base is invalid."));
        }
    }
}

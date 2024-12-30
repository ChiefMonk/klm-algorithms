package uct.cs.klm.algorithms.controllers;

import java.util.List;

import uct.cs.klm.algorithms.models.ModelErrorResponse;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.services.KnowledgeBaseServiceImpl;
import uct.cs.klm.algorithms.utils.DefeasibleParser;

import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import uct.cs.klm.algorithms.services.IKnowledgeBaseService;

public class KnowledgeBaseController {
  private final static IKnowledgeBaseService kbService = new KnowledgeBaseServiceImpl();

  public static void getKnowledgeBase(Context ctx) {
    ctx.status(200);
    ctx.json(kbService.getKnowledgeBase());
  }

  public static void createKb(Context ctx) {
    try {
      KnowledgeBase kb = ctx.bodyAsClass(KnowledgeBase.class);
      ctx.status(200);
      ctx.json(kb);
    } catch (Exception e) {
        
      System.out.println(String.format("An error occured: %s", e));
       e.printStackTrace();
       
      ctx.status(400);
      ctx.json(new ModelErrorResponse(400, "Bad Request", "The knowledge base is invalid."));
    }
  }

  public static void createKbFromFile(Context ctx) {
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
    } catch (Exception e) {
        
         System.out.println(String.format("An error occured: %s", e));
          e.printStackTrace();
      ctx.status(400);
      ctx.json(new ModelErrorResponse(400, "Bad Request", "The knowledge base is invalid."));
    }
  }
}

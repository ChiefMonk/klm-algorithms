package uct.cs.klm.algorithms.services;

import java.io.IOException;

import uct.cs.klm.algorithms.models.KbGenerationInput;
import uct.cs.klm.algorithms.models.KnowledgeBase;

/**
 * This interface represents a knowledge base service for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

public interface IKnowledgeBaseService {

  public KnowledgeBase getKnowledgeBase();
  
  public KnowledgeBase generateKnowledgeBase(KbGenerationInput kbGenerationInput);
  
  public KnowledgeBase getKnowledgeBase(String kbFilePath) throws Exception, IOException;
}

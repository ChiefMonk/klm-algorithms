package uct.cs.klm.algorithms.services;

import java.io.IOException;

import uct.cs.klm.algorithms.models.KbGenerationInput;
import uct.cs.klm.algorithms.models.KnowledgeBase;

public interface IKnowledgeBaseService {

  public KnowledgeBase getKnowledgeBase();
  
  public KnowledgeBase generateKnowledgeBase(KbGenerationInput kbGenerationInput);
  
  public KnowledgeBase getKnowledgeBase(String kbFilePath) throws Exception, IOException;
}

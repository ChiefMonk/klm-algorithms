package uct.cs.klm.algorithms.services;

import uct.cs.klm.algorithms.models.BaseRank;
import uct.cs.klm.algorithms.models.KnowledgeBase;

public interface BaseRankService {
  public BaseRank constructBaseRank(KnowledgeBase knowledgeBase);
}

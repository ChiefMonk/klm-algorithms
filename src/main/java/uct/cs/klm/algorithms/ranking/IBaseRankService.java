package uct.cs.klm.algorithms.ranking;

import uct.cs.klm.algorithms.models.KnowledgeBase;

/**
 * This interface represents a base rank service for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public sealed interface IBaseRankService permits BaseRankService
{
  public ModelBaseRank construct(
          KnowledgeBase knowledgeBase);
}

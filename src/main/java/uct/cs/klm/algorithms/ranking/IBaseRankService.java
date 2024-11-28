package uct.cs.klm.algorithms.ranking;

import uct.cs.klm.algorithms.models.KnowledgeBase;

/**
 *
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 */
public sealed interface IBaseRankService permits BaseRankService
{
  public ModelBaseRank construct(
          KnowledgeBase knowledgeBase);
}

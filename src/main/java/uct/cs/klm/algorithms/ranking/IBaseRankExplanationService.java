package uct.cs.klm.algorithms.ranking;

/**
 * This interface represents a base rank explanation service for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public interface IBaseRankExplanationService {
    BaseRankExplanation generateExplanation(ModelBaseRank baseRank);
}

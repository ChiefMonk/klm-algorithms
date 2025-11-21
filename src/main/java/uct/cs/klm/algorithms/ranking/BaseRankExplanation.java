package uct.cs.klm.algorithms.ranking;

import uct.cs.klm.algorithms.models.KnowledgeBase;

import java.util.List;

/**
 * This class represents a base rank explanation for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public record BaseRankExplanation(
        KnowledgeBase knowledgeBase,
        List<SequenceElement> sequence,
        ModelRankCollection ranks
) {
}

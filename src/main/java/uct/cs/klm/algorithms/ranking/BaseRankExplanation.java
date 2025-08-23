package uct.cs.klm.algorithms.ranking;

import uct.cs.klm.algorithms.models.KnowledgeBase;

import java.util.List;

public record BaseRankExplanation(
        KnowledgeBase knowledgeBase,
        List<SequenceElement> sequence,
        ModelRankCollection ranks
) {
}

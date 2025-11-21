package uct.cs.klm.algorithms.ranking;

import com.fasterxml.jackson.annotation.JsonProperty;
import uct.cs.klm.algorithms.models.KnowledgeBase;

import java.util.List;

/**
 * This class represents a sequence element for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public record SequenceElement(
        int elementNumber,
        KnowledgeBase formulas,
        List<SequenceElementCheck> checks,
        boolean isLastElement
) {
    @JsonProperty
    public boolean isEmpty() {
        return formulas.isEmpty();
    }
}

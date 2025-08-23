package uct.cs.klm.algorithms.ranking;

import com.fasterxml.jackson.annotation.JsonProperty;
import uct.cs.klm.algorithms.models.KnowledgeBase;

import java.util.List;

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

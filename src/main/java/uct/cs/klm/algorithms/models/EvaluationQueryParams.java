package uct.cs.klm.algorithms.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import uct.cs.klm.algorithms.enums.*;

import java.util.List;

public record EvaluationQueryParams(
        @JsonProperty(required = true)
        int numberOfRanks,

        @JsonProperty(required = true)
        Distribution distribution,

        @JsonProperty(required = true)
        int numberOfDefeasibleImplications,

        @JsonProperty(required = true)
        boolean simpleDiOnly,

        @JsonProperty(required = true)
        boolean reuseConsequent,

        @JsonProperty(required = true)
        List<Complexity> antecedentComplexity,

        @JsonProperty(required = true)
        List<Complexity> consequentComplexity,

        @JsonProperty(required = true)
        List<Connective> connective,

        @JsonProperty(required = true)
        CharacterSet characterSet,

        @JsonProperty(required = true)
        Generator generator
) {
}

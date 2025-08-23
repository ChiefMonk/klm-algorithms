package uct.cs.klm.algorithms.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import uct.cs.klm.algorithms.enums.Algorithm;
import uct.cs.klm.algorithms.enums.InferenceOperator;

import java.util.List;

public record EvaluationQuery(
        @JsonProperty(required = true)
        EvaluationQueryParams parameters,

        @JsonProperty(required = true)
        InferenceOperator inferenceOperator,

        @JsonProperty(required = true)
        List<Algorithm> algorithms
) {
}

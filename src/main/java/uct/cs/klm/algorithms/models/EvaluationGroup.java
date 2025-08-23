package uct.cs.klm.algorithms.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import uct.cs.klm.algorithms.enums.InferenceOperator;

import java.util.List;

public record EvaluationGroup(
        @JsonProperty(required = true)
        String title,

        @JsonProperty(required = true)
        InferenceOperator inferenceOperator,

        @JsonProperty(required = true)
        List<EvaluationData> data
) {
}

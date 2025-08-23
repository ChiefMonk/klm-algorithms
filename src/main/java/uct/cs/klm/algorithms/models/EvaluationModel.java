package uct.cs.klm.algorithms.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record EvaluationModel(
        @JsonProperty(required = true)
        EvaluationQuery query,

        @JsonProperty(required = true)
        List<EvaluationGroup> data
) {
}

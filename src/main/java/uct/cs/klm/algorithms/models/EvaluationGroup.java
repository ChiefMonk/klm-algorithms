package uct.cs.klm.algorithms.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import uct.cs.klm.algorithms.enums.InferenceOperator;

import java.util.List;

/**
 * This class models an evaluation group for a given query set.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public record EvaluationGroup(
        @JsonProperty(required = true)
        String title,

        @JsonProperty(required = true)
        InferenceOperator inferenceOperator,

        @JsonProperty(required = true)
        List<EvaluationData> data
) {
}

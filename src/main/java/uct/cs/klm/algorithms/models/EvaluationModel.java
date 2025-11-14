package uct.cs.klm.algorithms.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * This class models an evaluation model for a given query set.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public record EvaluationModel(
        @JsonProperty(required = true)
        EvaluationQuery query,

        @JsonProperty(required = true)
        List<EvaluationGroup> data
) {
}

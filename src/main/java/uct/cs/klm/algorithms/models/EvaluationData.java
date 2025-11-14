package uct.cs.klm.algorithms.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import uct.cs.klm.algorithms.enums.Algorithm;
import uct.cs.klm.algorithms.enums.InferenceOperator;

import java.util.EnumMap;
import java.util.Map;

/**
 * This class models evaluation data for a given query set.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public class EvaluationData {
    @JsonProperty(required = true)
    private String querySetLabel;

    @JsonProperty(required = true)
    private InferenceOperator inferenceOperator;

    @JsonProperty(required = true)
    private Map<Algorithm, Double> results;

    public EvaluationData() {
        this("", InferenceOperator.RationalClosure);
    }

    public EvaluationData(String querySetLabel, InferenceOperator inferenceOperator) {
        this.querySetLabel = querySetLabel;
        this.inferenceOperator = inferenceOperator;
        this.results = new EnumMap<>(Algorithm.class);
    }

    public void addResult(Algorithm algorithm, Double time) {
        results.putIfAbsent(algorithm, time);
    }

    public String getQuerySetLabel() {
        return querySetLabel;
    }

    public void setQuerySetLabel(String querySetLabel) {
        this.querySetLabel = querySetLabel;
    }

    public InferenceOperator getInferenceOperator() {
        return inferenceOperator;
    }

    public void setInferenceOperator(InferenceOperator inferenceOperator) {
        this.inferenceOperator = inferenceOperator;
    }

    public Map<Algorithm, Double> getResults() {
        return results;
    }

    public void setResults(Map<Algorithm, Double> results) {
        this.results = results;
    }
}

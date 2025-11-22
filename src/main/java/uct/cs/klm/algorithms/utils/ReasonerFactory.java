package uct.cs.klm.algorithms.utils;

import uct.cs.klm.algorithms.relevant.*;
import uct.cs.klm.algorithms.lexicographic.*;
import uct.cs.klm.algorithms.rational.*;
import uct.cs.klm.algorithms.enums.*;
import uct.cs.klm.algorithms.explanation.*;
import uct.cs.klm.algorithms.services.*;

/**
 * This class represents a reasoner factory for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

public class ReasonerFactory {

    public static ReasonerType createReasonerType(String type) {
        return switch (type) {
            case "rational" ->
                ReasonerType.RationalClosure;
            case "ratc" ->
                ReasonerType.RationalClosure;
            case "lexical" ->
                ReasonerType.LexicographicClosure;
            case "lexc" ->
                ReasonerType.LexicographicClosure;
            case "mrelc" ->
                ReasonerType.MinimalRelevantClosure;
            case "brelc" ->
                ReasonerType.BasicRelevantClosure;

            default ->
                throw new IllegalArgumentException("Unknown reasoner: " + type);
        };
    }

    public static IReasonerService createEntailment(ReasonerType reasonerType) {
        return switch (reasonerType) {
            case ReasonerType.RationalClosure ->
                new RationalClosureReasonerImpl();
            case ReasonerType.LexicographicClosure ->
                new LexicographicClosureReasonerImpl();
            case ReasonerType.MinimalRelevantClosure ->
                new MinimalRelevantReasonerImpl();
            case ReasonerType.BasicRelevantClosure ->
                new BasicRelevantReasonerImpl();

            default ->
                throw new IllegalArgumentException("Unknown reasoner: " + reasonerType);
        };
    }

    public static IJustificationService createJustification(ReasonerType reasonerType) {

        return switch (reasonerType) {
            case ReasonerType.RationalClosure ->
                new RationalClosureJustificationService();
            case ReasonerType.LexicographicClosure ->
                new LexicographicClosureJustificationService();
            case ReasonerType.MinimalRelevantClosure ->
                new MinimalRelevantJustificationService();
            case ReasonerType.BasicRelevantClosure ->
                new BasicRelevantJustificationService();

            default ->
                throw new IllegalArgumentException("Unknown reasoner: " + reasonerType);
        };
    }
}

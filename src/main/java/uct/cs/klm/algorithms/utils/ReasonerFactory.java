package uct.cs.klm.algorithms.utils;

import uct.cs.klm.algorithms.enums.*;
import uct.cs.klm.algorithms.explanation.*;
import uct.cs.klm.algorithms.services.*;

public class ReasonerFactory {

     public static ReasonerType createReasonerType(String type) {
        return switch (type) {
            case "rational" ->
                ReasonerType.RationalClosure;
            case "lexical" ->
               ReasonerType.LexicographicClosure;
            case "relevant" ->
                ReasonerType.RelevantClosure;

            default ->
                throw new IllegalArgumentException("Unknown reasoner: " + type);
        };
    }
     
    public static IReasonerService createEntailment(ReasonerType reasonerType) {
        return switch (reasonerType) {
            case ReasonerType.RationalClosure ->
                new RationalReasonerImpl();
            case ReasonerType.LexicographicClosure ->
                new LexicalReasonerImpl();
            case ReasonerType.RelevantClosure ->
                new RelevantReasonerImpl();

            default ->
                throw new IllegalArgumentException("Unknown reasoner: " + reasonerType);
        };
    }

    public static IJustificationService createJustification(ReasonerType reasonerType) {
        
        return switch (reasonerType) {
            case ReasonerType.RationalClosure ->
                 new RationalJustificationService();
            case ReasonerType.LexicographicClosure ->
                 new RationalJustificationService();
            case ReasonerType.RelevantClosure ->
                new RationalJustificationService();

            default ->
                throw new IllegalArgumentException("Unknown reasoner: " + reasonerType);
        };               
    }
}
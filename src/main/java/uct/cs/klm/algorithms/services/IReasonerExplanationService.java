package uct.cs.klm.algorithms.services;

import uct.cs.klm.algorithms.models.ModelEntailment;

/**
 * This interface represents a reasoner explanation service for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

public interface IReasonerExplanationService<T> {
    T generateExplanation(ModelEntailment entailment);
}

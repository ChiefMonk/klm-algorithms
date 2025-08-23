package uct.cs.klm.algorithms.services;

import uct.cs.klm.algorithms.models.ModelEntailment;

public interface IReasonerExplanationService<T> {
    T generateExplanation(ModelEntailment entailment);
}

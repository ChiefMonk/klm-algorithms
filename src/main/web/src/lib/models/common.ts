enum InferenceOperator {
  RationalClosure = "RationalClosure",
  LexicographicClosure = "LexicographicClosure",
  BasicRelevantClosure = "BasicRelevantClosure",
  MinimalRelevantClosure = "MinimalRelevantClosure",
}

enum Algorithm {
  Naive = "Naive",
  Binary = "Binary",
  Ternary = "Ternary",
  PowerSet = "PowerSet",
}

enum QueryType {
  Entailment,
  Justification,
  Evaluation,
}

export { InferenceOperator, Algorithm, QueryType };

enum InferenceOperator {
  RationalClosure = "RationalClosure",
  LexicographicClosure = "LexicographicClosure",
  BasicRelevantClosure = "BasicRelevantClosure",
  MinimalRelevantClosure = "MinimalRelevantClosure",
}

enum Algorithm {
  Naive = "Naive",
  NaiveIndex = "NaiveIndex",
  Binary = "Binary",
  BinaryIndex = "BinaryIndex",
  Ternary = "Ternary",
  TernaryIndex = "TernaryIndex",
  PowerSet = "PowerSet",
}

enum QueryType {
  Entailment,
  Justification,
  Evaluation,
}

export { InferenceOperator, Algorithm, QueryType };

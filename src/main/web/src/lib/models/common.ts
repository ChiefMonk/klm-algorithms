/**
 * An enum representing an inference operator.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2025-01-26
 */
enum InferenceOperator {
  RationalClosure = "RationalClosure",
  LexicographicClosure = "LexicographicClosure",
  BasicRelevantClosure = "BasicRelevantClosure",
  MinimalRelevantClosure = "MinimalRelevantClosure",
}

/**
 * An enum representing an algorithm.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2025-01-26
 */
enum Algorithm {
  Naive = "Naive",
  NaiveIndex = "NaiveIndex",
  Binary = "Binary",
  BinaryIndex = "BinaryIndex",
  Ternary = "Ternary",
  TernaryIndex = "TernaryIndex",
  PowerSet = "PowerSet",
  PowerSetCombined = "PowerSetCombined",
  PowerSetSubset = "PowerSetSubset",
}

/**
 * An enum representing a query type.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2025-01-26
 */
enum QueryType {
  Entailment,
  Justification,
  Evaluation,
}

/**
 * An interface representing a common model.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2025-01-26
 */
export { InferenceOperator, Algorithm, QueryType };
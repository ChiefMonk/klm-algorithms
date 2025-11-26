import { InferenceOperator, Algorithm } from "./common";

/**
 * An enum representing a distribution type.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2025-01-26
 */
enum Distribution {
  Uniform = "Uniform",
  Linear = "Linear",
  ReversedLinear = "ReversedLinear",
  Exponential  = "Exponential",
  ReversedExponential = "ReversedExponential",
}

/**
 * An enum representing a complexity type.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2025-01-26
 */
enum Complexity {
  Low = "Low",
  Medium = "Medium",
  High = "High",
}

/**
 * An enum representing a connective type.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2025-01-26
 */
enum Connective {
  Disjunction = "Disjunction",
  Conjunction = "Conjunction",
  Implication = "Implication",
  BiImplication = "BiImplication",
}

/**
 * An enum representing a character set type.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2025-01-26
 */
enum CharacterSet {
  LowerLatin = "LowerLatin" 
}

enum Generator {
  Optimized = "Optimized",
  Standard = "Standard", 
}

interface IEvaluationQueryParams {
  numberOfRanks: number;
  distribution: Distribution;
  numberOfDefeasibleImplications: number;
  simpleDiOnly: boolean;
  reuseConsequent: boolean;
  antecedentComplexity: Complexity[];
  consequentComplexity: Complexity[];
  connective: Connective[];
  characterSet: CharacterSet;
  generator: Generator;
}

interface IEvaluationQuery {
  parameters: IEvaluationQueryParams;
  inferenceOperator: InferenceOperator;
  algorithms: Algorithm[];
}

interface IEvaluationData {
  querySetLabel: string;
  inferenceOperator: InferenceOperator;
  results: Record<Algorithm, number>;
}

interface IEvaluationGroup {
  title: string;
  inferenceOperator: InferenceOperator;
  data: IEvaluationData[];
}

interface IEvaluationModel {
  query: IEvaluationQuery;
  data: IEvaluationGroup[];
}

export { Distribution, Complexity, Connective, CharacterSet, Generator };
export type {
  IEvaluationQuery,
  IEvaluationData,
  IEvaluationGroup,
  IEvaluationQueryParams,
  IEvaluationModel,
};

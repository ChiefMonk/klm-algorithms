import { InferenceOperator, Algorithm } from "./common";

enum Distribution {
  Uniform = "Uniform",
  Linear = "Linear",
  ReversedLinear = "ReversedLinear",
  Exponential  = "Exponential",
  ReversedExponential = "ReversedExponential",
}

enum Complexity {
  Low = "Low",
  Medium = "Medium",
  High = "High",
}

enum Connective {
  Disjunction = "Disjunction (∨)",
  Conjunction = "Conjunction (∧)",
  Implication = "Implication (→)",
  BiImplication = "Bi-implication (↔)",
}

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

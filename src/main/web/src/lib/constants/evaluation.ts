// Evaluation-related constants and validation messages.

import {
  CharacterSet,
  Complexity,
  Connective,
  Distribution,
  Generator,
} from "../models";

const NUMBER_OF_RANKS_MIN = 1;
const NUMBER_OF_RANKS_MAX = 10;
const NUMBER_OF_DI_MAX = 50;

/**
 * Constants and error messages for evaluation.
 */
type EvaluationConstantsType = {
  NUMBER_OF_RANKS_MIN: number;
  NUMBER_OF_RANKS_MIN_MESSAGE: string;
  NUMBER_OF_RANKS_MAX: number;
  NUMBER_OF_RANKS_MAX_MESSAGE: string;
  NUMBER_OF_DI_MAX: number;
  NUMBER_OF_DI_MAX_MESSAGE: string;
  NUMBER_OF_DI_MIN_MESSAGE: (min: number) => string;
  INFERENCE_OPERATOR_REQUIRED_MESSAGE: string;
  ALGORITHM_REQUIRED_MESSAGE: string;
  COMPLEXITY_REQUIRED_MESSAGE: string;
  CONNECTIVE_REQUIRED_MESSAGE: string;
};

export const EvaluationConstants: EvaluationConstantsType = {
  /** Minimum number of ranks allowed in evaluation. */
  NUMBER_OF_RANKS_MIN,

  /** Error message shown when rank is below minimum. */
  NUMBER_OF_RANKS_MIN_MESSAGE: `Must be at least ${NUMBER_OF_RANKS_MIN}`,

  /** Maximum number of ranks allowed in evaluation. */
  NUMBER_OF_RANKS_MAX,

  /** Error message shown when rank exceeds maximum. */
  NUMBER_OF_RANKS_MAX_MESSAGE: `Must be at most ${NUMBER_OF_RANKS_MAX}`,

  /** Maximum number of defeasible implications allowed. */
  NUMBER_OF_DI_MAX,

  /** Error message shown when defeasible implications exceed maximum. */
  NUMBER_OF_DI_MAX_MESSAGE: `Must be at most ${NUMBER_OF_DI_MAX}`,

  /**
   * Generates an error message for when the number of DIs
   * is below the number of ranks.
   *
   * @param min - Usually the number of ranks
   * @returns A formatted error message
   */
  NUMBER_OF_DI_MIN_MESSAGE: (min: number): string =>
    `Must be greater or equal to number of ranks (${min})`,

  /** Error message shown when inference operator is not selected */
  INFERENCE_OPERATOR_REQUIRED_MESSAGE: "Please select an inference operator",

  /** Error message shown when algorithm is not selected */
  ALGORITHM_REQUIRED_MESSAGE: "At least one algorithm must be selected",

  /** Error message shown when complexity is not selected */
  COMPLEXITY_REQUIRED_MESSAGE: "At least one complexity must be selected",

  /** Error message shown when connective is not selected */
  CONNECTIVE_REQUIRED_MESSAGE: "At least one connective must be selected",
};

export const DISTRIBUTIONS: ReadonlyMap<Distribution, string> = new Map([
  [Distribution.Flat, "Flat Distribution"],
  [Distribution.LinearGrowth, "Linear Growth"],
  [Distribution.LinearDecline, "Linear Decline"],
  [Distribution.Random, "Random Distribution"],
]);

export const COMPLEXITIES: ReadonlyMap<Complexity, string> = new Map([
  [Complexity.Low, "Low"],
  [Complexity.Medium, "Medium"],
  [Complexity.High, "High"],
]);

export const CONNECTIVES: ReadonlyMap<Connective, string> = new Map([
  [Connective.Disjunction, "Disjunction"],
  [Connective.Conjunction, "Conjunction"],
  [Connective.Implication, "Implication"],
  [Connective.BiImplication, "Bi-implication"],
]);

export const CHARACTER_SETS: ReadonlyMap<CharacterSet, string> = new Map([
  [CharacterSet.LowerLatin, "Lowercase Latin"],
  [CharacterSet.UpperLatin, "Uppercase Latin"],
  [CharacterSet.AltLatin, "Alternative Latin"],
  [CharacterSet.Greek, "Greek Letters"],
]);

export const GENERATORS: ReadonlyMap<Generator, string> = new Map([
  [Generator.Standard, "Standard Generator"],
  [Generator.Optimized, "Optimized Generator"],
]);

import { Algorithm, InferenceOperator } from "../models";

/**
 * change-app-screen-size
 * Percentage of the screen size an application takes.
 */
export const SCREEN_WIDTH_SIZE = 95;

/**
 * Application version (Date).
 */
export const APP_VERSION = "v2.11.2";

export const INFERENCE_OPERATORS: ReadonlyMap<InferenceOperator, string> =
  new Map([
    [InferenceOperator.RationalClosure, "Rational Closure"],
    [InferenceOperator.LexicographicClosure, "Lexicographic Closure"],
    [InferenceOperator.BasicRelevantClosure, "Basic Relevant Closure"],
    [InferenceOperator.MinimalRelevantClosure, "Minimal Relevant Closure"],
  ]);

export const ALGORITHMS: ReadonlyMap<Algorithm, string> = new Map([
  [Algorithm.Naive, "Naive"],
  [Algorithm.NaiveIndex, "Naive Index"],
  [Algorithm.Binary, "Binary"],
  [Algorithm.BinaryIndex, "Binary Index"],
  [Algorithm.Ternary, "Ternary"],
  [Algorithm.TernaryIndex, "Ternary Index"],
  [Algorithm.PowerSet, "Power Set"],
  [Algorithm.PowerSetCombined, "Power Set Combined"],
  [Algorithm.PowerSetSubset, "Power Set Subset"],
]);

export const ALGORITHMS_RATC: ReadonlyMap<Algorithm, string> = new Map([
  [Algorithm.Naive, "RatCNaive: The Naive Implementation"],
  [
    Algorithm.NaiveIndex,
    "RatCNaiveIndexIndexing: The Naive with Indexing Implementation",
  ],
  [Algorithm.Binary, "RatCBinary: Binary Search Implementation"],
  [
    Algorithm.BinaryIndex,
    "RatCBinaryIndexing: Binary Search with Indexing Implementation",
  ],
  [Algorithm.Ternary, "RatCTernary: Ternary Search Implementation"],
  [
    Algorithm.TernaryIndex,
    "RatCTernaryIndexing: Ternary Search with Indexing Implementation",
  ],
]);

export const ALGORITHMS_LEXC: ReadonlyMap<Algorithm, string> = new Map([
  [Algorithm.Naive, "LexCNaive: The Naive Implementation"],
  [
    Algorithm.PowerSetCombined,
    "LexCPowerSetCombined: The Combined Knowledge Bases Implementation",
  ],
  [
    Algorithm.PowerSetSubset,
    "LexCPowerSetSubset: The Subset Knowledge Bases Implementation",
  ],
  [Algorithm.Binary, "LexCBinary: Binary Search Implementation"],
  [Algorithm.Ternary, "LexCTernary: Ternary Search Implementation"],
]);

export const ALGORITHMS_RELC: ReadonlyMap<Algorithm, string> = new Map([
  [Algorithm.Naive, "RelCNaive: The Naive Implementation"],
  [
    Algorithm.PowerSetCombined,
    "RelCPowerSetCombined: The Combined Knowledge Bases Implementation",
  ],
  [
    Algorithm.PowerSetSubset,
    "RelCPowerSetSubset: The Subset Knowledge Bases Implementation",
  ],
  [Algorithm.Binary, "RelCBinary: Binary Search Implementation"],
  [Algorithm.Ternary, "RelCTernary: Ternary Search Implementation"],
]);
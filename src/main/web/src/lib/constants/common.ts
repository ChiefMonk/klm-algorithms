import { Algorithm, InferenceOperator } from "../models";

/**
 * change-app-screen-size
 * Percentage of the screen size an application takes.
 */
export const SCREEN_WIDTH_SIZE = 95;

/**
 * Application version (Date).
 */
export const APP_VERSION = "v2025/06/22";

export const INFERENCE_OPERATORS: ReadonlyMap<InferenceOperator, string> =
  new Map([
    [InferenceOperator.RationalClosure, "Rational Closure"],
    [InferenceOperator.LexicographicClosure, "Lexicographic Closure"],
    [InferenceOperator.BasicRelevantClosure, "Basic Relevant Closure"],
    [InferenceOperator.MinimalRelevantClosure, "Minimal Relevant Closure"],
  ]);

export const ALGORITHMS: ReadonlyMap<Algorithm, string> = new Map([
  [Algorithm.Naive, "Naive Algorithm"],
  [Algorithm.Binary, "Binary Search Algorithm"],
  [Algorithm.Ternary, "Ternary Search Algorithm"],
  [Algorithm.PowerSet, "Power Set Algorithm"],
]);

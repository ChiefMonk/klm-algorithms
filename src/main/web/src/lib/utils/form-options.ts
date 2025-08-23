import {
  DISTRIBUTIONS,
  CHARACTER_SETS,
  GENERATORS,
  ALGORITHMS,
  INFERENCE_OPERATORS,
} from "../constants";
import {
  Complexity,
  Connective,
  InferenceOperator,
  Algorithm,
  Distribution,
  CharacterSet,
  Generator,
  IFormFieldOption,
} from "../models";

/**
 * Generates select options for query complexities.
 *
 * Each option includes the enum value as both label and value.
 *
 * @returns {IFormFieldOption<Complexity>[]} An array of select options for query complexity levels.
 */
export const complexityOptions: IFormFieldOption<Complexity>[] = Object.values(
  Complexity
).map((complexity) => ({
  label: complexity,
  value: complexity,
}));

/**
 * Generates select options for logical connectives.
 *
 * Each option includes the enum value as both label and value.
 *
 * @returns {IFormFieldOption<Connective>[]} An array of select options for logical connectives.
 */
export const connectiveOptions: IFormFieldOption<Connective>[] = Object.values(
  Connective
).map((connective) => ({
  label: connective,
  value: connective,
}));

/**
 * Generates select options for inference operators.
 *
 * Each option includes the enum value as both label and value.
 *
 * @returns {IFormFieldOption<InferenceOperator>[]} An array of select options for inference operators.
 */
export const operatorOptions: IFormFieldOption<InferenceOperator>[] =
  Object.values(InferenceOperator).map((operator) => ({
    label: INFERENCE_OPERATORS.get(operator) || InferenceOperator[operator],
    value: operator,
  }));

/**
 * Generates select options for supported evaluation algorithms.
 *
 * Each option includes the enum value as both label and value.
 *
 * @returns {IFormFieldOption<Algorithm>[]} An array of select options for evaluation algorithms.
 */
export const algorithmOptions: IFormFieldOption<Algorithm>[] = Object.values(
  Algorithm
).map((algorithm) => ({
  label: ALGORITHMS.get(algorithm) || Algorithm[algorithm],
  value: algorithm,
}));

/**
 * Generates select options for distributions.
 *
 * Uses friendly labels from `DISTRIBUTIONS`, falling back to the enum name if not found.
 *
 * @returns {IFormFieldOption<Distribution>[]} An array of select options for distributions.
 */
export const distributionOptions: IFormFieldOption<Distribution>[] =
  Object.values(Distribution).map((distribution) => ({
    label: DISTRIBUTIONS.get(distribution) || Distribution[distribution],
    value: distribution,
  }));

/**
 * Generates select options for character sets.
 *
 * Uses friendly labels from `CHARACTER_SETS`, falling back to the enum name if not found.
 *
 * @returns {IFormFieldOption<CharacterSet>[]} An array of select options for character sets.
 */
export const characterSetOptions: IFormFieldOption<CharacterSet>[] =
  Object.values(CharacterSet).map((characterSet) => ({
    label: CHARACTER_SETS.get(characterSet) || CharacterSet[characterSet],
    value: characterSet,
  }));

/**
 * Generates select options for generators.
 *
 * Uses friendly labels from `GENERATORS`, falling back to the enum name if not found.
 *
 * @returns {IFormFieldOption<Generator>[]} An array of select options for generators.
 */
export const generatorOptions: IFormFieldOption<Generator>[] = Object.values(
  Generator
).map((generator) => ({
  label: GENERATORS.get(generator) || Generator[generator],
  value: generator,
}));

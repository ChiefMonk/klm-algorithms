import { IEvaluationModel } from "../models";

/**
 * The key used to store evaluation data in `localStorage`.
 *
 * @constant {string}
 */
const EVALUATION_STORAGE_KEY: string = "evaluation";

/**
 * Retrieves the evaluation model from `localStorage`.
 *
 * @returns {IEvaluationModel | null} The parsed evaluation model if it exists, or `null` if not found.
 */
function getEvaluation(): IEvaluationModel | null {
  const storedValue = localStorage.getItem(EVALUATION_STORAGE_KEY);
  return JSON.parse(storedValue ?? "null") as IEvaluationModel | null;
}

/**
 * Saves the given evaluation model to `localStorage`.
 *
 * @param {IEvaluationModel} data - The evaluation data to store.
 */
function saveEvaluation(data: IEvaluationModel): void {
  localStorage.setItem(EVALUATION_STORAGE_KEY, JSON.stringify(data));
}

/**
 * Removes the stored evaluation model from `localStorage`.
 */
function deleteEvaluation(): void {
  localStorage.removeItem(EVALUATION_STORAGE_KEY);
}

export { getEvaluation, saveEvaluation, deleteEvaluation };

import { IEvaluationModel, IEvaluationQuery } from "../models";
import { api, createApiError } from "./api";

/**
 * Sends an evaluation query to the backend API and returns the evaluation result.
 *
 * @param {IEvaluationQuery} query The query parameters used to perform the evaluation.
 * @returns {Promise<IEvaluationModel>} A promise resolving to the evaluation result.
 * @throws {ErrorModel} If the request fails, a structured error is thrown.
 */
async function evaluate(query: IEvaluationQuery): Promise<IEvaluationModel> {
  const endpoint = "/evaluation";

  try {
    console.info(`[API][POST] ${endpoint}`, query);

    const response = await api.post(endpoint, query);
    const data = response.data as IEvaluationModel;

    console.info(`[API][SUCCESS] ${endpoint}`, {
      status: response.status,
      data,
    });
    return data;
  } catch (error) {
    console.error(`[API][ERROR] ${endpoint}`, error);
    throw createApiError(error);
  }
}

/**
 * Sends a file to the backend API and returns the evaluation result.
 *
 * @param {FormData} formData The form data containing the file.
 * @returns {Promise<IEvaluationModel>} A promise resolving to the evaluation result.
 * @throws {ErrorModel} If the request fails, a structured error is thrown.
 */
async function importEvaluation(formData: FormData): Promise<IEvaluationModel> {
  const endpoint = "/evaluation/import";

  try {
    console.info(`[API][POST] ${endpoint}`, formData);

    const response = await api.post(endpoint, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    const data = response.data as IEvaluationModel;

    console.info(`[API][SUCCESS] ${endpoint}`, {
      status: response.status,
      data,
    });
    return data;
  } catch (error) {
    console.error(`[API][ERROR] ${endpoint}`, error);
    throw createApiError(error);
  }
}

/**
 * Sends a file to the backend API and returns the evaluation result.
 *
 * @param {IEvaluationModel} model The form data containing the file.
 * @throws {ErrorModel} If the request fails, a structured error is thrown.
 */
async function exportEvaluation(model: IEvaluationModel): Promise<void> {
  const endpoint = "/evaluation/export";

  try {
    console.info(`[API][POST] ${endpoint}`, model);

    const response = await api.post(endpoint, model, { responseType: "blob" });

    console.info(`[API][SUCCESS] ${endpoint}`, {
      status: response.status,
    });

    // Extract filename from Content-Disposition header
    const disposition = response.headers["content-disposition"];
    let filename = "evaluations.json";

    if (disposition && disposition.includes("filename=")) {
      const filenameMatch = disposition.match(/filename="(.+)"/);
      if (filenameMatch && filenameMatch.length > 1) {
        filename = filenameMatch[1];
      }
    }

    // Create a download like and trigger it
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", filename);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error(`[API][ERROR] ${endpoint}`, error);
    throw createApiError(error);
  }
}

export const evaluateApi = {
  evaluate,
  importEvaluation,
  exportEvaluation,
} as const;

import axios, { AxiosError } from "axios";
import { ErrorModel } from "../models";

/**
 * Axios instance pre-configured with default base URL and JSON headers.
 *
 * - `baseURL` is set to `/api`, which is useful when calling backend routes from the frontend.
 * - `Content-Type` is set to `'application/json'` by default, but can be overridden per request.
 *
 * @example
 * const response = await api.get('/knowledge-base');
 * const data = await api.post('/evaluate', data);
 */
const api = axios.create({
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Converts any unknown error (typically thrown by Axios requests) into a standardized `ErrorModel`.
 *
 * - Handles known Axios errors (e.g., network issues, 4xx responses with a message).
 * - Fallbacks to a generic internal server error if the error is not Axios-related or lacks a response.
 *
 * @param {unknown} error - The error thrown during an HTTP request.
 * @returns {ErrorModel} A structured error object for consistent error handling in the app.
 *
 * @example
 * try {
 *   await api.get('/users');
 * } catch (error) {
 *   const friendlyError = createApiError(error);
 *   showToast(friendlyError.message);
 * }
 */
function createApiError(error: unknown): ErrorModel {
  if (error instanceof AxiosError) {
    if (
      error.response &&
      error.response.status <= 500 &&
      error.response.data?.message
    ) {
      return ErrorModel.create(error.response.data);
    }
  }
  return ErrorModel.create({
    code: 500,
    description: "Internal Server Error",
    message:
      "We're currently experiencing issues with our server. Please try again later.",
  });
}

export { api, createApiError };

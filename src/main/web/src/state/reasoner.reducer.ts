import { ReasonerState, ReasonerAction } from "./reasoner.types";
import {
  getQueryInput,
  getEntailmentQueryResult,
  getEvaluation,
} from "@/lib/storage";
import { QueryType } from "@/lib/models";

export const initialReasonerState: ReasonerState = {
  queryInput: getQueryInput(),
  entailmentQueryResult: getEntailmentQueryResult(),
  queryType: QueryType.Entailment,
  inputPending: false,
  resultPending: false,
  evaluation: getEvaluation(),
};

export function reasonerReducer(
  state: ReasonerState,
  action: ReasonerAction
): ReasonerState {
  switch (action.type) {
    case "SET_QUERY_INPUT":
      return { ...state, queryInput: action.payload };
    case "SET_ENTAILMENT_QUERY_RESULT":
      return { ...state, entailmentQueryResult: action.payload };
    case "SET_QUERY_TYPE":
      return { ...state, queryType: action.payload };
    case "SET_INPUT_PENDING":
      return { ...state, inputPending: action.payload };
    case "SET_RESULT_PENDING":
      return { ...state, resultPending: action.payload };
    case "SET_EVALUATION":
      return { ...state, evaluation: action.payload };
    case "CLEAR_ALL":
      return { ...initialReasonerState };
    default:
      return state;
  }
}

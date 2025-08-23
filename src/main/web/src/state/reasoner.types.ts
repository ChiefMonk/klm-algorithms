import { IEvaluationModel, QueryType } from "@/lib/models";
import { IEntailementQueryResult, QueryInput } from "@/lib/storage";

export interface ReasonerState {
  queryInput: QueryInput | null;
  entailmentQueryResult: IEntailementQueryResult | null;
  queryType: QueryType;
  inputPending: boolean;
  resultPending: boolean;
  evaluation: IEvaluationModel | null;
}

export type ReasonerAction =
  | { type: "SET_QUERY_INPUT"; payload: QueryInput | null }
  | {
      type: "SET_ENTAILMENT_QUERY_RESULT";
      payload: IEntailementQueryResult | null;
    }
  | { type: "SET_QUERY_TYPE"; payload: QueryType }
  | { type: "SET_INPUT_PENDING"; payload: boolean }
  | { type: "SET_RESULT_PENDING"; payload: boolean }
  | { type: "SET_EVALUATION"; payload: IEvaluationModel | null }
  | { type: "CLEAR_ALL" };

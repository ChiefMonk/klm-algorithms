import {
  createContext,
  useContext,
  useReducer,
  ReactNode,
  useCallback,
  useEffect,
} from "react";
import { reasonerReducer, initialReasonerState } from "./reasoner.reducer";
import { ReasonerState, ReasonerAction } from "./reasoner.types";
import { useToast } from "@/components/ui/use-toast";
import * as api from "@/lib/api";
import * as storage from "@/lib/storage";
import {
  QueryType,
  InferenceOperator,
  KbGenerationInput,
  IEvaluationQuery,
  IEvaluationModel,
} from "@/lib/models";
import { useRouteQueryType } from "@/hooks/use-route-query-type";
import { ErrorModel } from "@/lib/models";

interface ReasonerContextValue extends ReasonerState {
  dispatch: React.Dispatch<ReasonerAction>;
  fetchQueryInput: () => Promise<void>;
  fetchEntailmentQueryResult: (ops?: InferenceOperator[]) => Promise<void>;
  updateFormula: (formula: string) => Promise<void>;
  createInputKnowledgeBase: (formulas: string[]) => Promise<void>;
  generateKnowledgeBase: (data: KbGenerationInput) => Promise<void>;
  createFileKnowledgeBase: (formulas: FormData) => Promise<void>;
  exportEvaluation: () => Promise<void>;
  importEvaluation: (formData: FormData) => Promise<void>;
  getEvaluation: (query: IEvaluationQuery) => Promise<void>;
  deleteEvaluation: () => void;
  deleteEntailment: () => void;
}

const ReasonerContext = createContext<ReasonerContextValue | null>(null);

export const ReasonerProvider = ({ children }: { children: ReactNode }) => {
  const [state, dispatch] = useReducer(reasonerReducer, initialReasonerState);
  const { toast } = useToast();
  const routeQueryType = useRouteQueryType();

  // Sync queryType with route
  useEffect(() => {
    dispatch({ type: "SET_QUERY_TYPE", payload: routeQueryType });
  }, [routeQueryType]);

  const toastError = useCallback(
    (error: unknown) => {
      const message =
        error instanceof ErrorModel
          ? error.message
          : error instanceof Error
          ? error.message
          : "Oops! Something went wrong.";
      toast({ description: message, variant: "destructive" });
    },
    [toast]
  );

  const fetchQueryInput = useCallback(async () => {
    dispatch({ type: "SET_INPUT_PENDING", payload: true });
    try {
      const queryFormula = await api.fetchQueryFormula();
      const knowledgeBase = await api.getDefaultKnowledgeBase();
      const signature = await api.getSignatureKnowledgeBase(knowledgeBase);
      const data = { queryFormula, knowledgeBase, signature };
      dispatch({ type: "SET_QUERY_INPUT", payload: data });
      storage.saveQueryInput(data);
    } catch (error) {
      toastError(error);
    }
    dispatch({ type: "SET_INPUT_PENDING", payload: false });
  }, [toastError]);

  const fetchEntailmentQueryResult = useCallback(
    async (inferenceOperators: InferenceOperator[] = []) => {
      dispatch({ type: "SET_RESULT_PENDING", payload: true });
      try {
        if (
          state.queryInput?.knowledgeBase &&
          state.queryInput.queryFormula &&
          (state.queryType === QueryType.Entailment ||
            state.queryType === QueryType.Justification) &&
          inferenceOperators.length > 0
        ) {
          const baseRank = await api.fetchBaseRank(
            state.queryInput.knowledgeBase
          );
          const baseRankExplanation = await api.fetchBaseRankExplanation(
            baseRank
          );

          const rationalEntailment = inferenceOperators.includes(
            InferenceOperator.RationalClosure
          )
            ? await api.fetchRationalEntailment(
                state.queryInput.queryFormula,
                baseRank
              )
            : null;

          const lexicalEntailment = inferenceOperators.includes(
            InferenceOperator.LexicographicClosure
          )
            ? await api.fetchLexicalEntailment(
                state.queryInput.queryFormula,
                baseRank
              )
            : null;

          const basicRelevantEntailment = inferenceOperators.includes(
            InferenceOperator.BasicRelevantClosure
          )
            ? await api.fetchBasicRelevantEntailment(
                state.queryInput.queryFormula,
                baseRank
              )
            : null;

            const minimalRelevantEntailment = inferenceOperators.includes(
              InferenceOperator.MinimalRelevantClosure
            )
              ? await api.fetchMinimalRelevantEntailment(
                  state.queryInput.queryFormula,
                  baseRank
                )
              : null;
  

          const rationalExplanation =
            rationalEntailment == null
              ? null
              : await api.fetchRationalExplanation(rationalEntailment);

          const data = {
            inferenceOperators,
            baseRank,
            baseRankExplanation,
            rationalEntailment,
            lexicalEntailment,
            basicRelevantEntailment,
            minimalRelevantEntailment,
            rationalExplanation,
          };

          dispatch({ type: "SET_ENTAILMENT_QUERY_RESULT", payload: data });
          storage.saveEntailmentQueryResult(data);
        }
      } catch (error) {
        toastError(error);
      }
      dispatch({ type: "SET_RESULT_PENDING", payload: false });
    },
    [state.queryInput, state.queryType, toastError]
  );

  const updateFormula = useCallback(
    async (formula: string) => {
      dispatch({ type: "SET_INPUT_PENDING", payload: true });
      try {
        const queryFormula = await api.createQueryFormula(formula);
        dispatch({ type: "SET_ENTAILMENT_QUERY_RESULT", payload: null });
        if (typeof queryFormula === "string") {
          const updatedInput = {
            knowledgeBase: state.queryInput?.knowledgeBase || [],
            signature: state.queryInput?.signature || [],
            queryFormula,
          };
          dispatch({ type: "SET_QUERY_INPUT", payload: updatedInput });
          storage.saveQueryInput(updatedInput);
        }
      } catch (error) {
        toastError(error);
      }
      dispatch({ type: "SET_INPUT_PENDING", payload: false });
    },
    [state.queryInput, toastError]
  );

  const createInputKnowledgeBase = useCallback(
    async (formulas: string[]) => {
      dispatch({ type: "SET_INPUT_PENDING", payload: true });
      try {
        const kb = await api.createInputKnowledgeBase(formulas);
        const signatures = await api.getSignatureKnowledgeBase(kb);
        const updatedInput = {
          knowledgeBase: kb,
          signature: signatures,
          queryFormula: state.queryInput?.queryFormula || "",
        };
        dispatch({ type: "SET_QUERY_INPUT", payload: updatedInput });
        dispatch({ type: "SET_ENTAILMENT_QUERY_RESULT", payload: null });

        storage.saveQueryInput(updatedInput);
      } catch (error) {
        toastError(error);
      }
      dispatch({ type: "SET_INPUT_PENDING", payload: false });
    },
    [state.queryInput, toastError]
  );

  const generateKnowledgeBase = useCallback(
    async (data: KbGenerationInput) => {
      dispatch({ type: "SET_INPUT_PENDING", payload: true });
      try {
        const kb = await api.getGeneratedKnowledgeBase(data);
        const signature = await api.getSignatureKnowledgeBase(kb);
        const updatedInput = {
          knowledgeBase: kb,
          signature,
          queryFormula: state.queryInput?.queryFormula || "",
        };
        dispatch({ type: "SET_QUERY_INPUT", payload: updatedInput });
        dispatch({ type: "SET_ENTAILMENT_QUERY_RESULT", payload: null });

        storage.saveQueryInput(updatedInput);
      } catch (error) {
        toastError(error);
      }
      dispatch({ type: "SET_INPUT_PENDING", payload: false });
    },
    [state.queryInput, toastError]
  );

  const createFileKnowledgeBase = useCallback(
    async (formulas: FormData) => {
      dispatch({ type: "SET_INPUT_PENDING", payload: true });
      try {
        const kb = await api.createFileKnowledgeBase(formulas);
        const signatures = await api.getSignatureKnowledgeBase(kb);
        const updatedInput = {
          knowledgeBase: kb,
          signature: signatures,
          queryFormula: state.queryInput?.queryFormula || "",
        };
        dispatch({ type: "SET_QUERY_INPUT", payload: updatedInput });
        dispatch({ type: "SET_ENTAILMENT_QUERY_RESULT", payload: null });

        storage.saveQueryInput(updatedInput);
      } catch (error) {
        toastError(error);
      }
      dispatch({ type: "SET_INPUT_PENDING", payload: false });
    },
    [state.queryInput, toastError]
  );

  const getEvaluation = useCallback(
    async (query: IEvaluationQuery) => {
      try {
        dispatch({ type: "SET_RESULT_PENDING", payload: true });
        const data: IEvaluationModel = await api.evaluateApi.evaluate(query);
        dispatch({ type: "SET_EVALUATION", payload: data });
        storage.saveEvaluation(data);
        toast({ description: "Evaluation successful." });
      } catch (error) {
        toastError(error);
      } finally {
        dispatch({ type: "SET_RESULT_PENDING", payload: false });
      }
    },
    [toastError, toast]
  );

  const exportEvaluation = useCallback(async () => {
    if (state.evaluation) {
      dispatch({ type: "SET_RESULT_PENDING", payload: true });
      try {
        await api.evaluateApi.exportEvaluation(state.evaluation);
        toast({ description: "File downloaded successfully." });
      } catch (error) {
        toastError(error);
      }
      dispatch({ type: "SET_RESULT_PENDING", payload: false });
    }
  }, [state.evaluation, toast, toastError]);

  const importEvaluation = useCallback(
    async (formData: FormData) => {
      dispatch({ type: "SET_RESULT_PENDING", payload: true });
      try {
        dispatch({ type: "SET_RESULT_PENDING", payload: true });
        const data: IEvaluationModel = await api.evaluateApi.importEvaluation(
          formData
        );
        dispatch({ type: "SET_EVALUATION", payload: data });
        storage.saveEvaluation(data);
        toast({ description: "File uploaded successfully." });
      } catch (error) {
        toastError(error);
      }
      dispatch({ type: "SET_RESULT_PENDING", payload: false });
    },
    [toast, toastError]
  );

  const deleteEvaluation = useCallback(() => {
    dispatch({ type: "SET_RESULT_PENDING", payload: true });
    storage.deleteEvaluation();
    dispatch({ type: "SET_EVALUATION", payload: null });
    dispatch({ type: "SET_RESULT_PENDING", payload: false });
  }, []);

  const deleteEntailment = useCallback(() => {
    dispatch({ type: "SET_RESULT_PENDING", payload: true });
    storage.deleteEntailmentQueryResult();
    dispatch({ type: "SET_ENTAILMENT_QUERY_RESULT", payload: null });
    dispatch({ type: "SET_RESULT_PENDING", payload: false });
  }, []);

  return (
    <ReasonerContext.Provider
      value={{
        ...state,
        dispatch,
        fetchQueryInput,
        fetchEntailmentQueryResult,
        updateFormula,
        createInputKnowledgeBase,
        generateKnowledgeBase,
        createFileKnowledgeBase,
        exportEvaluation,
        importEvaluation,
        getEvaluation,
        deleteEvaluation,
        deleteEntailment,
      }}
    >
      {children}
    </ReasonerContext.Provider>
  );
};

export const useReasonerContext = () => {
  const ctx = useContext(ReasonerContext);
  if (!ctx) {
    throw new Error(
      "useReasonerContext must be used within a ReasonerProvider"
    );
  }
  return ctx;
};

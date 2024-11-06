import { useToast } from "@/components/ui/use-toast";
import * as api from "@/lib/data";
import { useCallback, useEffect, useState } from "react";
import {
  QueryInput,
  QueryResult,
  getQueryInput,
  saveQueryInput,
  getQueryResult,
  saveQueryResult,
  deleteAllData,
  deleteQueryResult,
} from "@/lib/storage";

function useReasoner() {
  const { toast } = useToast();
  const [inputPending, setInputPending] = useState(false);
  const [resultPending, setResultPending] = useState(false);

  const [queryInput, setQueryInput] = useState(getQueryInput());
  const [queryResult, setQueryResult] = useState(getQueryResult());

  const clearData = () => {
    setQueryInput(null);
    setQueryResult(null);
    deleteAllData();
  };

  const clearQueryResult = () => {
    setQueryResult(null);
    deleteQueryResult();
  };

  const toastError = useCallback(
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    (error: any) => {
      toast({
        description: error.message,
        variant: "destructive",
      });
    },
    [toast]
  );

  const fetchQueryInput = useCallback(async () => {
    setInputPending(true);
    try {
      const queryFormula = await api.fetchQueryFormula();
      const knowledgeBase = await api.fetchKnowledgeBase();
      const data: QueryInput = { queryFormula, knowledgeBase };
      setQueryInput(data);
      saveQueryInput(data);
    } catch (error) {
      toastError(error);
    }
    setInputPending(false);
  }, [toastError]);

  const fetchQueryResult = useCallback(async () => {
    setResultPending(true);
    if (queryInput?.knowledgeBase && queryInput.queryFormula) {
      try {
        const baseRank = await api.fetchBaseRank(queryInput.knowledgeBase);
        const rationalEntailment = await api.fetchRationalEntailment(
          queryInput.queryFormula,
          baseRank
        );
        const lexicalEntailment = await api.fetchLexicalEntailment(
          queryInput.queryFormula,
          baseRank
        );

        const data: QueryResult = {
          baseRank,
          rationalEntailment,
          lexicalEntailment,
        };
        setQueryResult(data);
        saveQueryResult(data);
      } catch (error) {
        toastError(error);
      }
    }
    setResultPending(false);
  }, [queryInput, toastError]);

  const updateFormula = useCallback(
    async (formula: string) => {
      try {
        const queryFormula = await api.createQueryFormula(formula);
        clearQueryResult();
        if (typeof queryFormula === "string") {
          setQueryInput((prev) => {
            const updatedInput = {
              knowledgeBase: prev?.knowledgeBase || [],
              queryFormula: queryFormula,
            };
            saveQueryInput(updatedInput);
            return updatedInput;
          });
        }
      } catch (error) {
        toastError(error);
      }
    },
    [toastError]
  );

  const updateKnowledgeBase = useCallback(
    async (formulas: string[]) => {
      try {
        const kb = await api.createKnowledgeBase(formulas);
        clearQueryResult();
        setQueryInput((prev) => {
          const updatedInput = {
            knowledgeBase: kb,
            queryFormula: prev?.queryFormula || "",
          };
          saveQueryInput(updatedInput);
          return updatedInput;
        });
      } catch (error) {
        toastError(error);
      }
    },
    [toastError]
  );

  const uploadKnowledgeBase = useCallback(
    async (formulas: FormData) => {
      try {
        const kb = await api.uploadKnowledgeBase(formulas);
        clearQueryResult();
        setQueryInput((prev) => {
          const updatedInput = {
            knowledgeBase: kb,
            queryFormula: prev?.queryFormula || "",
          };
          saveQueryInput(updatedInput);
          return updatedInput;
        });
      } catch (error) {
        toastError(error);
      }
    },
    [toastError]
  );

  useEffect(() => {
    if (getQueryInput() == null) {
      fetchQueryInput();
    }
  }, [fetchQueryInput, queryInput]);

  return {
    queryInput,
    queryResult,
    clearData,
    fetchQueryInput,
    inputPending,
    resultPending,
    fetchQueryResult,
    updateFormula,
    updateKnowledgeBase,
    uploadKnowledgeBase,
  };
}

export { useReasoner };

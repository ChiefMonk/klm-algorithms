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
      const knowledgeBase = await api.getDefaultKnowledgeBase();
      const signature = await api.getSignatureKnowledgeBase(knowledgeBase);

      const data: QueryInput = { 
        queryFormula, 
        knowledgeBase,
        signature 
      };
      setQueryInput(data);
      saveQueryInput(data);
    } 
    catch (error) 
    {
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

        const relevantEntailment = await api.fetchRelevantEntailment(
          queryInput.queryFormula,
          baseRank
        );
        
        //console.log('rationalEntailment: ' + rationalEntailment.justification.toString())
        //console.log('lexicalEntailment: ' + lexicalEntailment.justification.toString())
        //console.log('relevantEntailment: ' + relevantEntailment.justification.toString())

        const data: QueryResult = {
          baseRank,
          rationalEntailment,
          lexicalEntailment,
          relevantEntailment,
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
              signature: prev?.signature || [],
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

  const createInputKnowledgeBase = useCallback(
    async (formulas: string[]) => {
      try {
        const kb = await api.createInputKnowledgeBase(formulas);
        const signatures = await api.getSignatureKnowledgeBase(kb);

        clearQueryResult();
        setQueryInput((prev) => {
          const updatedInput = {
            knowledgeBase: kb,
            signature: signatures,
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

  const generateKnowledgeBase = useCallback(async () => {
    setInputPending(true);
    try {     
      const kb = await api.getGeneratedKnowledgeBase();
      const signature = await api.getSignatureKnowledgeBase(kb);

      clearQueryResult();
      setQueryInput((prev) => {
        const updatedInput = {
          knowledgeBase: kb,
          signature: signature,
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

  const createFileKnowledgeBase = useCallback(
    async (formulas: FormData) => {
      try {
        const kb = await api.createFileKnowledgeBase(formulas);
        const signatures = await api.getSignatureKnowledgeBase(kb);

        clearQueryResult();
        setQueryInput((prev) => {
          const updatedInput = {
            knowledgeBase: kb,
            signature: signatures,
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
    createInputKnowledgeBase,
    generateKnowledgeBase,
    createFileKnowledgeBase,
  };
}

export { useReasoner };

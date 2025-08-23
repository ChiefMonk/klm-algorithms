import { KbCard } from "./KbCard";
import { FormulaCard } from "./FormulaCard";
import { QueryInput } from "@/lib/storage";
import { KbGenerationInput, QueryType } from "@/lib/models";
import { EntailmentQueryCard } from "./EntailmentQueryCard";
import { useReasonerContext } from "@/state/reasoner.context";
import { useEffect } from "react";

interface QueryInputProps {
  isLoading: boolean;
  queryInput: QueryInput | null;
  submitKnowledgeBase: (knowledgeBase: string[]) => void;
  uploadKnowledgeBase: (data: FormData) => void;
  updateFormula: (formula: string) => void;
  generateKnowledgeBase: (input: KbGenerationInput) => void;
}
function QueryInputs({
  isLoading,
  queryInput,
  submitKnowledgeBase,
  uploadKnowledgeBase,
  updateFormula,
}: QueryInputProps) {
  const { queryType, fetchQueryInput } = useReasonerContext();

  useEffect(() => {
    if (!queryInput) {
      fetchQueryInput();
    }
  }, [fetchQueryInput, queryInput]);

  return (
    <div className="flex flex-col gap-4">
      <div className="grid grid-cols-1 xl:grid-cols-2 gap-4">
        {queryInput && (
          <KbCard
            isLoading={isLoading}
            knowledgeBase={queryInput.knowledgeBase}
            signature={queryInput.signature}
            submitKnowledgeBase={submitKnowledgeBase}
            uploadKnowledgeBase={uploadKnowledgeBase}
          />
        )}

        {queryInput && (
          <FormulaCard
            isLoading={isLoading}
            queryFormula={queryInput.queryFormula}
            updateFormula={updateFormula}
          />
        )}
      </div>

      <div className="py-4 border-y-4">
        {(queryType === QueryType.Entailment ||
          queryType === QueryType.Justification) && <EntailmentQueryCard />}
      </div>
    </div>
  );
}

export { QueryInputs };

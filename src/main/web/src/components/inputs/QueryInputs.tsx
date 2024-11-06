import { KbCard } from "./KbCard";
import { FormulaCard } from "./FormulaCard";
import { QueryInput } from "@/lib/storage";

interface QueryInputProps {
  isLoading: boolean;
  queryInput: QueryInput | null;
  submitKnowledgeBase: (knowledgeBase: string[]) => void;
  uploadKnowledgeBase: (data: FormData) => void;
  submitQuery: () => void;
  updateFormula: (formula: string) => void;
}
function QueryInputs({
  isLoading,
  queryInput,
  submitKnowledgeBase,
  uploadKnowledgeBase,
  submitQuery,
  updateFormula,
}: QueryInputProps) {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
      {queryInput && (
        <FormulaCard
          isLoading={isLoading}
          handleQuerySubmit={submitQuery}
          queryFormula={queryInput.queryFormula}
          updateFormula={updateFormula}
        />
      )}
      {queryInput && (
        <KbCard
          isLoading={isLoading}
          knowledgeBase={queryInput.knowledgeBase}
          submitKnowledgeBase={submitKnowledgeBase}
          uploadKnowledgeBase={uploadKnowledgeBase}
        />
      )}
    </div>
  );
}

export { QueryInputs };

import { EntailmentModel, QueryType } from "@/lib/models";
import { Kb } from "./common/formulas";
import {  EntailResult,Formula } from "./common/formulas";

interface JustificationProps {
  queryType: QueryType;
  entailment: EntailmentModel | null;
  entailed: boolean;
  queryFormula: string 
}

export function Justification({
  queryType,
  entailment,
  queryFormula,
  entailed,
}: JustificationProps): JSX.Element | null {
  // Render nothing unless we’re in Justification mode and have data
  if (queryType !== QueryType.Justification || !entailment) return null;

  const hasJustifications =
    Array.isArray(entailment.justification) && entailment.justification.length > 0;

  return (
    <div>
      <h1 className="text-lg font-bold mb-2">
        B. Entailment Explanation
      </h1>

      <p className="mb-2">The deciding knowledge base <Formula formula="\mathcal{D}" /> is passed to the <i>universal justication knowledge algorithm</i> justification-based explanation algorithm to generate the justification sets <Formula formula="\mathcal{J}_{i}" />:</p>
      <Kb
        formulas={entailment.entailmentKnowledgeBase}
        name={`\\mathcal{D}`}
        set
      />

      <div className="my-6" />

      {hasJustifications && (
        <>
          <p className="mb-2">
            The Justification sets for{" "}
            <EntailResult formula={queryFormula} entailed={entailed} /> from <Formula formula={"\\mathcal{D} \\subseteq \\mathcal{K}"} /> :
          </p>

          {entailment.justification.map((just, index) => (
            <Kb
              key={index}
              formulas={just}
              name={`\\mathcal{J}_{${index + 1}}`}
              set
            />
          ))}
        </>
      )}
    </div>
  );
}

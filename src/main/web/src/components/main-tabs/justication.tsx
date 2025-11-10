import { EntailmentModel, QueryType } from "@/lib/models";
import { Kb } from "./common/formulas";

interface JustificationProps {
  queryType: QueryType;
  entailment: EntailmentModel | null;
}

export function Justification({
  queryType,
  entailment,
}: JustificationProps): JSX.Element {
  return (
    <>
      {queryType === QueryType.Justification && entailment && (
        <div>
          <h1 className="text-lg font-bold mb-2">Justification-based Explanation Algorithm</h1>
          The Deciding Knowledge Base: <Kb
            formulas={entailment.entailmentKnowledgeBase}
            name="\mathcal{D}"
            set
          />
          <div className="my-6"></div>

          The Justification Sets:

          {entailment.justification.map((just, index) => (
                  <Kb
                    key={index}
                    formulas={just}
                    name={`\\mathcal{J}_{${index + 1}}`}
                    set
                  />
                ))}
         
        </div>
      )}
    </>
  );
}

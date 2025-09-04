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
          <h1 className="text-lg font-bold mb-2">Justification Algorithm</h1>
          The deciding knowledge base <Kb
            formulas={entailment.entailmentKnowledgeBase}
            name="\mathcal{D}"
            set
          />
          <div className="my-6"></div>

          <Kb
            formulas={entailment.justification.flat()}
            name="\mathcal{J}_{1}"
            set
          />
        </div>
      )}
    </>
  );
}

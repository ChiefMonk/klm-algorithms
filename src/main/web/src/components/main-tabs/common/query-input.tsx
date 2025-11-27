import { EntailmentType } from "@/lib/models";
import { Formula, Kb, QueryFormula } from "./formulas";

const inferenceOperatorMap: Record<EntailmentType, string> = {
  [EntailmentType.Unknown]: "Unknown",
  [EntailmentType.RationalClosure]: "RationalClosure",
  [EntailmentType.LexicographicClosure]: "LexicographicClosure",
  [EntailmentType.BasicRelevantClosure]: "BasicRelevantClosure",
  [EntailmentType.MinimalRelevantClosure]: "MinimalRelevantClosure",
};

interface QueryInputContainerProps {
  type: EntailmentType;
  queryFormula: string;
  knowledgeBase: string[];
  queryFormulaHidden?: boolean;
}

function QueryInputContainer({
  type,
  queryFormula,
  knowledgeBase,
  queryFormulaHidden = false,
}: QueryInputContainerProps) {

  const inferenceOp = inferenceOperatorMap[type];

  return (
    <>
      <h1 className="text-lg font-bold mb-2">A. Defeasible Entailment Determination</h1>          
      <p className="mb-3">
        The results of defeasible <i><strong>{inferenceOp}</strong></i> entailment algorithms, which determine whether a <i>defeasible query formula</i>,<strong><Formula formula="\alpha" /></strong>, can be inferred from a <i>defeasible knowledge base</i>, <strong><Formula formula="\mathcal{K}" /></strong>.
      </p>
      <div className="space-y-4">
        <Kb formulas={knowledgeBase} set />
        {!queryFormulaHidden && <QueryFormula formula={queryFormula} />}
      </div>
    </>
  );
}


function QueryInputContainerAgain({
  queryFormula,
  knowledgeBase,
  queryFormulaHidden = false,
}: QueryInputContainerProps) {

  return (
    <div className="space-y-4">
      <Kb formulas={knowledgeBase} set />
      {!queryFormulaHidden && <QueryFormula formula={queryFormula} />}
    </div>

  );
}

export { QueryInputContainer, QueryInputContainerAgain };

import { Kb, QueryFormula } from "./formulas";

interface QueryInputContainerProps {
  queryFormula: string;
  knowledgeBase: string[];
  queryFormulaHidden?: boolean;
}

function QueryInputContainer({
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

export { QueryInputContainer };

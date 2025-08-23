import {
  buildExceptionalityCheckFormula,
  buildExceptionalityCheckResult,
} from "@/lib/build-formula/base-rank";
import { Formula } from "../common/formulas";

interface PartitioningCheckProps {
  check: {
    isExceptional: boolean;
    antecedentNegation: string;
    formula: string;
  };
  index: number;
}

function PartitioningCheck({ check, index }: PartitioningCheckProps) {
  const { formula, antecedentNegation, isExceptional } = check;
  const exceptionalityCheckFormula = buildExceptionalityCheckFormula(
    index,
    antecedentNegation,
    isExceptional
  );
  const result = buildExceptionalityCheckResult(formula, isExceptional);

  return (
    <>
      <Formula formula={exceptionalityCheckFormula} />
      <span className="ml-8">
        <Formula formula={result.formula} /> {result.text}.
      </span>
    </>
  );
}

export { PartitioningCheck };

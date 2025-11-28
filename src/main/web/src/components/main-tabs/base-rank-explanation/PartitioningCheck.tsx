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
       <div style={{ display: 'inline-block', width: '18ch' }}>
  <Formula formula={result.formula.trim()} />
</div>: Since {"  "} <Formula formula={exceptionalityCheckFormula.trim()} />, therefore <Formula formula={result.formula.trim()} /> {result.text.trim()}.    
      
    </>
  );
}

export { PartitioningCheck };

import { buildExceptionalityElementFormula } from "@/lib/build-formula/base-rank";
import { Formula } from "../common/formulas";
import { PartitioningCheck } from "./PartitioningCheck";
import { IBaseRankExplanation } from "@/lib/models";

interface PartitioningProcedureProps {
  sequence: IBaseRankExplanation["sequence"];
}

function PartitioningProcedure({ sequence }: PartitioningProcedureProps) {
  return (
    <>
      <p>The partitioning procedure is described as follows:</p>
      <ul className="ml-8 list-disc space-y-6">
        {sequence.map((element, i) => (
          <li key={i}>
            <Formula formula={buildExceptionalityElementFormula(i, element)} />
            <ul className="ml-8 list-disc">
              {!element.isLastElement &&
                element.checks.map((check, j) => (
                  <li key={j}>
                    <PartitioningCheck check={check} index={i} />
                  </li>
                ))}
              {element.isLastElement && (
                <li>
                  <Formula formula="\therefore\;\;" />
                  partitioning procedure ends with{" "}
                  <Formula formula={`*_{${i}}`} />.
                </li>
              )}
            </ul>
          </li>
        ))}
      </ul>
    </>
  );
}

export { PartitioningProcedure };

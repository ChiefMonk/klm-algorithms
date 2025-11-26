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
      <p>For the given <Formula formula="\mathcal{K}" /> above, we describe the partitioning procedure as follows:</p>
      <ol className="ml-8 list-disc space-y-6">
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
<>

                <li>
                  Since <Formula formula={`*_{${i}}`} /> = <Formula formula={`*_{${i - 1}}`} />, partitioning procedure terminates with <Formula formula={`*_{${i}}`} />.
                </li>
                <li>
                  If <Formula formula="\mathcal{K}" /> contains classical statements or contradictory statements, then <Formula formula={`*_{${i}}`} /> = <Formula formula={`*_{\\infty}`} />.
                </li>
                </>
              )}
            </ul>
          </li>
        ))}
      </ol>
    </>
  );
}

export { PartitioningProcedure };

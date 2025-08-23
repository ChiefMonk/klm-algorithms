import { IRationalClosureExplanation } from "@/lib/models";
import { Formula } from "../common/formulas";
import {
  buildRankCheck,
  buildRankUnion,
} from "@/lib/build-formula/rational-closure";
import { RankingTable } from "../tables/ranking-table";

function RationalClosureRankChek({
  explanation,
}: {
  explanation: IRationalClosureExplanation;
}) {
  return (
    <div className="space-y-6">
      {explanation.checks.map((check, index) => (
        <div key={index}>
          <RankingTable ranking={check.ranks} />
          <div className="ml-8 py-4 space-y-2">
            <p>
              Check if the above ranks entail{" "}
              <Formula formula={check.antecedentNegation} />
            </p>
            <p>
              <Formula formula={buildRankUnion(check.ranks)} />
            </p>
            <p className="space-x-4">
              <span>
                <Formula formula={buildRankCheck(check)} />
              </span>
              {!check.isConsistent && (
                <span>
                  Remove rank{" "}
                  <Formula
                    formula={`\\mathcal{R}_{${check.removedRank.rankNumber}}`}
                  />
                </span>
              )}
              {check.isConsistent && (
                <span>
                  Stop removing ranks and check if the remaining ranks entail{" "}
                  <Formula formula={explanation.queryFormula} />
                </span>
              )}
            </p>
          </div>
        </div>
      ))}
    </div>
  );
}

export { RationalClosureRankChek };

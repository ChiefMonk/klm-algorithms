import { IRationalClosureExplanation } from "@/lib/models";
import { Formula } from "../common/formulas";
import {
  buildRankCheck,
  buildRankUnion,
} from "@/lib/build-formula/rational-closure";

function RationalClosureRankChek({
  explanation,
}: {
  explanation: IRationalClosureExplanation;
}) {
  return (
    <div>
      {explanation.checks.map((check, index) => (
        <div key={index}>       
          <div className="ml-8 py-4 space-y-2">
            <p>
              We check and determine if the above union of ranks entails the negation of the query antecedent,{" "}
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
                <br />Becase the knowledge base is still consistency with respect to <Formula formula={check.antecedentNegation} />, we remove the most typical rank,{" "}
                  <Formula
                    formula={`\\mathcal{R}_{${check.removedRank.rankNumber}}`}
                  />
                </span>
              )}
              {check.isConsistent && (
                <span>
                   <br /> We now stop discarding ranks and check if the remaining ranks entail the query,{" "}
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

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { ResultSkeleton } from "@/components/main-tabs/ResultSkeleton";

import { IRationalClosureExplanation } from "@/lib/models";
import { EntailResult, Formula, Kb, QueryFormula } from "../common/formulas";
import { NoResults } from "../NoResults";
import { RationalClosureRankChek } from "./RationalClosureRankChek";
import { buildFinalEntailmentCheck } from "@/lib/build-formula/rational-closure";

interface RationalClosureExplanationProps {
  isLoading: boolean;
  rationalExplanation: IRationalClosureExplanation | null;
}

function RationalClosureExplanation({
  isLoading,
  rationalExplanation,
}: RationalClosureExplanationProps): JSX.Element {
  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-lg font-bold">
          The Rational Closure Entailment Algorithm
        </CardTitle>
        <CardDescription>
          Using rational closure algorithm to check for entailment.
        </CardDescription>
      </CardHeader>
      <CardContent>
        {!isLoading && rationalExplanation && (
          <div>
            <Kb formulas={rationalExplanation.knowledgeBase} set />
            <div className="mb-6">
              <QueryFormula formula={rationalExplanation.queryFormula} />
            </div>
            <div className="mb-6 space-y-4">
              <p>
                Rational closure starts with the initial rankings constructed by
                the <i>BaseRank</i> algorithm.
              </p>

              <RationalClosureRankChek explanation={rationalExplanation} />
            </div>
            <div>
              It follows that{" "}
              <Formula
                formula={buildFinalEntailmentCheck(rationalExplanation)}
              />
              {"  "}. Therefore Therefore{" "}
              <EntailResult
                formula={rationalExplanation.queryFormula}
                entailed={rationalExplanation.isEntailed}
              />
              .
            </div>
          </div>
        )}
        {isLoading && <ResultSkeleton />}
        {!isLoading && !rationalExplanation && <NoResults />}
      </CardContent>
    </Card>
  );
}

export { RationalClosureExplanation };

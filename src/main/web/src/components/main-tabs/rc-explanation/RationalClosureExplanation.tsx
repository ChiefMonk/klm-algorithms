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
import { useReasonerContext } from "@/state/reasoner.context";
import { Justification } from "../justication";

interface RationalClosureExplanationProps {
  isLoading: boolean;
  rationalExplanation: IRationalClosureExplanation | null;
}

function RationalClosureExplanation({
  isLoading,
  rationalExplanation,
}: RationalClosureExplanationProps): JSX.Element {
  const { queryType, entailmentQueryResult } = useReasonerContext();

  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-2xl font-bold">Rational Closure</CardTitle>
        <CardDescription className="text-base text-xl md:text-xl">         
          Rational Closure is a KLM inference mechanism for non-monotonic reasoning that ranks defeasible statements by their level of exceptionality. During entailment checking, lower (more typical) ranks are iteratively discarded until consistent higher-ranked worlds remain. A defeasible formula  
<Formula formula=" \alpha \mid\sim \beta" /> holds if <Formula formula="\beta" /> is true in all minimal <Formula formula="\alpha" />-worlds, yielding a unique, consistent, and computationally efficient defeasible entailment relation. 
         
        </CardDescription>
      </CardHeader>
      <CardContent>
        {!isLoading && rationalExplanation && (
          <div className="space-y-6">
            <div>
              <h1 className="text-lg font-bold mb-2">Entailment Determination</h1>
              <Kb formulas={rationalExplanation.knowledgeBase} set />
              <div className="mb-6">
                <QueryFormula formula={rationalExplanation.queryFormula} />
              </div>
              <div className="mb-6 space-y-4">
                <p>
                  Rational closure starts with the initial rankings constructed
                  by the <i>BaseRank</i> algorithm.
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
            <Justification
              queryType={queryType}
              entailment={entailmentQueryResult?.rationalEntailment || null}
            />
          </div>
        )}
        {isLoading && <ResultSkeleton />}
        {!isLoading && !rationalExplanation && <NoResults />}
      </CardContent>
    </Card>
  );
}

export { RationalClosureExplanation };

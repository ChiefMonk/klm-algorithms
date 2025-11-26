import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { ResultSkeleton } from "@/components/main-tabs/ResultSkeleton";

import { IRationalClosureExplanation, RationalEntailmentModel } from "@/lib/models";
import { EntailResult, Formula } from "../common/formulas";
import { NoResults } from "../NoResults";
import { RationalClosureRankChek } from "./RationalClosureRankChek";
import { buildFinalEntailmentCheck } from "@/lib/build-formula/rational-closure";
import { useReasonerContext } from "@/state/reasoner.context";
import { Justification } from "../justication";
import { QueryInputContainer } from "../common/query-input";
import { BaseRankingExplanation, EntailmentExplanation } from "../common/explanations";

interface RationalClosureExplanationProps {
  isLoading: boolean;
  rationalExplanation: IRationalClosureExplanation | null;
  rationalEntailment: RationalEntailmentModel | null;
}

function RationalClosureExplanation({
  isLoading,
  rationalExplanation,
  rationalEntailment,
}: RationalClosureExplanationProps): JSX.Element {
  const { queryType, entailmentQueryResult } = useReasonerContext();

  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-2xl font-bold">Rational Closure</CardTitle>
        <CardDescription className="text-base text-medium">
          <b>Rational Closure</b> is a KLM inference mechanism for non-monotonic reasoning introduced by <b>Lehmann</b> and <b>Magidor</b>, and the entailment algorithms implemented here
          build on the version proposed by <b>Casini et al</b>.
          The algorithm operates in two sub-phases, <b><i>BaseRank</i></b> and <b><i>RationalClosure</i></b> algorithms. The algorithm functions by assigning a ranking of typicality to the statements in the knowledge base.
          When an inconsistency arises during entailment computation, the algorithm removes the most typical information from the knowledge base to restore consistency.
          The <i>BaseRank</i> phase determines these rankings, where statements with lower ranks represent more typical information.

        </CardDescription>
      </CardHeader>
      <CardContent>
        {!isLoading && rationalExplanation && rationalEntailment && (
          <div>
            <h1 className="text-lg font-bold mb-2">A. Entailment Determination</h1>
            <QueryInputContainer
              knowledgeBase={rationalExplanation.knowledgeBase}
              queryFormula={rationalExplanation.queryFormula}
            />
            <div className="my-6">
              <p className="font-medium">
                <strong> Base Rank of statements in <Formula formula="\mathcal{K}" />:</strong>
              </p>
              <BaseRankingExplanation
                entailment={rationalEntailment}
                className="mb-6 space-y-4"
              />

              <p className="font-medium bold">
                <strong>Lexicographic powerset of statements in <Formula formula="\mathcal{K}" />:</strong>
              </p>

              <div className="mb-6 space-y-4">
                <RationalClosureRankChek explanation={rationalExplanation} />
              </div>

              <p className="font-medium bold"> <strong>Entailment check: Does <Formula formula="\mathcal{K}" /> defeasibly entail <Formula formula={rationalEntailment.queryFormula} />?:</strong></p>
              <EntailmentExplanation
                entailment={rationalEntailment}
                className="mb-6 space-y-4"
              />
              <div>
                It follows that{" "}
                <Formula
                  formula={buildFinalEntailmentCheck(rationalExplanation)}
                />
                {"  "}. <br />Therefore, we conclude that the knowledge base{" "}
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
              queryFormula={rationalExplanation.queryFormula}
              entailed={rationalExplanation.isEntailed}
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

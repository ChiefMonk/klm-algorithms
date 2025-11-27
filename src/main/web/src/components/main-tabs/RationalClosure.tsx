import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { ResultSkeleton } from "@/components/main-tabs/ResultSkeleton";
import { NoResults } from "./NoResults";
import { EntailmentType, IRationalClosureExplanation, RationalEntailmentModel } from "@/lib/models";
import { QueryInputContainer } from "./common/query-input";
import { BaseRankingExplanation, EntailmentExplanation, DiscardedRankingExplanation, RemainingRankingExplanation, RationalClosureDetermination } from "./common/explanations";
import { Formula } from "./common/formulas";
import { Justification } from "./justication";
import { useReasonerContext } from "@/state/reasoner.context";

interface RationalClosureProps {
  isLoading: boolean;
  rationalEntailment: RationalEntailmentModel | null;
  rationalExplanation: IRationalClosureExplanation | null;
}

function RationalClosure({
  isLoading,
  rationalEntailment,
  rationalExplanation,
}: RationalClosureProps): JSX.Element {
  const { queryType } = useReasonerContext();
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
        {!isLoading && rationalEntailment && rationalExplanation && (
          <div>
          
            <QueryInputContainer
              type={EntailmentType.RationalClosure}
              knowledgeBase={rationalEntailment.knowledgeBase}
              queryFormula={rationalEntailment.queryFormula}
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
                <strong>Determination of rank statements consistent with <Formula formula={rationalEntailment.antecedent} />:</strong>
              </p>
              <div className="mb-6 space-y-4">
                <RationalClosureDetermination explanation={rationalExplanation} />
              </div>

              <p className="font-medium bold"> <strong>Discarded ranks from <Formula formula="\mathcal{K}" />:</strong></p>
              <DiscardedRankingExplanation
                entailment={rationalEntailment}
                className="mb-6 space-y-4"
              />

              <p className="font-medium bold"> <strong>Remaining ranks from <Formula formula="\mathcal{K}" />:</strong></p>
              <RemainingRankingExplanation
                entailment={rationalEntailment}
                className="mb-6 space-y-4"
              />

              <p className="font-medium bold"> <strong>Entailment check: Does <Formula formula="\mathcal{K}" /> defeasibly entail <Formula formula={rationalEntailment.queryFormula} />?:</strong></p>
              <EntailmentExplanation
                entailment={rationalEntailment}
                className="mb-6 space-y-4"
              />
            </div>

            <Justification
              queryType={queryType}
              entailment={rationalEntailment}
              queryFormula={rationalEntailment.queryFormula}
              entailed={rationalEntailment.entailed}
            />
          </div>
        )}
        {isLoading && <ResultSkeleton />}
        {!isLoading && !rationalEntailment && <NoResults />}
      </CardContent>
    </Card>
  );
}

export { RationalClosure };

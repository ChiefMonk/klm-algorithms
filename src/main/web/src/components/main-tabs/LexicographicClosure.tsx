import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { ResultSkeleton } from "@/components/main-tabs/ResultSkeleton";
import { NoResults } from "./NoResults";
import { QueryInputContainer } from "./common/query-input";
import { LexicalEntailmentModel } from "@/lib/models";
import { BaseRankingExplanation, EntailmentExplanation, DiscardedRankingExplanation, RemainingRankingExplanation, LexicographicPowersetExplanation } from "./common/explanations";
import { Justification } from "./justication";
import { useReasonerContext } from "@/state/reasoner.context";
import { Formula } from "./common/formulas";

interface LexicographicClosureProps {
  isLoading: boolean;
  lexicalEntailment: LexicalEntailmentModel | null;
}

function LexicographicClosure({
  isLoading,
  lexicalEntailment,
}: LexicographicClosureProps): JSX.Element {
  const { queryType } = useReasonerContext();
  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-2xl font-bold">
          Lexicographic Closure
        </CardTitle>
        <CardDescription className="text-base text-medium">
          <b>Lexicographic Closure</b> is a KLM inference mechanism for non-monotonic reasoning introduced by <b>Lehmann</b>, and the entailment algorithms implemented here build on the version proposed by <b>Casini et al</b>. The algorithm operates in two sub-phases, <b><i>BaseRank</i></b> and <b><i>LexicographicClosure</i></b> algorithms. The algorithm functions by assigning a ranking of typicality to the statements in the knowledge base.
          Lexicographic Closure can be seen as a refinement of Rational Closure, where we remove single statements instead of entire ranks when inconsistencies arise during the reasoning process, starting with the most typical information. The <i>BaseRank</i> phase determines these rankings, where statements with lower ranks represent more typical information.
        </CardDescription>
      </CardHeader>
      <CardContent>
        {!isLoading && lexicalEntailment && (
          <div>
            <h1 className="text-lg font-bold mb-2">A. Entailment Determination</h1>
            <QueryInputContainer
              knowledgeBase={lexicalEntailment.knowledgeBase}
              queryFormula={lexicalEntailment.queryFormula}
            />
            <div className="my-6">

              <p className="font-medium">
                <strong> Base Rank of statements in <Formula formula="\mathcal{K}" />:</strong>
              </p>
              <BaseRankingExplanation
                entailment={lexicalEntailment}
                className="mb-6 space-y-4"
              />

              <p className="font-medium bold">
                <strong>Lexicographic powerset of statements in <Formula formula="\mathcal{K}" />:</strong>
              </p>
              <LexicographicPowersetExplanation
                entailment={lexicalEntailment}
                className="mb-6 space-y-4"
              />

              <p className="font-medium bold"> <strong>Discarded statements from <Formula formula="\mathcal{K}" /> shown per rank:</strong></p>
              <DiscardedRankingExplanation
                entailment={lexicalEntailment}
                className="mb-6 space-y-4"
              />

              <p className="font-medium bold"> <strong>Remaining statements from <Formula formula="\mathcal{K}" /> shown per rank:</strong></p>
              <RemainingRankingExplanation
                entailment={lexicalEntailment}
                className="mb-6 space-y-4"
              />

              <p className="font-medium bold"> <strong>Entailment check: Does <Formula formula="\mathcal{K}" /> defeasibly entail <Formula formula={lexicalEntailment.queryFormula} />?:</strong></p>
              <EntailmentExplanation
                entailment={lexicalEntailment}
                className="mb-6 space-y-4"
              />
            </div>

            <Justification
              queryType={queryType}
              entailment={lexicalEntailment}
              queryFormula={lexicalEntailment.queryFormula}
              entailed={lexicalEntailment.entailed}
            />
          </div>
        )}
        {isLoading && <ResultSkeleton />}
        {!isLoading && !lexicalEntailment && <NoResults />}
      </CardContent>
    </Card>
  );
}

export { LexicographicClosure };
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { ResultSkeleton } from "@/components/main-tabs/ResultSkeleton";
import { NoResults } from "./NoResults";
import { RankingTableWithout } from "./tables/ranking-table";
import { BasicRelevantEntailmentModel } from "@/lib/models";
import { EntailmentExplanation,DiscardedRankingExplanation, RemainingRankingExplanation, LexicographicPowersetExplanation } from "./common/explanations";
import { QueryInputContainer } from "./common/query-input";
import { Justification } from "./justication";
import { useReasonerContext } from "@/state/reasoner.context";
import { Formula } from "./common/formulas";

interface BasicRelevantClosureProps {
  isLoading: boolean;
  basicRelevantEntailment: BasicRelevantEntailmentModel | null;
}

function BasicRelevantClosure({
  isLoading,
  basicRelevantEntailment,
}: BasicRelevantClosureProps): JSX.Element {
  const { queryType } = useReasonerContext();
  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-2xl font-bold">
          Basic Relevant Closure
        </CardTitle>
        <CardDescription className="text-base text-medium">              
         
  <b>Basic Relevant Closure</b> is a KLM inference mechanism for non-monotonic reasoning introduced by <b>Casini et al.</b>, and the entailment algorithms implemented here build on the version proposed by <b>Casini et al</b>. The algorithm operates in two sub-phases, <b><i>BaseRank</i></b> and <b><i>BasicRelevantClosure</i></b> algorithms. The algorithm functions by assigning a ranking of typicality to the statements in the knowledge base. 
        Basic Relevant Closure can be seen as a refinement of Rational Closure, where we only retract the statements in a less specific rank that actually disagree with more specific statements in higher ranks with respect to the antecedent of the query, starting with the most typical information. The <i>BaseRank</i> phase determines these rankings, where statements with lower ranks represent more typical information.
        </CardDescription>
      </CardHeader>
      <CardContent>
        {!isLoading && basicRelevantEntailment && (
          <div>
            <h1 className="text-lg font-bold mb-2">A. Entailment Determination</h1>
            <QueryInputContainer
              knowledgeBase={basicRelevantEntailment.knowledgeBase}
              queryFormula={basicRelevantEntailment.queryFormula}
            />
            <div className="my-6">

              <p className="font-medium">
              <strong> Base Rank of statements in <Formula formula="\mathcal{K}" />:</strong>
              </p>
              <p className="mb-3">
              <b>Basic Relevant Closure</b> starts with the base rankings of statements in <Formula formula="\mathcal{K}" /> constructed by the <i>BaseRank</i> algorithm.
              </p>

              <RankingTableWithout
                ranking={basicRelevantEntailment.baseRanking}
              />

              <p className="font-medium bold">
               <strong>Lexicographic powerset of statements in <Formula formula="\mathcal{K}" />:</strong> 
              </p>
              <LexicographicPowersetExplanation
                entailment={basicRelevantEntailment}
                className="mb-6 space-y-4"
              />
                         
              <p className="font-medium bold"> <strong>Discarded statements from <Formula formula="\mathcal{K}" /> shown per rank:</strong></p>
              <DiscardedRankingExplanation
                entailment={basicRelevantEntailment}
                className="mb-6 space-y-4"
              />

              <p className="font-medium bold"> <strong>Remaining statements from <Formula formula="\mathcal{K}" /> shown per rank:</strong></p>
              <RemainingRankingExplanation
                entailment={basicRelevantEntailment}
                className="mb-6 space-y-4"
              />            

              <p className="font-medium bold"> <strong>Entailment check: Does <Formula formula="\mathcal{K}" /> defeasibly entail <Formula formula={basicRelevantEntailment.queryFormula} />?:</strong></p>
              <EntailmentExplanation
                entailment={basicRelevantEntailment}
                className="mb-6 space-y-4"
              />
            </div>

            <Justification
              queryType={queryType}
              entailment={basicRelevantEntailment}
              queryFormula={basicRelevantEntailment.queryFormula}
              entailed={basicRelevantEntailment.entailed}
            />
          </div>
        )}
        {isLoading && <ResultSkeleton />}
        {!isLoading && !basicRelevantEntailment && <NoResults />}
      </CardContent>
    </Card>
  );
}

export { BasicRelevantClosure };

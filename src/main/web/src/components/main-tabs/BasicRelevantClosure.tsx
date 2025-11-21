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
import { Explanation } from "./common/explanations";
import { Kb, QueryFormula, Formula } from "./common/formulas";
import { Justification } from "./justication";
import { useReasonerContext } from "@/state/reasoner.context";

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
            <h1 className="text-lg font-bold mb-2">Entailment Determination</h1>

            <Kb formulas={basicRelevantEntailment.knowledgeBase} set />
            <div className="mb-6">
              <QueryFormula formula={basicRelevantEntailment.queryFormula} />
            </div>
            <div className="mb-6">
              <p className="mb-3">
              Basic Relevant Closure starts with the base rankings of statements in <Formula formula="\mathcal{K}" /> constructed by the <i>BaseRank</i> algorithm.               
              </p>
              <p className="font-medium">
              Base Rank of statements in <Formula formula="\mathcal{K}" />:             
              </p>
              <RankingTableWithout
                ranking={basicRelevantEntailment.baseRanking}
              />

              <p className="font-medium">Relevant Statements to the negation of Query Antecedent, <Formula formula={basicRelevantEntailment.negation} /></p>
              <RankingTableWithout
                ranking={basicRelevantEntailment.relevantRanking}               
              />


              <p className="font-medium">Discarded Statements by Basic Relevant Closure Algorithm</p>
              <RankingTableWithout
                ranking={basicRelevantEntailment.removedRanking}
              />

              <p className="font-medium">Remaining Statements by Basic Relevant Closure Algorithm</p>
              <RankingTableWithout
                ranking={basicRelevantEntailment.remainingRanking}
              />
            </div>
            <Explanation
              entailment={basicRelevantEntailment}
              className="mb-6 space-y-4"
            />
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

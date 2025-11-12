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
import { QueryInputContainer } from "./common/query-input";
import { LexicalEntailmentModel } from "@/lib/models";
import { Explanation } from "./common/explanations";
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
        <CardDescription className="text-base text-lg">           
        <b>Lexicographic Closure</b> is a KLM inference mechanism for non-monotonic reasoning introduced by <b>Lehmann</b>, and the entailment algorithms implemented here build on the version proposed by <b>Casini et al</b>. The algorithm operates in two sub-phases, <b><i>BaseRank</i></b> and <b><i>LexicographicClosure</i></b> algorithms. The algorithm functions by assigning a ranking of typicality to the knowledge base. 
        Lexicographic Closure can be seen as a refinement of Rational Closure, where we remove single statements instead of entire ranks when inconsistencies arise during the reasoning process, starting with the most typical information. The <i>BaseRank</i> phase determines these rankings, where statements with lower ranks represent more typical information.
        </CardDescription>
      </CardHeader>
      <CardContent>
        {!isLoading && lexicalEntailment && (
          <div>
            <h1 className="text-lg font-bold mb-2">Entailment Determination</h1>
            <QueryInputContainer
              knowledgeBase={lexicalEntailment.knowledgeBase}
              queryFormula={lexicalEntailment.queryFormula}
            />
            <div className="my-6">
              <p className="mb-3">
                Lexicographic Closure starts with the base rankings of statements in <Formula formula="\mathcal{K}" /> constructed by the <i>BaseRank</i> algorithm.
              </p>

              <p className="font-medium">
              Base Rank of statements in <Formula formula="\mathcal{K}" />:             
              </p>
              <RankingTableWithout
                ranking={lexicalEntailment.baseRanking}
              />

              <p className="font-medium">Discarded Statements by Lexigraphic Closure Algorithm</p>
              <RankingTableWithout
                ranking={lexicalEntailment.removedRanking}
              />

              <p className="font-medium">Remaining Statements by Lexigraphic Closure Algorithm</p>
              <RankingTableWithout
                ranking={lexicalEntailment.remainingRanking}
              />
             
            </div>
            <Explanation
              entailment={lexicalEntailment}
              className="mb-6 space-y-4"
            />

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

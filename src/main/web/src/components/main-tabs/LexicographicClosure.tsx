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
        <CardDescription className="text-base">
          {/* Lexicographic closure description */}
          Lorem ipsum dolor sit amet consectetur adipisicing elit. Enim tenetur
          omnis temporibus, corrupti dolorem debitis accusantium nihil quod
          soluta eligendi, impedit odit sunt! Qui, distinctio necessitatibus
          atque accusantium recusandae non.
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
                Lexicographic closure starts with the initial rankings constructed by the <i>BaseRank</i> algorithm.
              </p>

              <p className="font-medium">
              Base Rank of statements in <Formula formula="\mathcal{K}" /> produced by the <i>BaseRank</i> algorithm              
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

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { ResultSkeleton } from "@/components/main-tabs/ResultSkeleton";
import { NoResults } from "./NoResults";
import { RankingTable } from "./tables/ranking-table";
import { MinimalRelevantEntailmentModel } from "@/lib/models";
import { Explanation } from "./common/explanations";
import { Kb, QueryFormula } from "./common/formulas";
import { Justification } from "./justication";
import { useReasonerContext } from "@/state/reasoner.context";

interface MinimalRelevantClosureProps {
  isLoading: boolean;
  minimalRelevantEntailment: MinimalRelevantEntailmentModel | null;
}

function MinimalRelevantClosure({
  isLoading,
  minimalRelevantEntailment,
}: MinimalRelevantClosureProps): JSX.Element {
  const { queryType } = useReasonerContext();
  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-2xl font-bold">
          Mininal Relevant Closure
        </CardTitle>
        <CardDescription className="text-base">
          {/* Mininal Relevant closure description */}
          Lorem ipsum dolor sit amet consectetur adipisicing elit. Enim tenetur
          omnis temporibus, corrupti dolorem debitis accusantium nihil quod
          soluta eligendi, impedit odit sunt! Qui, distinctio necessitatibus
          atque accusantium recusandae non.
        </CardDescription>
      </CardHeader>
      <CardContent>
        {!isLoading && minimalRelevantEntailment && (
          <div>
            <h1 className="text-lg font-bold mb-2">Entailment Algorithm</h1>
            <Kb formulas={minimalRelevantEntailment.knowledgeBase} set />
            <div className="mb-6">
              <QueryFormula formula={minimalRelevantEntailment.queryFormula} />
            </div>
            <div className="mb-6">
              <p className="mb-3">
                Relevant closure starts with the initial rankings constructed by
                the <i>BaseRank</i> algorithm.
              </p>
              <p className="font-medium">
                Output of the <i>BaseRank</i> algorithm
              </p>
              <RankingTable
                ranking={minimalRelevantEntailment.baseRanking}
                caption="Ranks constructed by the Base Rank algorithm"
              />
            </div>
            <Explanation
              entailment={minimalRelevantEntailment}
              className="mb-6 space-y-4"
            />
            <Justification
              queryType={queryType}
              entailment={minimalRelevantEntailment}
            />
          </div>
        )}
        {isLoading && <ResultSkeleton />}
        {!isLoading && !minimalRelevantEntailment && <NoResults />}
      </CardContent>
    </Card>
  );
}

export { MinimalRelevantClosure };

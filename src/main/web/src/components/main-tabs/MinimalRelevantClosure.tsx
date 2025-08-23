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

interface MinimalRelevantClosureProps {
  isLoading: boolean;
  minimalRelevantEntailment: MinimalRelevantEntailmentModel | null;
}

function MinimalRelevantClosure({
  isLoading,
  minimalRelevantEntailment,
}: MinimalRelevantClosureProps): JSX.Element {
  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-lg font-bold">The Minimal Relevant Closure Entailment Algorithm</CardTitle>
        <CardDescription>
          Using minimal relevant closure algorithm to check for entailment.
        </CardDescription>
      </CardHeader>
      <CardContent>
        {!isLoading && minimalRelevantEntailment && (
          <div>
            <Kb formulas={minimalRelevantEntailment.knowledgeBase} set />
            <div className="mb-6">
              <QueryFormula formula={minimalRelevantEntailment.queryFormula} />
            </div>
            <div className="mb-6">
              <p className="mb-3">               
                Relevant closure starts with the initial rankings constructed by the <i>BaseRank</i> algorithm.
              </p>
              <p className="font-medium">Output of the <i>BaseRank</i> algorithm</p>
              <RankingTable
                ranking={minimalRelevantEntailment.baseRanking}
                caption="Ranks constructed by the Base Rank algorithm"
              />
            </div>
            <Explanation
              entailment={minimalRelevantEntailment}
              className="mb-6 space-y-4"
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

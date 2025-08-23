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
import { BasicRelevantEntailmentModel } from "@/lib/models";
import { Explanation } from "./common/explanations";
import { Kb, QueryFormula } from "./common/formulas";

interface BasicRelevantClosureProps {
  isLoading: boolean;
  basicRelevantEntailment: BasicRelevantEntailmentModel | null;
}

function BasicRelevantClosure({
  isLoading,
  basicRelevantEntailment,
}: BasicRelevantClosureProps): JSX.Element {
  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-lg font-bold">The Basic Relevant Closure Entailment Algorithm</CardTitle>
        <CardDescription>
          Using basic relevant closure algorithm to check for entailment.
        </CardDescription>
      </CardHeader>
      <CardContent>
        {!isLoading && basicRelevantEntailment && (
          <div>
            <Kb formulas={basicRelevantEntailment.knowledgeBase} set />
            <div className="mb-6">
              <QueryFormula formula={basicRelevantEntailment.queryFormula} />
            </div>
            <div className="mb-6">
              <p className="mb-3">               
                Relevant closure starts with the initial rankings constructed by the <i>BaseRank</i> algorithm.
              </p>
              <p className="font-medium">Output of the <i>BaseRank</i> algorithm</p>
              <RankingTable
                ranking={basicRelevantEntailment.baseRanking}
                caption="Ranks constructed by the Base Rank algorithm"
              />
            </div>
            <Explanation
              entailment={basicRelevantEntailment}
              className="mb-6 space-y-4"
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

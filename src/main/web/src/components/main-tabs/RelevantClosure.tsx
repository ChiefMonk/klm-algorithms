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
import { RationalEntailmentModel } from "@/lib/models";
import { Explanation } from "./common/explanations";
import { Kb, QueryFormula } from "./common/formulas";

interface RelevantClosureProps {
  isLoading: boolean;
  relevantEntailment: RationalEntailmentModel | null;
}

function RelevantClosure({
  isLoading,
  relevantEntailment,
}: RelevantClosureProps): JSX.Element {
  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-lg font-bold">Relevant Closure</CardTitle>
        <CardDescription>
          Using relevant closure algorithm to check for entailment.
        </CardDescription>
      </CardHeader>
      <CardContent>
        {!isLoading && relevantEntailment && (
          <div>
            <Kb formulas={relevantEntailment.knowledgeBase} set />
            <div className="mb-6">
              <QueryFormula formula={relevantEntailment.queryFormula} />
            </div>
            <div className="mb-6">
              <p className="mb-3">
                Relevant closure starts with the initial rankings constructed by
                the Base Rank algorithm.
              </p>
              <p className="font-medium">Initial ranks</p>
              <RankingTable
                ranking={relevantEntailment.baseRanking}
                caption="Ranks constructed by the Base Rank algorithm"
              />
            </div>
            <Explanation
              entailment={relevantEntailment}
              className="mb-6 space-y-4"
            />
          </div>
        )}
        {isLoading && <ResultSkeleton />}
        {!isLoading && !relevantEntailment && <NoResults />}
      </CardContent>
    </Card>
  );
}

export { RelevantClosure };

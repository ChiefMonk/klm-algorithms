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

interface RationalClosureProps {
  isLoading: boolean;
  rationalEntailment: RationalEntailmentModel | null;
}

function RationalClosure({
  isLoading,
  rationalEntailment,
}: RationalClosureProps): JSX.Element {
  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-lg font-bold">Rational Closure</CardTitle>
        <CardDescription>
          Using rational closure algorithm to check for entailment.
        </CardDescription>
      </CardHeader>
      <CardContent>
        {!isLoading && rationalEntailment && (
          <div>
            <Kb formulas={rationalEntailment.knowledgeBase} set />
            <div className="mb-6">
              <QueryFormula formula={rationalEntailment.queryFormula} />
            </div>
            <div className="mb-6">
              <p className="mb-3">
                Rational closure starts with the initial rankings constructed by
                the Base Rank algorithm.
              </p>
              <p className="font-medium">Initial ranks</p>
              <RankingTable
                ranking={rationalEntailment.baseRanking}
                caption="Ranks constructed by the Base Rank algorithm"
              />
            </div>
            <Explanation
              entailment={rationalEntailment}
              className="mb-6 space-y-4"
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

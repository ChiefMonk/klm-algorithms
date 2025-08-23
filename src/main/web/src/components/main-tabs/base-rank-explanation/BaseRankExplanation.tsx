import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { ResultSkeleton } from "@/components/main-tabs/ResultSkeleton";
import { NoResults } from "../NoResults";
import { IBaseRankExplanation } from "@/lib/models";
import { BaseRankContent } from "./BaseRankContent";

interface BaseRankExplanationProps {
  isLoading: boolean;
  baseRankExplanation: IBaseRankExplanation | null;
}

function BaseRankExplanation({
  isLoading,
  baseRankExplanation,
}: BaseRankExplanationProps): JSX.Element {
  if (isLoading) return <ResultSkeleton />;
  if (!baseRankExplanation) return <NoResults />;

  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-lg font-bold">
          The Base Rank Algorithm
        </CardTitle>
        <CardDescription>
          Using the base rank algorithm to determine the initial ranks.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <BaseRankContent baseRankExplanation={baseRankExplanation} />
      </CardContent>
    </Card>
  );
}

export { BaseRankExplanation };

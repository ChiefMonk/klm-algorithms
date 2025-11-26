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
import { Formula } from "../common/formulas";

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
        <CardTitle className="text-2xl font-bold">
          The Base Rank
        </CardTitle>
        <CardDescription className="text-base text-medium">
        With respect to a given defeasible knowledge base  <Formula formula="\mathcal{K}" />, the <b>base rank</b>, <Formula formula="br_{\mathcal{K}}(\alpha)" />, of a formula <Formula formula="\alpha \in \mathcal{L}" /> is the smallest index <i>r</i> such that <Formula formula="\alpha" />  is not exceptional in <Formula formula="\mathcal{E}^{\mathcal{K}}_{r}" />.
        The base rank of a defeasible implication with respect to <Formula formula="\mathcal{K}" />, <Formula formula="br_{\mathcal{K}}(\alpha ~> \beta)" />, is simply the base rank of its antecedent, <Formula formula="br_{\mathcal{K}}(\alpha)" />.  
        The intuition behind this ranking is that a lower rank indicates a higher degree of defeasibility.
        The <b>BaseRank</b> algorithm to determine the base ranking of statements in <Formula formula="\mathcal{K}" />.
        </CardDescription>      
      </CardHeader>
      <CardContent>
        <BaseRankContent baseRankExplanation={baseRankExplanation} />
      </CardContent>
    </Card>
  );
}

export { BaseRankExplanation };

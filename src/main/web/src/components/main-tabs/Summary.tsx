import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { NoResults } from "./NoResults";
import { ResultSkeleton } from "./ResultSkeleton";
import { EntailmentTable, TimesTable } from "./tables/other-tables";
import { RankingTable } from "./tables/ranking-table";
import { QueryInputContainer } from "./common/query-input";

import { useReasonerContext } from "@/state/reasoner.context";
import { AlgosSummary } from "./algos-summary";
import { InferenceOperator, QueryType } from "@/lib/models";
import { Formula } from "./common/formulas";

function Summary(): JSX.Element { 
  const {
    queryType,
    resultPending: isLoading,
    queryInput,
    entailmentQueryResult,
  } = useReasonerContext();

  const baseRank = entailmentQueryResult?.baseRank ?? null;
  const rationalEntailment = entailmentQueryResult?.rationalEntailment ?? null;
  const lexicalEntailment = entailmentQueryResult?.lexicalEntailment ?? null;
  const minimalRelevantEntailment =
    entailmentQueryResult?.minimalRelevantEntailment ?? null;
  const basicRelevantEntailment =
    entailmentQueryResult?.basicRelevantEntailment ?? null;

  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-2xl font-bold">
        {queryType === QueryType.Justification
        ? "Summary of Entailment and Explanation Algorithm Results"
        : "Summary of Entailment Algorithm Results"}        
          </CardTitle>

        <CardDescription className="text-base">
        {queryType === QueryType.Justification
        ? "A summary of defeasible entailment and justification algorithm results"
        : "A summary of defeasible entailment algorithm results"}             
        </CardDescription>
      </CardHeader>
      <CardContent>
        {!isLoading && (
          <div className="space-y-6">
            <QueryInputContainer
              knowledgeBase={queryInput?.knowledgeBase || []}
              queryFormula={queryInput?.queryFormula || ""}
            />

            <div>
              <h4 className="scroll-m-20 text-lg font-bold tracking-tight">
                Algorithm Results
              </h4>
              <EntailmentTable
                rationalEntailment={rationalEntailment}
                lexicalEntailment={lexicalEntailment}
                basicRelevantEntailment={basicRelevantEntailment}
                minimalRelevantEntailment={minimalRelevantEntailment}
              />
            </div>
            <div>
              <h4 className="scroll-m-20 text-lg font-bold tracking-tight">
                Base Rank of statements in <Formula formula="\mathcal{K}" /> produced by the <i>BaseRank</i> algorithm
              </h4>
              <RankingTable ranking={baseRank?.ranking || []} />
            </div>

            <div>
              <h4 className="scroll-m-20 mb-4 text-lg font-bold tracking-tight">

                  {queryType === QueryType.Justification
        ? "Results of Entailment and Explanation Algorithms"
        : "Results of Entailment Algorithms"}        
              </h4>
              <div className="grid grid-cols-1 gap-4 lg:grid-cols-2 mb-12">
                {rationalEntailment && (
                  <AlgosSummary
                    operator={InferenceOperator.RationalClosure}
                    entailment={rationalEntailment}
                  />
                )}

                {lexicalEntailment && (
                  <AlgosSummary
                    operator={InferenceOperator.LexicographicClosure}
                    entailment={lexicalEntailment}
                  />
                )}

                {basicRelevantEntailment && (
                  <AlgosSummary
                    operator={InferenceOperator.BasicRelevantClosure}
                    entailment={basicRelevantEntailment}
                  />
                )}

                {minimalRelevantEntailment && (
                  <AlgosSummary
                    operator={InferenceOperator.MinimalRelevantClosure}
                    entailment={minimalRelevantEntailment}
                  />
                )}
              </div>
            </div>

            <div>
              <h4 className="scroll-m-20  text-lg font-bold tracking-tight">
                Algorithm Execution Times (in seconds)
              </h4>
              <TimesTable
                baseRank={baseRank}
                rationalEntailment={rationalEntailment}
                lexicalEntailment={lexicalEntailment}
                basicRelevantEntailment={basicRelevantEntailment}
                minimalRelevantEntailment={minimalRelevantEntailment}
              />
            </div>
          </div>
        )}
        {!isLoading &&
          !(
            baseRank ||
            rationalEntailment ||
            lexicalEntailment ||
            basicRelevantEntailment ||
            minimalRelevantEntailment
          ) && <NoResults />}
        {isLoading && <ResultSkeleton />}
      </CardContent>
    </Card>
  );
}

export { Summary };

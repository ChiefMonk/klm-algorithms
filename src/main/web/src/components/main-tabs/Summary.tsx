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
  const minimalRelevantEntailment =  entailmentQueryResult?.minimalRelevantEntailment ?? null;
  const basicRelevantEntailment = entailmentQueryResult?.basicRelevantEntailment ?? null;

  let knowledgeBase: string[] = [];

  const sources = [
    rationalEntailment,
    lexicalEntailment,
    basicRelevantEntailment,
    minimalRelevantEntailment
  ];
  
  for (const src of sources) {
    if (src != null && src?.knowledgeBase?.length > 0) {
      knowledgeBase = src.knowledgeBase;
      break;
    }
  }

  if (knowledgeBase.length === 0 && queryInput?.knowledgeBase != null && queryInput?.knowledgeBase?.length > 0) {
    knowledgeBase = queryInput?.knowledgeBase;
  }
   
  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-2xl font-bold">
          {queryType === QueryType.Justification
            ? "Summary of Defeasible Entailment and Explanation Results"
            : "Summary of Defeasible Entailment Results"}
        </CardTitle>

        <CardDescription className="text-base text-medium">
          {queryType === QueryType.Justification
            ? "This summary presents the results of defeasible entailment algorithms, which determine whether a query formula can be inferred from a knowledge base using various inference operators (Rational Closure, Lexicographic Closure, Basic Relevant Closure, and Minimal Relevant Closure). The explanation results provide minimal sets of formulas that justify the entailment, showing which knowledge base statements are necessary to support the conclusion. Additionally, the summary includes the base rank of statements produced by the BaseRank algorithm, detailed algorithm results, and execution times for performance analysis."
            : "This summary presents the results of defeasible entailment algorithms, which determine whether a query formula can be inferred from a knowledge base using various inference operators (Rational Closure, Lexicographic Closure, Basic Relevant Closure, and Minimal Relevant Closure). The summary includes the base rank of statements produced by the BaseRank algorithm, showing how knowledge base statements are ranked according to their exceptionality. Additionally, detailed algorithm results and execution times are provided for each inference operator to enable performance analysis and comparison."}
        </CardDescription>
      </CardHeader>
      <CardContent>
        {!isLoading && (
          <div className="space-y-6">
            <QueryInputContainer
              knowledgeBase={knowledgeBase}
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

                {basicRelevantEntailment && (
                  <AlgosSummary
                    operator={InferenceOperator.BasicRelevantClosure}
                    entailment={basicRelevantEntailment}
                  />
                )}


                {lexicalEntailment && (
                  <AlgosSummary
                    operator={InferenceOperator.LexicographicClosure}
                    entailment={lexicalEntailment}
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

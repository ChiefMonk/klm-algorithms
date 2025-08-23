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

function Summary(): JSX.Element {
  const {
    resultPending: isLoading,
    queryInput,
    entailmentQueryResult,
  } = useReasonerContext();

  const baseRank = entailmentQueryResult?.baseRank ?? null;
  const rationalEntailment = entailmentQueryResult?.rationalEntailment ?? null;
  const lexicalEntailment = entailmentQueryResult?.lexicalEntailment ?? null;
  const minimalRelevantEntailment = entailmentQueryResult?.minimalRelevantEntailment ?? null;
  const basicRelevantEntailment = entailmentQueryResult?.basicRelevantEntailment ?? null;  

  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-lg font-bold">Summary of Results</CardTitle>
        <CardDescription>
          A summary of entailment and justification algorithm results
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
              <h4 className="scroll-m-20 font-medium tracking-tight">
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
              <h4 className="scroll-m-20 font-medium tracking-tight">
                Initial Ranks
              </h4>
              <RankingTable ranking={baseRank?.ranking || []} />
            </div>

            <div>
              <h4 className="scroll-m-20 font-medium tracking-tight">
                Remaining Ranks and Statements
              </h4>

              {rationalEntailment && (
                <>
                  <h5 className="text-sm text-muted-foreground mt-2 font-medium">
                    Rational Closure
                  </h5>
                  <RankingTable ranking={rationalEntailment.remainingRanks} />
                </>
              )}

              {lexicalEntailment && (
                <>
                  <h5 className="text-sm text-muted-foreground mt-2 font-medium">
                    Lexicographic Closure
                  </h5>
                  <RankingTable ranking={lexicalEntailment.remainingRanks} />
                </>
              )}
              
              {basicRelevantEntailment && (
                <>
                  <h5 className="text-sm text-muted-foreground mt-2 font-medium">
                  Basic Relevant Closure
                  </h5>
                  <RankingTable ranking={basicRelevantEntailment.remainingRanks} />
                </>
              )}

              {minimalRelevantEntailment && (
                <>
                  <h5 className="text-sm text-muted-foreground mt-2 font-medium">
                    Minimal Relevant Closure
                  </h5>
                  <RankingTable ranking={minimalRelevantEntailment.remainingRanks} />
                </>
              )}

             
            </div>

            <div>
              <h4 className="scroll-m-20 font-medium tracking-tight">
                Time Taken
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

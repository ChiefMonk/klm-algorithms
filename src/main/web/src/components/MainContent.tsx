import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Summary } from "./main-tabs/Summary";
import { LexicographicClosure } from "./main-tabs/LexicographicClosure";
import { BasicRelevantClosure } from "./main-tabs/BasicRelevantClosure";
import { MinimalRelevantClosure } from "./main-tabs/MinimalRelevantClosure";
import { QueryInputs } from "./inputs/QueryInputs";
import { SyncLoader } from "react-spinners";
import { BaseRankExplanation } from "./main-tabs/base-rank-explanation/BaseRankExplanation";
import { RationalClosureExplanation } from "./main-tabs/rc-explanation/RationalClosureExplanation";
import { InferenceOperator } from "@/lib/models";
import { useEffect } from "react";
import { useReasonerContext } from "@/state/reasoner.context";

export function MainContent() {
  const reasoner = useReasonerContext();
  const inferenceOperators = reasoner.entailmentQueryResult?.inferenceOperators;

  useEffect(() => {
    console.log("Entailment Query Result:", reasoner.entailmentQueryResult);
  }, [reasoner.entailmentQueryResult, reasoner.resultPending]);

  return (
    <>
      {(reasoner.resultPending || reasoner.inputPending) && (
        <div className="fixed top-0 left-0 w-screen h-screen z-10 flex items-center justify-center">
          <SyncLoader
            color="#0ea5e9"
            loading={reasoner.inputPending || reasoner.resultPending}
          />
        </div>
      )}
      <div className="flex flex-col gap-6 w-full">
        <QueryInputs
          isLoading={reasoner.inputPending}
          queryInput={reasoner.queryInput}
          submitKnowledgeBase={reasoner.createInputKnowledgeBase}
          uploadKnowledgeBase={reasoner.createFileKnowledgeBase}
          updateFormula={reasoner.updateFormula}
          generateKnowledgeBase={reasoner.generateKnowledgeBase}
        />
        {inferenceOperators && inferenceOperators.length > 0 && (
          <Tabs defaultValue="summary">
            <TabsList className="grid grid-cols-2 sm:grid-cols-5 flex-wrap h-auto space-y-1'">
              <TabsTrigger value="summary">Summary</TabsTrigger>
              <TabsTrigger value="baseRankExplanation">Base Rank</TabsTrigger>
              {inferenceOperators.includes(
                InferenceOperator.RationalClosure
              ) && (
                <TabsTrigger value="rationaClosure">
                  Rational Closure
                </TabsTrigger>
              )}
              {inferenceOperators.includes(
                InferenceOperator.LexicographicClosure
              ) && (
                <TabsTrigger value="lexicographicClosure">
                  Lexicographic Closure
                </TabsTrigger>
              )}
              {inferenceOperators.includes(
                InferenceOperator.BasicRelevantClosure
              ) && (
                <TabsTrigger value="basicRelevantClosure">
                  Basic Relevant Closure
                </TabsTrigger>
              )}
               {inferenceOperators.includes(
                InferenceOperator.MinimalRelevantClosure
              ) && (
                <TabsTrigger value="minimalRelevantClosure">
                  Minimal Relevant Closure
                </TabsTrigger>
              )}
            </TabsList>
            <TabsContent value="summary">
              <Summary />
            </TabsContent>
            <TabsContent value="baseRankExplanation">
              <BaseRankExplanation
                isLoading={reasoner.resultPending}
                baseRankExplanation={
                  reasoner.entailmentQueryResult?.baseRankExplanation || null
                }
              />
            </TabsContent>
            {inferenceOperators.includes(InferenceOperator.RationalClosure) && (
              <TabsContent value="rationaClosure">
                <RationalClosureExplanation
                  isLoading={reasoner.resultPending}
                  rationalExplanation={
                    reasoner.entailmentQueryResult?.rationalExplanation || null
                  }
                />
              </TabsContent>
            )}
            {inferenceOperators.includes(
              InferenceOperator.LexicographicClosure
            ) && (
              <TabsContent value="lexicographicClosure">
                <LexicographicClosure
                  isLoading={reasoner.resultPending}
                  lexicalEntailment={
                    reasoner.entailmentQueryResult?.lexicalEntailment || null
                  }
                />
              </TabsContent>
            )}

            {inferenceOperators.includes(InferenceOperator.BasicRelevantClosure) && (
              <TabsContent value="basicRelevantClosure">
                <BasicRelevantClosure
                  isLoading={reasoner.resultPending}
                  basicRelevantEntailment={
                    reasoner.entailmentQueryResult?.basicRelevantEntailment || null
                  }
                />
              </TabsContent>
            )}
            
            {inferenceOperators.includes(InferenceOperator.MinimalRelevantClosure) && (
              <TabsContent value="minimalRelevantClosure">
                <MinimalRelevantClosure
                  isLoading={reasoner.resultPending}
                  minimalRelevantEntailment={
                    reasoner.entailmentQueryResult?.minimalRelevantEntailment || null
                  }
                />
              </TabsContent>
            )}      
          </Tabs>
        )}
      </div>
    </>
  );
}

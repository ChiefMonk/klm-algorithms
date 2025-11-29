import { SyncLoader } from "react-spinners";
import { useEffect, useState } from "react";

import { Tabs, TabsContent, TabsList, TabsTrigger } from "../ui/tabs";
import { EvaluationChart } from "./evaluation-chart";
import { EvaluationTable } from "./evaluation-table";

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "../ui/card";
import { INFERENCE_OPERATORS } from "@/lib/constants";
import { EvaluationQueryCard } from "./evaluation-query-card";
import { useReasonerContext } from "@/state/reasoner.context";
import { Separator } from "../ui/separator";

const descriptionMap: Record<string, string> = {
  'Category 1 Test Cases': "The first set of test cases measures how long each algorithm takes to locate query antecedents within a ranked defeasible knowledge base with 300 statements distributed linearly across 60 ranks with some classical statements in rank infinity.",
  'Category 2 Test Cases': "This second set of test cases measures how long each algorithm takes to locate query antecedents within a ranked defeasible knowledge base with 300 statements distributed reversed-linearly across 60 ranks with no classical statements.",
  'Category 3 Test Cases': "This third set of test cases extends the first and second tests by measuring not only the time to locate matching antecedents within the ranks but also the duration required to identify the subset of statements that renders a query antecedent consistent with defeasible knowledge base of 1400 statements distributed uniformly across 100 ranks with some classical statements in rank infinity.",
  'Category 4 Test Cases': "This fourth set of test cases recursively combines the datasets from the first and second test but randomly selecting only a ranked defeasible knowledge base with 1400 statements distributed linearly across 300.",
  'Category 5 Test Cases': "This fifth set of test cases measures how long each algorithm takes to locate query antecedents within a ranked defeasible knowledge base with 300 statements distributed linearly across 60 ranks with some classical statements in rank infinity.",
};

export function EvaluationContent() {
  const reasoner = useReasonerContext();

  const evaluationData =
    reasoner.evaluation?.data.map((evaluation, index) => ({
      key: `group${index}`,
      label: evaluation.title,
      data: evaluation.data,
      inferenceOperator: evaluation.inferenceOperator,
    })) || [];

  const [activeTab, setActiveTab] = useState<string | null>();

  useEffect(() => {
    if (evaluationData.length > 0 && activeTab == null) {
      setActiveTab("group0");
    }
  }, [activeTab, evaluationData.length]);


  return (
    <>
      {(reasoner.resultPending || reasoner.inputPending) && (
        <div className="fixed top-0 left-0 w-screen h-screen z-10 flex items-center justify-center">
          <SyncLoader color="#0ea5e9" />
        </div>
      )}

      <div className="flex flex-col gap-6 w-full">
        <EvaluationQueryCard />
        <Separator className="h-1" />

        {activeTab && evaluationData.length > 0 && (
          <Tabs
            value={activeTab}
            onValueChange={setActiveTab}
            defaultValue="group0"
          >
            <TabsList className="grid grid-cols-2 sm:grid-cols-5 flex-wrap h-auto space-y-1">
              {evaluationData.map(
                (evaluation) =>
                  evaluation.data.length > 0 && (
                    <TabsTrigger key={evaluation.key} value={evaluation.key}>
                      {evaluation.label}
                    </TabsTrigger>
                  )
              )}
            </TabsList>

            {evaluationData.map(
              (evaluation) =>

                evaluation.data.length > 0 && (
                  <TabsContent key={evaluation.key} value={evaluation.key}>
                    <Card>
                      <CardHeader className="text-center">
                        <CardTitle className="text-2xl">
                          {INFERENCE_OPERATORS.get(
                            evaluation.inferenceOperator
                          )}{" "}
                          - {evaluation.label}
                        </CardTitle>
                        <CardDescription>
                          {descriptionMap[evaluation.label] ?? `Description for ${evaluation.label}`}
                        </CardDescription>
                      </CardHeader>
                      <CardContent className="grid grid-cols-1 gap-6 xl:grid-cols-12">
                        <EvaluationTable
                          data={evaluation.data}
                          caption={evaluation.label}
                          className="xl:col-span-5"
                        />
                        <EvaluationChart
                          data={evaluation.data}
                          className="xl:col-span-7"
                        />
                      </CardContent>
                    </Card>
                  </TabsContent>
                )
            )}
          </Tabs>
        )}
      </div>
    </>
  );
}

import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { useReasoner } from "@/hooks/use-reasoner";
import { Summary } from "./main-tabs/Summary";
import { BaseRank } from "./main-tabs/BaseRank";
import { RationalClosure } from "./main-tabs/RationalClosure";
import { LexicographicClosure } from "./main-tabs/LexicographicClosure";
import { QueryInputs } from "./inputs/QueryInputs";
import BarLoader from "react-spinners/BarLoader";

export function MainContent() {
  const reasoner = useReasoner();

  return (
    <>
      <div className="fixed top-0 left-0 w-screen z-10">
        <BarLoader
          cssOverride={{ width: "100vw" }}
          color="#0ea5e9"
          loading={reasoner.inputPending || reasoner.resultPending}
        />
      </div>
      <div className="flex flex-col gap-6 w-full">
        <QueryInputs
          isLoading={reasoner.inputPending}
          queryInput={reasoner.queryInput}
          submitKnowledgeBase={reasoner.updateKnowledgeBase}
          uploadKnowledgeBase={reasoner.uploadKnowledgeBase}
          submitQuery={reasoner.fetchQueryResult}
          updateFormula={reasoner.updateFormula}
        />
        <Tabs defaultValue="summary">
          <TabsList className="grid grid-cols-2 sm:grid-cols-4 flex-wrap h-auto space-y-1'">
            <TabsTrigger value="summary">Summary</TabsTrigger>
            <TabsTrigger value="baseRank">Base Rank</TabsTrigger>
            <TabsTrigger value="rationaClosure">Rational Closure</TabsTrigger>
            <TabsTrigger value="lexicographicClosure">
              Lexicographic Closure
            </TabsTrigger>
          </TabsList>
          <TabsContent value="summary">
            <Summary
              isLoading={reasoner.resultPending}
              baseRank={reasoner.queryResult?.baseRank || null}
              lexicalEntailment={
                reasoner.queryResult?.lexicalEntailment || null
              }
              rationalEntailment={
                reasoner.queryResult?.rationalEntailment || null
              }
            />
          </TabsContent>
          <TabsContent value="baseRank">
            <BaseRank
              isLoading={reasoner.resultPending}
              baseRank={reasoner.queryResult?.baseRank || null}
            />
          </TabsContent>
          <TabsContent value="rationaClosure">
            <RationalClosure
              isLoading={reasoner.resultPending}
              rationalEntailment={
                reasoner.queryResult?.rationalEntailment || null
              }
            />
          </TabsContent>
          <TabsContent value="lexicographicClosure">
            <LexicographicClosure
              isLoading={reasoner.resultPending}
              lexicalEntailment={
                reasoner.queryResult?.lexicalEntailment || null
              }
            />
          </TabsContent>
        </Tabs>
      </div>
    </>
  );
}

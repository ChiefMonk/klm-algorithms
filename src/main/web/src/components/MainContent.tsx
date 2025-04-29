import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { useReasoner } from "@/hooks/use-reasoner";
import { Summary } from "./main-tabs/Summary";
import { BaseRank } from "./main-tabs/BaseRank";
import { RationalClosure } from "./main-tabs/RationalClosure";
import { LexicographicClosure } from "./main-tabs/LexicographicClosure";
import { RelevantClosure } from "./main-tabs/RelevantClosure";
import { QueryInputs } from "./inputs/QueryInputs";
import BarLoader from "react-spinners/BarLoader";
import Divider from "./Divider";

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
          submitKnowledgeBase={reasoner.createInputKnowledgeBase}
          uploadKnowledgeBase={reasoner.createFileKnowledgeBase}
          submitQuery={reasoner.fetchQueryResult}
          updateFormula={reasoner.updateFormula}
          generateKnowledgeBase={reasoner.generateKnowledgeBase}
        />
        <Divider />
        <Tabs defaultValue="summary">
          <TabsList className="grid grid-cols-2 sm:grid-cols-5 flex-wrap h-auto space-y-1'">
            <TabsTrigger value="summary">Summary</TabsTrigger>
            <TabsTrigger value="baseRank">Base Rank</TabsTrigger>
            <TabsTrigger value="rationaClosure">Rational Closure</TabsTrigger>
            <TabsTrigger value="lexicographicClosure">Lexicographic Closure</TabsTrigger>
            <TabsTrigger value="relevantClosure">Relevant Closure</TabsTrigger>
          </TabsList>
          <TabsContent value="summary">
            <Summary
              isLoading={reasoner.resultPending}
              baseRank={reasoner.queryResult?.baseRank || null}
             
              rationalEntailment={
                reasoner.queryResult?.rationalEntailment || null
              }

              lexicalEntailment={
                reasoner.queryResult?.lexicalEntailment || null
              }

              relevantEntailment={
                reasoner.queryResult?.relevantEntailment || null
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
          <TabsContent value="relevantClosure">
            <RelevantClosure
              isLoading={reasoner.resultPending}
              relevantEntailment={
                reasoner.queryResult?.relevantEntailment || null
              }
            />
          </TabsContent>
        </Tabs>
      </div>
    </>
  );
}

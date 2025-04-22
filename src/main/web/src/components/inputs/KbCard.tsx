import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { KbForm } from "./kb-form";
import { Formula, Kb } from "../main-tabs/common/formulas";
import { GenerateForm } from "./genereteForm";

// Type for generate form submission
interface GenerateData {
  numberOfRanks: number;
  complexity: string[];
  distributionType: string;
  defeasibleImplications: number;
}

interface KbCardProps {
  isLoading: boolean;
  knowledgeBase: string[];
  signature: string[];
  submitKnowledgeBase: (knowledgeBase: string[]) => void;
  uploadKnowledgeBase: (data: FormData) => void;
  generateKnowledgeBase: (data: GenerateData) => void;
}

function KbCard({
  isLoading,
  knowledgeBase,
  submitKnowledgeBase,
  uploadKnowledgeBase,
  generateKnowledgeBase
}: KbCardProps) {
  const [state, setState] = useState({
    editing: false,
    fromGenerating: false,
    fromFile: false
  });

  const setMode = (mode: Partial<typeof state>) => {
    setState({ editing: true, fromGenerating: false, fromFile: false, ...mode });
  };

  const handleReset = () => {
    setState({ editing: false, fromGenerating: false, fromFile: false });
  };

  const handleGenerateSubmit = (data: GenerateData) => {
    console.log('i was clicked!!')
    generateKnowledgeBase(data);
    handleReset();
  };

  return (
    <Card className="h-full">
      <CardHeader className="space-y-0 pb-4">
        <CardTitle className="text-center font-semibold">
          The Knowledge Base, <Formula formula="\mathcal{K}" />
        </CardTitle>
      </CardHeader>

      <CardContent>
        {!state.editing && (
          <div className="w-full flex flex-col gap-4 items-center">
            <Kb formulas={knowledgeBase} />

            <hr className="w-full max-w-sm border-t border-gray-300 my-[5px]" />

            <div className="grid grid-cols-3 gap-4 w-full max-w-sm">
              <Button
                variant="secondary"
                className="border border-light-blue-500"
                onClick={() => setMode({})}
                disabled={isLoading}
              >
                Edit
              </Button>

              <Button
                variant="secondary"
                className="border border-light-blue-500"
                onClick={() => setMode({ fromFile: true })}
                disabled={isLoading}
              >
                Upload
              </Button>

              <Button
                variant="secondary"
                className="border border-light-blue-500"
                onClick={() => setMode({ fromGenerating: true })}
                disabled={isLoading}
              >
                Generate
              </Button>
            </div>
          </div>
        )}

        {state.editing && (
          <div className="w-full flex flex-col gap-4 items-center">
            {state.fromGenerating ? (
              <GenerateForm onSubmit={handleGenerateSubmit} onCancel={handleReset} />
            ) : (
              <KbForm
                defaultFormulas={knowledgeBase.join(",")}
                fromFile={state.fromFile}
                fromGenerating={state.fromGenerating}
                submitKnowledgeBase={submitKnowledgeBase}
                handleReset={handleReset}
                uploadKnowledgeBase={uploadKnowledgeBase}
              />
            )}
          </div>
        )}
      </CardContent>
    </Card>
  );
}

export { KbCard };

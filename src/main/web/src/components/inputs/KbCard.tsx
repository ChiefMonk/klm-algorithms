import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { KbForm } from "./kb-form";
import { Formula, Kb } from "../main-tabs/common/formulas";

interface KbCardProps {
  isLoading: boolean;
  knowledgeBase: string[];
  signature: string[];
  submitKnowledgeBase: (knowledgeBase: string[]) => void;
  uploadKnowledgeBase: (data: FormData) => void;
  generateKnowledgeBase: () => void;
}

function KbCard({
  isLoading,
  knowledgeBase,   
  submitKnowledgeBase,
  uploadKnowledgeBase 
}: KbCardProps) {
  const [state, setState] = useState({ editing: false,  fromGenerating: false, fromFile: false });

  const handleEdit = () => {
    setState((prev) => ({ ...prev, editing: true, fromGenerating: false, fromFile: false }));
  };

  const handleUpload = () => {
    setState((prev) => ({ ...prev, editing: true, fromGenerating: false, fromFile: true }));
  };

  const handleGenerate = () => {
    setState((prev) => ({ ...prev, editing: false, fromGenerating: true, fromFile: false }));
  };

  const handleReset = () => {
    setState((prev) => ({ ...prev, editing: false, fromGenerating: false, fromFile: false }));
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
                size="default"   
                className="border border-light-blue-500"           
                onClick={handleEdit}
                disabled={isLoading}
              >
                Edit
              </Button>

              <Button
                variant="secondary"
                size="default"   
                className="border border-light-blue-500"
                onClick={handleUpload}
                disabled={isLoading}
              >
                Upload
              </Button>

              <Button
                variant="secondary"
                size="default"  
                className="border border-light-blue-500"
                onClick={handleGenerate}
                disabled={isLoading}
              >
                Generate
              </Button>
            
            </div>
          </div>
        )}
        {state.editing && (
          <div className="w-full flex flex-col gap-4 items-center">
            <KbForm
              defaultFormulas={knowledgeBase.join(",")}
              fromFile={state.fromFile}
              fromGenerating={state.fromGenerating}
              submitKnowledgeBase={submitKnowledgeBase}
              handleReset={handleReset}
              uploadKnowledgeBase={uploadKnowledgeBase}            
            />
          </div>
        )}
      </CardContent>
    </Card>
  );
}

export { KbCard };

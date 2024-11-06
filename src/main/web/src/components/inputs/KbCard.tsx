import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { KbForm } from "./kb-form";
import { Formula, Kb } from "../main-tabs/common/formulas";

interface KbCardProps {
  isLoading: boolean;
  knowledgeBase: string[];
  submitKnowledgeBase: (knowledgeBase: string[]) => void;
  uploadKnowledgeBase: (data: FormData) => void;
}

function KbCard({
  isLoading,
  knowledgeBase,
  submitKnowledgeBase,
  uploadKnowledgeBase,
}: KbCardProps) {
  const [state, setState] = useState({ editing: false, fromFile: false });

  const handleEdit = () => {
    setState((prev) => ({ ...prev, editing: true, fromFile: false }));
  };

  const handleUpload = () => {
    setState((prev) => ({ ...prev, editing: true, fromFile: true }));
  };

  const handleReset = () => {
    setState((prev) => ({ ...prev, editing: false, fromFile: false }));
  };

  return (
    <Card className="h-full">
      <CardHeader className="space-y-0 pb-4">
        <CardTitle className="text-center font-semibold">
          Knowledge Base <Formula formula="(\mathcal{K})" />
        </CardTitle>
      </CardHeader>
      <CardContent>
        {!state.editing && (
          <div className="w-full flex flex-col gap-4 items-center">
            <Kb formulas={knowledgeBase} />
            <div className="grid grid-cols-2 gap-4 w-full max-w-sm">
              <Button
                variant="outline"
                onClick={handleUpload}
                disabled={isLoading}
              >
                Upload
              </Button>
              <Button
                variant="secondary"
                onClick={handleEdit}
                disabled={isLoading}
              >
                Edit
              </Button>
            </div>
          </div>
        )}
        {state.editing && (
          <div className="w-full flex flex-col gap-4 items-center">
            <KbForm
              defaultFormulas={knowledgeBase.join(",")}
              fromFile={state.fromFile}
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

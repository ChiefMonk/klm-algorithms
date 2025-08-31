import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { KbForm } from "./kb-form";
import { Formula, Kb } from "../main-tabs/common/formulas";
import { SquarePen, Upload, RefreshCw } from "lucide-react";
import { useReasonerContext } from "@/state/reasoner.context";

interface KbCardProps {
  isLoading: boolean;
  knowledgeBase: string[];
  signature: string[];
  submitKnowledgeBase: (knowledgeBase: string[]) => void;
  uploadKnowledgeBase: (data: FormData) => void;
}

enum KbCardState {
  View,
  Edit,
  Upload,
}

function KbCard({
  isLoading,
  knowledgeBase,
  submitKnowledgeBase,
  uploadKnowledgeBase,
}: KbCardProps) {
  const reasoner = useReasonerContext();
  const [state, setState] = useState<KbCardState>(KbCardState.View);

  const handleEdit = () => {
    setState(KbCardState.Edit);
  };

  const handleUpload = () => {
    setState(KbCardState.Upload);
  };

  const handleReset = () => {
    setState(KbCardState.View);
  };

  const handleDefault = () => {
    reasoner.fetchDefaultKnowledgeBase();
  };

  return (
    <Card className="h-full">
      <CardHeader className="space-y-0 pb-4">
        <CardTitle className="text-center font-semibold">
          The Knowledge Base, <Formula formula="\mathcal{K}" />
        </CardTitle>
      </CardHeader>

      <CardContent>
        {state === KbCardState.View && (
          <div className="w-full flex flex-col gap-4 items-center">
            <Kb formulas={knowledgeBase} />

            <div className="grid grid-cols-3 gap-4 w-full max-w-md mt-4">
              <Button
                variant="secondary"
                size="default"
                className="border border-light-blue-500"
                onClick={handleDefault}
                disabled={isLoading}
              >
                <RefreshCw className="mr-4" />
                Default
              </Button>

              <Button
                variant="secondary"
                size="default"
                className="border border-light-blue-500"
                onClick={handleEdit}
                disabled={isLoading}
              >
                <SquarePen className="mr-4" />
                Edit
              </Button>

              <Button
                size="default"
                className="border border-light-blue-500"
                onClick={handleUpload}
                disabled={isLoading}
              >
                <Upload className="mr-4" />
                Upload
              </Button>
            </div>
          </div>
        )}
        {(state === KbCardState.Edit || state === KbCardState.Upload) && (
          <div className="w-full flex flex-col gap-4 items-center">
            <KbForm
              defaultFormulas={knowledgeBase.join(",")}
              upload={state === KbCardState.Upload}
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

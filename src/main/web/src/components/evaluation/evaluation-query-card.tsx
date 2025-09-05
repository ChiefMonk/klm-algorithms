import { useForm } from "react-hook-form";
import { useEffect } from "react";
import { zodResolver } from "@hookform/resolvers/zod";

import { EvaluationFormSchema, EvaluationFormSchemaType } from "@/lib/schemas";
import { EvaluationConstants } from "@/lib/constants";
import {
  Distribution,
  CharacterSet,
  Generator,
  InferenceOperator,
} from "@/lib/models";

import { useReasonerContext } from "@/state/reasoner.context";

import { Form } from "../ui/form";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "../ui/card";
import { Separator } from "../ui/separator";
import { Button } from "../ui/button";

import {
  CheckboxGroupField,
  NumberInputField,
  RadioGroupField,
  SelectField,
  SwitchField,
} from "../form-fields";

import {
  algorithmOptions,
  characterSetOptions,
  complexityOptions,
  connectiveOptions,
  distributionOptions,
  generatorOptions,
  operatorOptions,
} from "@/lib/utils/form-options";

import { ImportEvaluationDialog } from "./import-evaluation-dialog";
import { ChartArea, DownloadIcon, Trash2Icon } from "lucide-react";

/**
 * Renders a form card to configure and submit evaluation parameters.
 * Uses form state and validation with Zod + React Hook Form.
 *
 * @returns {JSX.Element} EvaluationQueryCard component
 */
export function EvaluationQueryCard(): JSX.Element {
  const reasoner = useReasonerContext();

  const defaultValues = {
    parameters: {
      numberOfRanks: 1,
      numberOfDefeasibleImplications: 1,
      distribution: Distribution.Flat,
      antecedentComplexity: [],
      consequentComplexity: [],
      simpleDiOnly: false,
      reuseConsequent: false,
      connective: [],
      characterSet: CharacterSet.LowerLatin,
      generator: Generator.Standard,
    },
    algorithm: [],
    inferenceOperator:
      InferenceOperator[
        reasoner.evaluation?.query.inferenceOperator || "RationalClosure"
      ],
  };

  const form = useForm<EvaluationFormSchemaType>({
    resolver: zodResolver(EvaluationFormSchema),
    defaultValues: defaultValues,
  });

  useEffect(() => {
    if (reasoner.evaluation) {
      form.reset({
        parameters: reasoner.evaluation.query.parameters,
        algorithm: reasoner.evaluation.query.algorithms,
        inferenceOperator:
          InferenceOperator[reasoner.evaluation.query.inferenceOperator],
      });
    }
  }, [form, reasoner.evaluation]);

  const numberOfRanks = form.watch("parameters.numberOfRanks");

  // Ensure defeasible implications >= number of ranks
  useEffect(() => {
    const currentDI = form.getValues(
      "parameters.numberOfDefeasibleImplications"
    );
    if (currentDI < numberOfRanks) {
      form.setValue("parameters.numberOfDefeasibleImplications", numberOfRanks);
    }
  }, [numberOfRanks, form]);

  /**
   * Handles form submission by passing evaluation data
   * to the Reasoner context for processing.
   *
   * @param {EvaluationFormSchemaType} data - Submitted form data
   */
  const onSubmit = async (data: EvaluationFormSchemaType) => {
    reasoner.getEvaluation({
      inferenceOperator: data.inferenceOperator,
      algorithms: data.algorithm,
      parameters: data.parameters,
    });
  };

  const handleChange = () => {
    reasoner.deleteEvaluation();
  };
  return (
    <Form {...form}>
      <div className="flex flex-col gap-6">
        {/* Generator Parameters Card */}
        <Card>
          <CardHeader>
            <CardTitle>Knowledge Base Generator Parameters</CardTitle>
            <CardDescription>
              Select parameter values for the knowledge base generator
            </CardDescription>
          </CardHeader>
          <CardContent className="grid grid-cols-1 gap-8">
            <div className="grid grid-cols-5 gap-8">
              <NumberInputField
                form={form}
                name="parameters.numberOfRanks"
                label="Number of ranks"
                min={EvaluationConstants.NUMBER_OF_RANKS_MIN}
                handleChange={handleChange}
              />
              <NumberInputField
                form={form}
                name="parameters.numberOfDefeasibleImplications"
                label="Number of defeasible implications"
                min={numberOfRanks}
                handleChange={handleChange}
              />
              <SelectField
                form={form}
                name="parameters.distribution"
                label="Distribution"
                options={distributionOptions}
                handleChange={handleChange}
              />
              <SelectField
                form={form}
                name="parameters.characterSet"
                label="Character Set"
                options={characterSetOptions}
                handleChange={handleChange}
              />
              <SelectField
                form={form}
                name="parameters.generator"
                label="Generator"
                options={generatorOptions}
                handleChange={handleChange}
              />
            </div>

            <div className="grid grid-cols-3 gap-8">
              <CheckboxGroupField
                form={form}
                name="parameters.antecedentComplexity"
                label="Antecedent complexity"
                options={complexityOptions}
                handleChange={handleChange}
              />
              <CheckboxGroupField
                form={form}
                name="parameters.consequentComplexity"
                label="Consequent complexity"
                options={complexityOptions}
                handleChange={handleChange}
              />
              <CheckboxGroupField
                form={form}
                name="parameters.connective"
                label="Connectives"
                options={connectiveOptions}
                handleChange={handleChange}
              />
            </div>

            <div className="grid grid-cols-3 gap-8">
              <SwitchField
                form={form}
                name="parameters.simpleDiOnly"
                label="Simple defeasible implications only"
                handleChange={handleChange}
              />
              <SwitchField
                form={form}
                name="parameters.reuseConsequent"
                label="Reuse consequent"
                handleChange={handleChange}
              />
            </div>
          </CardContent>
        </Card>

        <Separator className="h-1" />

        {/* Inference and Algorithms Card */}
        <Card>
          <CardHeader>
            <CardTitle>Entailment Algorithm Implementation Categories</CardTitle>
            <CardDescription>
              Select entailment algorithm and implementation category to evaluate or import
              evaluation data
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-3 gap-8">
              <RadioGroupField
                form={form}
                name="inferenceOperator"
                label=">Entailment Algorithm"
                options={operatorOptions}
                handleChange={handleChange}
              />
              <CheckboxGroupField
                form={form}
                name="algorithm"
                label="Implementation Category"
                options={algorithmOptions}
                handleChange={handleChange}
              />
              <div className="flex justify-end">
                <div className="mt-auto flex flex-col gap-16 w-48">
                  <Button
                    className="shrink-0"
                    type="button"
                    onClick={form.handleSubmit(onSubmit)}
                  >
                    <ChartArea className="mr-4" />
                    Evaluate
                  </Button>
                  <div className="flex flex-col gap-4">
                    <ImportEvaluationDialog
                      handleClick={() => {
                        form.reset(defaultValues);
                      }}
                    />
                    {reasoner.evaluation &&
                      reasoner.evaluation.data.length > 0 && (
                        <>
                          <Button
                            className="shrink-0"
                            type="button"
                            variant="outline"
                            onClick={() => reasoner.exportEvaluation()}
                          >
                            <DownloadIcon className="mr-4" />
                            Export
                          </Button>
                          <Button
                            className="shrink-0"
                            type="button"
                            variant="outline"
                            onClick={() => {
                              reasoner.deleteEvaluation();
                              form.reset(defaultValues);
                            }}
                          >
                            <Trash2Icon className="mr-4" />
                            Clear
                          </Button>
                        </>
                      )}
                  </div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </Form>
  );
}

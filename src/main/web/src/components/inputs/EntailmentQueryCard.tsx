import { z } from "zod";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "../ui/card";
import { InferenceOperator } from "@/lib/models";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "../ui/form";
import { Button } from "../ui/button";
import { Checkbox } from "../ui/checkbox";
import { useReasonerContext } from "@/state/reasoner.context";
import { INFERENCE_OPERATORS } from "@/lib/constants";
import { BrainIcon, Trash2Icon } from "lucide-react";

const formSchema = z.object({
  inferenceOperator: z
    .array(z.nativeEnum(InferenceOperator))
    .refine((value) => value.some((item) => item), {
      message: "At least one inference operator must be selected.",
    }),
});

export function EntailmentQueryCard() {
  const reasoner = useReasonerContext();
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      inferenceOperator:
        reasoner.entailmentQueryResult?.inferenceOperators || [],
    },
  });

  const onSubmit = (values: z.infer<typeof formSchema>) => {
    reasoner.fetchEntailmentQueryResult(values.inferenceOperator);
  };

  const handleChange = () => {
    reasoner.deleteEntailment();
  };

  const operators = Object.values(InferenceOperator);

  return (
    <Card>
      <CardHeader>
        <CardTitle>Inference Operators</CardTitle>
        <CardDescription>Please select inference operator</CardDescription>
      </CardHeader>
      <CardContent>
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="grid grid-cols-3 gap-4 justify-between"
          >
            <FormField
              control={form.control}
              name="inferenceOperator"
              render={({ field }) => (
                <FormItem className="col-span-2">
                  <div className="flex flex-col gap-4">
                    {operators.map((operator) => (
                      <div className="flex items-center gap-2" key={operator}>
                        <FormControl>
                          <Checkbox
                            checked={field.value?.includes(operator)}
                            onCheckedChange={(checked) => {
                              checked
                                ? field.onChange([...field.value, operator])
                                : field.onChange(
                                    field.value?.filter(
                                      (value) => value !== operator
                                    )
                                  );

                              if (handleChange) {
                                handleChange();
                              }
                            }}
                          />
                        </FormControl>
                        <FormLabel className="text-sm font-normal">
                          {INFERENCE_OPERATORS.get(operator)}
                        </FormLabel>
                      </div>
                    ))}
                  </div>
                  <FormMessage />
                </FormItem>
              )}
            />
            <div className="flex justify-end">
              <div className="flex flex-col gap-8 w-48 mt-auto">
                <Button type="submit">
                  <BrainIcon className="mr-2" />
                  Query
                </Button>
                {reasoner.entailmentQueryResult && (
                  <Button
                    type="button"
                    variant="outline"
                    onClick={handleChange}
                  >
                    <Trash2Icon className="mr-2" />
                    Clear
                  </Button>
                )}
              </div>
            </div>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
}

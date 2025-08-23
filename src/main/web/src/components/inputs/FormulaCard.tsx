import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Formula } from "../main-tabs/common/formulas";
import { CircleCheckBig, CircleX, SquarePen } from "lucide-react";

const formSchema = z.object({
  formula: z
    .string()
    .min(1, {
      message: "Formula is required.",
    })
    .refine((formula) => {
      const symbol = "~>";
      const index = formula.indexOf(symbol);

      // Check if the symbol is found and is not at the start or end
      if (index > 0 && index + symbol.length < formula.length) {
        return true;
      } else {
        return false;
      }
    }, "Query formula needs to be defeasible implication, e.g. p~>f"),
});

interface FormulaCardProps {
  isLoading: boolean;
  queryFormula: string;
  updateFormula: (formula: string) => void;
}

export function FormulaCard({
  isLoading,
  queryFormula,
  updateFormula,
}: FormulaCardProps) {
  const [editing, setEditing] = useState(false);
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: { formula: queryFormula },
  });

  useEffect(() => {
    form.reset({ formula: queryFormula });
  }, [form, queryFormula]);

  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    updateFormula(values.formula);
    form.reset({ formula: queryFormula });
    setEditing(false);
  };

  const handleCancel = () => {
    form.reset({ formula: queryFormula });
    setEditing(false);
  };

  const handleEdit = () => {
    setEditing(true);
  };

  return (
    <Card className="h-full">
      <CardHeader className="space-y-0 pb-4">
        <CardTitle className="font-semibold text-center">
          The Query Formula <Formula formula="(\alpha)" />
        </CardTitle>
      </CardHeader>
      <CardContent className="flex justify-center">
        {!editing && (
          <div className="w-full flex flex-col gap-4 items-center">
            <Formula formula={queryFormula} />

            <div className="w-full max-w-sm flex items-center justify-center mt-4">
              <Button
                size="default"
                className="border border-light-blue-500 w-full max-w-48"
                onClick={handleEdit}
                disabled={isLoading}
              >
                <SquarePen className="mr-4" />
                Edit
              </Button>
            </div>
          </div>
        )}
        {editing && (
          <Form {...form}>
            <form
              onSubmit={form.handleSubmit(onSubmit)}
              className="flex flex-col gap-4 w-full max-w-sm"
            >
              <FormField
                control={form.control}
                name="formula"
                render={({ field }) => (
                  <FormItem>
                    <FormControl>
                      <Input
                        placeholder="formula"
                        {...field}
                        className="text-center bg-secondary/40"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <div className="grid grid-cols-2 gap-4 mt-4">
                <Button
                  type="button"
                  variant="outline"
                  onClick={handleCancel}
                  disabled={isLoading}
                >
                  <CircleX className="mr-4" />
                  Cancel
                </Button>
                <Button disabled={isLoading} type="submit">
                  <CircleCheckBig className="mr-4" />
                  Update
                </Button>
              </div>
            </form>
          </Form>
        )}
      </CardContent>
    </Card>
  );
}

import { Complexity, DistributionType, KbGenerationInput } from "@/lib/models";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "../ui/form";
import { Input } from "../ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import { Button } from "../ui/button";
import { useEffect } from "react";

interface GenerateKbFormProps {
  handleReset: () => void;
  handleSumbit: (input: KbGenerationInput) => void;
}

const NUMBER_OF_RANKS_MIN = 1;
const NUMBER_OF_RANKS_MAX = 10;

const NUMBER_OF_DI_MAX = 50;

const formSchema = z
  .object({
    numberOfRanks: z.number().min(NUMBER_OF_RANKS_MIN).max(NUMBER_OF_RANKS_MAX),
    distributionType: z.nativeEnum(DistributionType),
    complexity: z.nativeEnum(Complexity),
    numberOfDefeasibleImplications: z
      .number()
      .min(1, "Must be at least 1") // use a general min for base validation
      .max(NUMBER_OF_DI_MAX),
  })
  .superRefine(({ numberOfRanks, numberOfDefeasibleImplications }, ctx) => {
    if (numberOfDefeasibleImplications < numberOfRanks) {
      ctx.addIssue({
        path: ["numberOfDefeasibleImplications"],
        code: z.ZodIssueCode.too_small,
        minimum: numberOfRanks,
        type: "number",
        inclusive: true,
        message: `Must be at least the number of ranks (${numberOfRanks})`,
      });
    }
  });

function GenerateKbForm({ handleReset, handleSumbit }: GenerateKbFormProps) {
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      numberOfRanks: 10,
      distributionType: DistributionType.Uniform,
      complexity: Complexity.Low,
      numberOfDefeasibleImplications: 30,
    },
  });

  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    handleSumbit(values);
    handleReset();
  };

  const onCancel = () => {
    handleReset();
  };

  const numberOfRanks = form.watch("numberOfRanks");

  useEffect(() => {
    const currentDI = form.getValues("numberOfDefeasibleImplications");
    if (currentDI < numberOfRanks) {
      form.setValue("numberOfDefeasibleImplications", numberOfRanks);
    }
  }, [numberOfRanks, form]);

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4 w-full">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <FormField
            control={form.control}
            name="numberOfRanks"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Number of Ranks</FormLabel>
                <FormControl>
                  <Input
                    {...field}
                    className="text-center bg-secondary/40"
                    type="number"
                    onChange={(e) => field.onChange(e.target.valueAsNumber)}
                    min={NUMBER_OF_RANKS_MIN}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="distributionType"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Distribution Type</FormLabel>
                <Select
                  onValueChange={field.onChange}
                  value={field.value ?? ""}
                >
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue placeholder="Select type" />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    {Object.values(DistributionType).map((value) => (
                      <SelectItem key={value} value={value}>
                        {value}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="complexity"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Complexity</FormLabel>
                <Select
                  onValueChange={field.onChange}
                  value={field.value ?? ""}
                >
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue placeholder="Select complexity" />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    {Object.values(Complexity).map((value) => (
                      <SelectItem key={value} value={value}>
                        {value}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="numberOfDefeasibleImplications"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Number of Defeasible Implications</FormLabel>
                <FormControl>
                  <Input
                    {...field}
                    className="text-center bg-secondary/40"
                    type="number"
                    onChange={(e) => field.onChange(e.target.valueAsNumber)}
                    min={numberOfRanks}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <Button type="button" variant="outline" onClick={onCancel}>
            Cancel
          </Button>
          <Button type="submit" variant="secondary">
            Generate
          </Button>
        </div>
      </form>
    </Form>
  );
}

export { GenerateKbForm };

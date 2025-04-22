import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";

const formSchema = z.object({
  numberOfRanks: z.coerce
    .number()
    .min(1, "Number of Ranks must be at least 1")
    .max(100, "Number of Ranks must be at most 100"),
  complexity: z
    .array(z.string())
    .nonempty("At least one complexity level must be selected"),
  distributionType: z.string().min(1, "Distribution Type is required"),
  defeasibleImplications: z.coerce
    .number()
    .min(55, "Must be at least 55")
    .max(999, "Must be less than 1000"),
});

type FormValues = z.infer<typeof formSchema>;

interface GenerateFormProps {
  onSubmit: (data: FormValues) => void;
  onCancel: () => void;
}

const MOCK_GENERATED_RULES: string[] = [
  "p ~> m",
  "p ~> t",
  "s ~> b",
  "s ~> !t",
  "u ~> !b",
  "u ~> b",
  "u => s",
  "a => p",
  "s => p",
];

function GenerateForm({ onSubmit, onCancel }: GenerateFormProps) {
  const [generatedRules, setGeneratedRules] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);

  const handleGenerate = async (data: FormValues) => {
    setLoading(true);
    try {
      // You can replace this with an actual fetch to URL_KB_GET_GENERATE
      // const response = await fetch("URL_KB_GET_GENERATE");
      // const json = await response.json();

      // Mocking the response
      const json = MOCK_GENERATED_RULES;

      setGeneratedRules(json);
      onSubmit(data); // optional if you still want to call the original onSubmit
    } catch (error) {
      console.error("Failed to generate rules", error);
    } finally {
      setLoading(false);
    }
  };

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      numberOfRanks: 1,
      complexity: [],
      distributionType: "",
      defeasibleImplications: 55,
    },
  });

  return (
    <Form {...form}>
      <form
        onSubmit={form.handleSubmit(handleGenerate)}
        className="space-y-4 w-full max-w-xl"
      >
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <FormField
            control={form.control}
            name="numberOfRanks"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Number of Ranks</FormLabel>
                <FormControl>
                  <Input
                    type="number"
                    min={1}
                    max={100}
                    placeholder="Enter 1–100"
                    {...field}
                    className="text-center"
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
                  defaultValue={field.value}
                >
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue placeholder="Select type" />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    <SelectItem value="Flat">Flat</SelectItem>
                    <SelectItem value="Linear">Linear</SelectItem>
                    <SelectItem value="Random">Random</SelectItem>
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
                <div className="flex flex-wrap gap-4">
                  {["Low", "Medium", "High"].map((option) => (
                    <div key={option} className="flex items-center space-x-2">
                      <Checkbox
                        checked={field.value.includes(option)}
                        onCheckedChange={(checked) => {
                          const updatedValue = checked
                            ? [...field.value, option]
                            : field.value.filter((val) => val !== option);
                          field.onChange(updatedValue);
                        }}
                      />
                      <span>{option}</span>
                    </div>
                  ))}
                </div>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="defeasibleImplications"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Number of Defeasible Implications</FormLabel>
                <FormControl>
                  <Input
                    type="number"
                    min={55}
                    max={999}
                    placeholder="Enter 55–999"
                    {...field}
                    className="text-center"
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
          <Button type="submit" variant="secondary" disabled={loading}>
            {loading ? "Generating..." : "Generate"}
          </Button>
        </div>
        {generatedRules.length > 0 && (
          <div className="mt-6 p-4 border rounded bg-muted">
            <h3 className="font-semibold mb-2">Generated Rules</h3>
            <ul className="list-disc list-inside space-y-1">
              {generatedRules.map((rule, idx) => (
                <li key={idx}>{rule}</li>
              ))}
            </ul>
          </div>
        )}
      </form>
    </Form>
  );
}

export { GenerateForm };

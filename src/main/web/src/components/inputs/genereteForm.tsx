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
  numberOfRanks: z
    .number()
    .min(1, "Number of Ranks must be at least 1")
    .max(100, "Number of Ranks must be at most 100"),
  complexity: z.array(z.string()).nonempty("At least one complexity level must be selected"),
  distributionType: z.string().min(1, "Distribution Type is required"),
  defeasibleImplications: z
    .number()
    .min(55, "Must be at least 55")
    .max(999, "Must be less than 1000"),
});


type FormValues = z.infer<typeof formSchema>;

interface GenerateFormProps {
  onSubmit: (data: FormValues) => void;
  onCancel: () => void;
}

function GenerateForm({ onSubmit, onCancel }: GenerateFormProps) {
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
        onSubmit={form.handleSubmit(onSubmit)}
        className="space-y-4 w-full max-w-sm"
      >
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
                  placeholder="Enter a number between 1 and 100"
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
          name="complexity"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Complexity</FormLabel>
              <div className="flex space-x-4">
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
          name="distributionType"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Distribution Type</FormLabel>
              <Select onValueChange={field.onChange} defaultValue={field.value}>
                <FormControl>
                  <SelectTrigger>
                    <SelectValue placeholder="Select distribution type" />
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
          name="defeasibleImplications"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Number of Defeasible Implications</FormLabel>
              <FormControl>
                <Input
                  type="number"
                  min={55}
                  max={999}
                  placeholder="Enter a number between 55 and 999"
                  {...field}
                  className="text-center"
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
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

export { GenerateForm };

import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { FieldPath, FieldValues, UseFormReturn } from "react-hook-form";

/**
 * Reusable form number input field.
 *
 * @template T - Form schema type
 * @param form - RHF form instance
 * @param name - Field path
 * @param label - Label text
 * @param min - Minimum value (optional)
 */
export function NumberInputField<T extends FieldValues>({
  form,
  name,
  label,
  min,
  handleChange,
}: {
  form: UseFormReturn<T>;
  name: FieldPath<T>;
  label: string;
  min?: number;
  handleChange?: () => void;
}) {
  return (
    <FormField
      control={form.control}
      name={name}
      render={({ field }) => (
        <FormItem>
          <FormLabel>{label}</FormLabel>
          <FormControl>
            <Input
              {...field}
              type="number"
              min={min}
              onChange={(e) => {
                field.onChange(e.target.valueAsNumber);
                if (handleChange) {
                  handleChange();
                }
              }}
              className="bg-secondary/40"
            />
          </FormControl>
          <FormMessage />
        </FormItem>
      )}
    />
  );
}

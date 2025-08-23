import {
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
import { FieldPath, FieldValues, UseFormReturn } from "react-hook-form";

/**
 * Reusable select field.
 *
 * @template T - Form schema type
 * @param form - RHF form instance
 * @param name - Field path
 * @param label - Field label
 * @param options - Tuple of value-label pairs
 */
export function SelectField<T extends FieldValues>({
  form,
  name,
  label,
  options,
  handleChange,
}: {
  form: UseFormReturn<T>;
  name: FieldPath<T>;
  label: string;
  options: { value: string; label: string }[];
  handleChange?: () => void;
}) {
  return (
    <FormField
      control={form.control}
      name={name}
      render={({ field }) => (
        <FormItem>
          <FormLabel>{label}</FormLabel>
          <Select
            onValueChange={(e) => {
              field.onChange(e);
              if (handleChange) {
                handleChange();
              }
            }}
            value={field.value ?? ""}
          >
            <FormControl>
              <SelectTrigger className="bg-secondary/40">
                <SelectValue placeholder={`Select ${label.toLowerCase()}`} />
              </SelectTrigger>
            </FormControl>
            <SelectContent>
              {options.map((opt) => (
                <SelectItem key={opt.value} value={opt.value}>
                  {opt.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <FormMessage />
        </FormItem>
      )}
    />
  );
}

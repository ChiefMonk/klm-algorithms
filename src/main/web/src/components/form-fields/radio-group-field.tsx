import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { cn } from "@/lib/utils";
import { FieldPath, FieldValues, UseFormReturn } from "react-hook-form";

/**
 * Renders a radio group from string enum options.
 */
export function RadioGroupField<T extends FieldValues>({
  form,
  name,
  label,
  options,
  className,
  handleChange,
}: {
  form: UseFormReturn<T>;
  name: FieldPath<T>;
  label: string;
  options: { value: string; label: string }[];
  className?: string;
  handleChange?: () => void;
}) {
  return (
    <FormField
      control={form.control}
      name={name}
      render={({ field }) => (
        <FormItem className="space-y-3">
          <FormLabel>{label}</FormLabel>
          <FormControl>
            <RadioGroup
              onValueChange={(e) => {
                field.onChange(e);
                if (handleChange) {
                  handleChange();
                }
              }}
              value={field.value}
              className={cn("flex flex-col gap-2", className)}
            >
              {options.map((opt) => (
                <FormItem
                  key={opt.value}
                  className="flex items-center space-x-2 space-y-0"
                >
                  <FormControl>
                    <RadioGroupItem value={opt.value} id={opt.value} />
                  </FormControl>
                  <FormLabel
                    htmlFor={opt.value}
                    className="text-sm font-normal"
                  >
                    {opt.label}
                  </FormLabel>
                </FormItem>
              ))}
            </RadioGroup>
          </FormControl>
          <FormMessage />
        </FormItem>
      )}
    />
  );
}

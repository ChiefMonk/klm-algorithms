import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Checkbox } from "@/components/ui/checkbox";
import { FieldPath, FieldValues, UseFormReturn } from "react-hook-form";
import { IFormFieldOption } from "@/lib/models";
import { cn } from "@/lib/utils";

/**
 * Renders a group of checkboxes for multiselect fields.
 */
export function CheckboxGroupField<T extends FieldValues, U>({
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
  options: IFormFieldOption<U>[];
  className?: string;
  handleChange?: () => void;
}) {
  return (
    <FormField
      control={form.control}
      name={name}
      render={() => (
        <FormItem className="space-y-3">
          <FormLabel>{label}</FormLabel>
          <div className={cn("flex flex-col gap-2", className)}>
            {options.map((opt, idx) => (
              <FormField
                key={idx}
                control={form.control}
                name={name}
                render={({ field }) => (
                  <FormItem key={idx}>
                    <div
                      key={idx}
                      className={cn("flex items-center gap-2", className)}
                    >
                      <FormControl>
                        <Checkbox
                          checked={field.value?.includes(opt.value)}
                          onCheckedChange={(checked) => {
                            checked
                              ? field.onChange([...field.value, opt.value])
                              : field.onChange(
                                  field.value.filter(
                                    (v: string) => v !== opt.value
                                  )
                                );
                            if (handleChange) {
                              handleChange();
                            }
                          }}
                        />
                      </FormControl>
                      <FormLabel className="text-sm font-normal">
                        {opt.label}
                      </FormLabel>
                    </div>
                  </FormItem>
                )}
              />
            ))}
          </div>
          <FormMessage />
        </FormItem>
      )}
    />
  );
}

import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
} from "@/components/ui/form";
import { Switch } from "@/components/ui/switch";
import { FieldPath, FieldValues, UseFormReturn } from "react-hook-form";

/**
 * Boolean switch toggle input.
 */
export function SwitchField<T extends FieldValues>({
  form,
  name,
  label,
  handleChange,
}: {
  form: UseFormReturn<T>;
  name: FieldPath<T>;
  label: string;
  handleChange?: () => void;
}) {
  return (
    <FormField
      control={form.control}
      name={name}
      render={({ field }) => (
        <FormItem>
          <FormControl>
            <div className="flex flex-row items-center gap-2">
              <Switch
                checked={field.value}
                onCheckedChange={(e) => {
                  field.onChange(e);
                  if (handleChange) {
                    handleChange();
                  }
                }}
              />
              <FormLabel>{label}</FormLabel>
            </div>
          </FormControl>
        </FormItem>
      )}
    />
  );
}

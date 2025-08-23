import { z } from "zod";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";

import { Button } from "../ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../ui/dialog";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "../ui/form";
import { Input } from "../ui/input";
import { useReasonerContext } from "@/state/reasoner.context";
import { CircleX, Upload } from "lucide-react";

/**
 * Zod schema for file upload validation.
 * - Only allows JSON files
 * - Max file size: 5MB
 */
const formSchema = z.object({
  file: z
    .instanceof(File, { message: "File is required" })
    .refine((file) => file.size < 5 * 1024 * 1024, {
      message: "File must be less than 5MB",
    })
    .refine(
      (file) =>
        file.type === "application/json" ||
        file.name.toLowerCase().endsWith(".json"),
      {
        message: "Only JSON files are allowed",
      }
    ),
});

type FormSchemaType = z.infer<typeof formSchema>;

interface ImportEvaluationDialogProps {
  handleClick?: () => void;
}
/**
 * A modal dialog that allows users to import evaluation data via a CSV file.
 *
 * @component
 * @param {ImportEvaluationDialogProps} props - Component props
 * @returns {JSX.Element}
 */
export function ImportEvaluationDialog({
  handleClick,
}: ImportEvaluationDialogProps): JSX.Element {
  const reasoner = useReasonerContext();

  const [open, setOpen] = useState(false);

  const form = useForm<FormSchemaType>({
    resolver: zodResolver(formSchema),
  });

  /**
   * Handles the file upload form submission.
   *
   * @param values - Validated form values
   */
  const onSubmit = (values: FormSchemaType) => {
    if (handleClick) {
      handleClick();
    }
    const formData = new FormData();
    formData.append("file", values.file);
    reasoner.importEvaluation(formData);
    close();
  };

  /**
   * Handles the opening/closing of the dialog.
   *
   * @param isOpen - Whether the dialog is open
   */
  const onOpenChange = (isOpen: boolean): void => {
    setOpen(isOpen);
    form.reset();
  };

  /**
   * Closes the dialog.
   */
  const close = (): void => {
    setOpen(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogTrigger asChild>
        <Button type="button" variant="secondary">
          <Upload className="mr-4" />
          Import
        </Button>
      </DialogTrigger>

      <DialogContent className="w-full max-w-md">
        <DialogHeader>
          <DialogTitle className="text-xl">Import evaluation data</DialogTitle>
          <DialogDescription>
            Please select the file to import data from.
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <div className="space-y-4 w-full">
            <FormField
              control={form.control}
              name="file"
              render={({ field }) => (
                <FormItem>
                  <FormControl>
                    <Input
                      className="text-center bg-secondary/40"
                      type="file"
                      accept=".json"
                      onChange={(e) =>
                        field.onChange(e.target.files?.[0] ?? null)
                      }
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <div className="grid grid-cols-2 gap-4">
              <Button type="button" variant="secondary" onClick={close}>
                <CircleX className="mr-4" />
                Cancel
              </Button>
              <Button onClick={form.handleSubmit(onSubmit)}>
                <Upload className="mr-4" />
                Update
              </Button>
            </div>
          </div>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

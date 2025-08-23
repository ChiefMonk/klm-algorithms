import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { CircleCheckBig, CircleX, Upload } from "lucide-react";

const formSchema = z.object({
  formulas: z.string().min(1, {
    message: "Knowledge base is required.",
  }),
  file: z
    .any()
    .refine((file) => file instanceof File, {
      message: "File is required.",
    })
    .optional(),
});

interface KbFormProps {
  defaultFormulas: string;
  upload: boolean;
  handleReset: () => void;
  submitKnowledgeBase: (knowledgeBase: string[]) => void;
  uploadKnowledgeBase: (data: FormData) => void;
}

function KbForm({
  defaultFormulas,
  upload,
  handleReset,
  submitKnowledgeBase,
  uploadKnowledgeBase,
}: KbFormProps) {
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: { formulas: defaultFormulas },
  });

  useEffect(() => {
    form.reset({ formulas: defaultFormulas });
  }, [defaultFormulas, form]);

  const onCancel = () => {
    form.reset({ formulas: defaultFormulas });
    handleReset();
  };
  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    if (upload) {
      if (values.file) {
        const formData = new FormData();
        formData.append("file", values.file);
        uploadKnowledgeBase(formData);
        handleReset();
      } else {
        form.setError("file", {
          type: "manual",
          message: "File is required",
        });
      }
    } else {
      submitKnowledgeBase(values.formulas.split(","));
      handleReset();
    }
  };

  return (
    <Form {...form}>
      <form
        onSubmit={form.handleSubmit(onSubmit)}
        className="flex flex-col gap-4 w-full max-w-sm"
      >
        {!upload && (
          <FormField
            control={form.control}
            name="formulas"
            render={({ field }) => (
              <FormItem>
                <FormControl>
                  <Input
                    placeholder="formula1, formula2, ..."
                    {...field}
                    className="text-center bg-secondary/40"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        )}

        {upload && (
          <FormField
            control={form.control}
            name="file"
            render={({ field }) => (
              <FormItem>
                <FormControl>
                  <Input
                    className="text-center bg-secondary/40"
                    type="file"
                    accept=".txt"
                    onChange={(e) => {
                      field.onChange(e.target.files ? e.target.files[0] : null);
                    }}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        )}
        <div className="grid grid-cols-2 gap-4 mt-4">
          <Button type="button" variant="outline" onClick={onCancel}>
            <CircleX className="mr-4" />
            Cancel
          </Button>
          <Button type="submit">
            {upload && <Upload className="mr-4" />}
            {!upload && <CircleCheckBig className="mr-4" />}
            Update
          </Button>
        </div>
      </form>
    </Form>
  );
}

export { KbForm };

import { z } from "zod";
import { EvaluationConstants } from "@/lib/constants";
import {
  Algorithm,
  CharacterSet,
  Complexity,
  Connective,
  Distribution,
  Generator,
  InferenceOperator,
} from "@/lib/models";

/** Evaluation form schema */
const EvaluationFormSchema = z
  .object({
    /** Inference operator */
    inferenceOperator: z.nativeEnum(InferenceOperator, {
      message: EvaluationConstants.INFERENCE_OPERATOR_REQUIRED_MESSAGE,
    }),

    /** Type of algorithm */
    algorithm: z
      .array(z.nativeEnum(Algorithm))
      .refine((value) => value.length > 0, {
        message: EvaluationConstants.ALGORITHM_REQUIRED_MESSAGE,
      }),

    parameters: z.object({
      /** Number of ranks */
      numberOfRanks: z
        .number()
        .min(EvaluationConstants.NUMBER_OF_RANKS_MIN, {
          message: EvaluationConstants.NUMBER_OF_RANKS_MIN_MESSAGE,
        })
        .max(EvaluationConstants.NUMBER_OF_RANKS_MAX, {
          message: EvaluationConstants.NUMBER_OF_RANKS_MAX_MESSAGE,
        }),

      /** Number of defeasible implications */
      numberOfDefeasibleImplications: z
        .number()
        .max(EvaluationConstants.NUMBER_OF_DI_MAX, {
          message: EvaluationConstants.NUMBER_OF_DI_MAX_MESSAGE,
        }),

      /** Distribution type */
      distribution: z.nativeEnum(Distribution),

      /** Antecedent complexity */
      antecedentComplexity: z
        .array(z.nativeEnum(Complexity))
        .refine((value) => value.length > 0, {
          message: EvaluationConstants.COMPLEXITY_REQUIRED_MESSAGE,
        }),

      /** Antecedent complexity */
      consequentComplexity: z
        .array(z.nativeEnum(Complexity))
        .refine((value) => value.length > 0, {
          message: EvaluationConstants.COMPLEXITY_REQUIRED_MESSAGE,
        }),

      /** Simple defeasible implications only? */
      simpleDiOnly: z.boolean(),

      /** Reuse consequent? */
      reuseConsequent: z.boolean(),

      /** Connective types */
      connective: z
        .array(z.nativeEnum(Connective))
        .refine((value) => value.length > 0, {
          message: EvaluationConstants.CONNECTIVE_REQUIRED_MESSAGE,
        }),

      /** Character set for knowledge base */
      characterSet: z.nativeEnum(CharacterSet),

      /** Generator type */
      generator: z.nativeEnum(Generator),
    }),
  })
  .superRefine(({ parameters }, ctx) => {
    if (parameters.numberOfDefeasibleImplications < parameters.numberOfRanks) {
      ctx.addIssue({
        path: ["parameters", "numberOfDefeasibleImplications"],
        code: z.ZodIssueCode.too_small,
        minimum: parameters.numberOfRanks,
        type: "number",
        inclusive: true,
        message: EvaluationConstants.NUMBER_OF_DI_MIN_MESSAGE(
          parameters.numberOfRanks
        ),
      });
    }
  });

/** Evaluation form schema type */
type EvaluationFormSchemaType = z.infer<typeof EvaluationFormSchema>;

export { EvaluationFormSchema, type EvaluationFormSchemaType };

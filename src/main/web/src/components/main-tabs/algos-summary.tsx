import { EntailmentModel, InferenceOperator, QueryType } from "@/lib/models";
import { Card, CardContent, CardHeader, CardTitle } from "../ui/card";
import { toTex } from "@/lib/formula";
import { Formula, Kb } from "./common/formulas";
import { INFERENCE_OPERATORS } from "@/lib/constants";
import { useReasonerContext } from "@/state/reasoner.context";

interface AlgosSummaryProps {
  operator: InferenceOperator;
  entailment: EntailmentModel;
}

export function AlgosSummary({ operator, entailment }: AlgosSummaryProps) {
  const { queryType } = useReasonerContext();

  const entailmentResultFormula = entailment.entailed
    ? toTex("\\mathcal{K} \\vapprox " + entailment.queryFormula)
    : toTex("\\mathcal{K} \\nvapprox " + entailment.queryFormula);

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-slate-700 font-bold">
          {INFERENCE_OPERATORS.get(operator)}
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div
          className="grid items-baseline"
          style={{ gridTemplateColumns: "auto 1fr", gap: "0.5rem 2rem" }}
        >
          <div className="text-slate-500 font-medium">Remaining ranks</div>
          <div>
            {(() => {
              const items = entailment.remainingRanking.sort(
                (a, b) => a.rankNumber - b.rankNumber
              );
              if (!items || items.length === 0) return null;
              if (items.length === 1) {
                return (
                  <Formula formula={`\\mathcal{R}_{${items[0].rankNumber}}`} />
                );
              }
              return items.map((rank, i) => {
                const isLast = i === items.length - 1;
                if (isLast) {
                  return (
                    <span key={i}>
                      and{" "}
                      <Formula formula={`\\mathcal{R}_{${rank.rankNumber}}`} />
                    </span>
                  );
                } else {
                  return (
                    <span key={i}>
                      <Formula formula={`\\mathcal{R}_{${rank.rankNumber}}`} />
                      {i < items.length - 2 ? ", " : " "}
                    </span>
                  );
                }
              });
            })()}
          </div>
          <div className="text-slate-500 font-medium">Entailment result</div>
          <div>
            <Formula formula={entailmentResultFormula} />
          </div>

          {queryType === QueryType.Justification && (
            <>
              <div className="text-slate-500 font-medium">Justifcation</div>
              <div>
                <Kb
                  formulas={entailment.justification.flat()}
                  name="\mathcal{J}_{1}"
                  set
                />
              </div>
            </>
          )}
        </div>
      </CardContent>
    </Card>
  );
}

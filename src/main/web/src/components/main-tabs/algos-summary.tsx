import { EntailmentModel, InferenceOperator, QueryType } from "@/lib/models";
import { Card, CardContent, CardHeader, CardTitle } from "../ui/card";
import { toTex } from "@/lib/formula";
import { Formula, Kb, KbSimple } from "./common/formulas";
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
          {operator === InferenceOperator.BasicRelevantClosure || operator === InferenceOperator.MinimalRelevantClosure ? (
            <>
              <div className="text-slate-500 font-medium">Relevant statements</div>
              <div>
                <KbSimple
                  formulas={entailment.relevantKnowledgeBase}
                  set
                />
              </div>
            </>
          ) : null}

          <div className="text-slate-500 font-medium">
            {operator === InferenceOperator.RationalClosure
              ? "Discarded ranks"
              : "Discarded statements"}</div>
          <div>
            {operator === InferenceOperator.RationalClosure ? (
              (() => {
                const items = entailment.removedRanking.sort(
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
                        <Formula formula="\cup" /> <Formula formula={`\\mathcal{R}_{${rank.rankNumber}}`} />
                      </span>
                    );
                  } else {
                    return (
                      <span key={i}>
                        <Formula formula={`\\mathcal{R}_{${rank.rankNumber}}`} />
                        {i < items.length - 2 ? <Formula formula="\cup" /> : " "}
                      </span>
                    );
                  }
                });
              })()
            ) : (
              <KbSimple
                formulas={entailment.removedKnowledgeBase}
                set
              />
            )}
          </div>


          <div className="text-slate-500 font-medium">
            {operator === InferenceOperator.RationalClosure
              ? "Remaining ranks"
              : "Remaining statements"}</div>
          <div>
            {operator === InferenceOperator.RationalClosure ? (
              (() => {
                const items = [...entailment.remainingRanking].sort(
                  (a, b) => a.rankNumber - b.rankNumber
                );

                if (!items?.length) return null;

                if (items.length === 1) {
                  return <Formula formula={`\\mathcal{R}_{${items[0].rankNumber}}`} />;
                }

                return items.map((rank, i) => {
                  const isLast = i === items.length - 1;
                  return isLast ? (
                    <span key={rank.rankNumber ?? i}>
                      <Formula formula="\cup" /> <Formula formula={`\\mathcal{R}_{${rank.rankNumber}}`} />
                    </span>
                  ) : (
                    <span key={rank.rankNumber ?? i}>
                      <Formula formula={`\\mathcal{R}_{${rank.rankNumber}}`} />
                      {i < items.length - 2 ? <Formula formula="\cup" /> : " "}
                    </span>
                  );
                });
              })()
            ) : (
              <KbSimple
                formulas={entailment.remainingKnowledgeBase}
                set
              />
            )}
          </div>

          <div className="text-slate-500 font-medium">Entailment result</div>
          <div>
            <Formula formula={entailmentResultFormula} />
          </div>

          {queryType === QueryType.Justification && (
            <>
              <div className="text-slate-500 font-medium">Deciding Statements</div>
              <div>
                
                <Kb
                  formulas={entailment.entailmentKnowledgeBase}
                  name={`\\mathcal{D}`}
                  set
                />
              </div>
              <div className="text-slate-500 font-medium">Justifcation Sets</div>
              <div className="space-y-2">
                {entailment.justification.map((just, index) => (
                  <Kb
                    key={index}
                    formulas={just}
                    name={`\\mathcal{J}_{${index + 1}}`}
                    set
                  />
                ))}
              </div>
            </>
          )}
        </div>
      </CardContent>
    </Card>
  );
}

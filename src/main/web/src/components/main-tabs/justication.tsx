import { EntailmentModel, QueryType } from "@/lib/models";
import { Kb } from "./common/formulas";
import { EntailResult, Formula } from "./common/formulas";

interface JustificationProps {
  queryType: QueryType;
  entailment: EntailmentModel | null;
  entailed: boolean;
  queryFormula: string
}

export function Justification({
  queryType,
  entailment,
  queryFormula,
  entailed,
}: JustificationProps): JSX.Element | null {
  // Render nothing unless we’re in Justification mode and have data
  if (queryType !== QueryType.Justification || !entailment) return null;

  const hasJustifications =
    Array.isArray(entailment.justification) && entailment.justification.length > 0;

  var signature1 = queryFormula.replaceAll("(", "").split("~>")[0];
  var signature2 = queryFormula.replaceAll(")", "").split("~>")[1];
  var signature = signature1.trim() + "," + signature2.trim();
  const classical = queryFormula.replaceAll("~>", "=>");
  const decidingKb = entailment.remainingRanks.flatMap(rank => rank.formulas).join(", ").replaceAll("~>", "=>");


  return (
    <div>
      <h1 className="text-lg font-bold mb-2">
        B. Entailment Explanation
      </h1>

      {entailed && hasJustifications ? (
        <div>
          <p className="mb-3">
            <ul className="list-disc list-inside">
              <li>The <i>deciding knowledge base</i> <Formula formula="\mathcal{D}" /> and a materialised query are passed as inputs to the <i>universal justication</i> algorithm to determine the justification sets, <Formula formula="\{\mathcal{J}_{1},\dots,\mathcal{J}_{i}\}" />, for <EntailResult formula={queryFormula} entailed={entailed} />.</li>
              <li>
                Therefore, <Formula formula={"\\overrightarrow{\\mathcal{D}}"} /> {" "}={" "} <Formula formula="\{" /><Formula formula={decidingKb} /><Formula formula="\}" />.
              </li>
              <li>The computation of all classical justication sets for <EntailResult formula={queryFormula} entailed={entailed} /> employs a <i>hitting set tree</i> algorithm constructed in a <i>breadth-first</i> manner.</li>
              <li>This involves a recursive algorithm that finds a single justification set at a time, incoporating the <i>expansion</i> and <i>contraction</i> stages, starting with the query <i>signature</i>, <Formula formula="\{" /><Formula formula={signature} /> <Formula formula="\}" />.</li>
              <li>If the query <Formula formula={`${queryFormula}`} />  <Formula formula="\lor" /> <Formula formula={`${queryFormula}`} /> <Formula formula="\in \mathcal{D}" />, then a set with only that one statement is one of the defeasible justifications sets for <Formula formula={`\\overrightarrow{\\mathcal{K}} \\models ${classical}`} />.</li>
              <li>The Justification sets for <EntailResult formula={queryFormula} entailed={entailed} /> from <Formula formula={"\\mathcal{D} \\subseteq \\mathcal{K}"} /> are:</li>
            </ul>

            {entailment.justification.map((just, index) => (
              <div key={index} style={{ marginLeft: '3rem' }}>
                <Kb
                  formulas={just}
                  name={`\\mathcal{J}_{${index + 1}}`}
                  set
                />
              </div>
            ))}
          </p>
        </div>
      ) : (

        <p className="mb-3">
          <ul className="list-disc list-inside">
            <li>Since <EntailResult formula={queryFormula} entailed={entailed} />, the <i>deciding knowledge base</i> <Formula formula="\mathcal{D} = \{\}" />.</li>
            <li>Therefore, there is no determination of any justification sets for this non-entailment of the query <Formula formula={queryFormula} />.</li>
          </ul>
        </p>

      )}


    </div>
  );
}

import { TexFormula } from "@/components/main-tabs/common/TexFormula";
import { allRanks, allRanksEntail, toTex } from "@/lib/formula";

interface AllRanksProps {
  /** The first finite rank. */
  start: number;
  /** The last finite rank. */
  end: number;
}

export function AllRanks({ start, end }: AllRanksProps) {
  return <TexFormula>{allRanks(start, end)}</TexFormula>;
}

interface AllRanksEntailProps extends AllRanksProps {
  /** Formula to entail/not entail. */
  formula: string;
  /** Entailment. */
  entailed?: boolean;
}

export function AllRanksEntail({
  start,
  end,
  formula,
  entailed = true,
}: AllRanksEntailProps) {
  return (
    <TexFormula>{allRanksEntail(start, end, formula, entailed)}</TexFormula>
  );
}

interface FormulaProps {
  formula: string;
}

export function Formula({ formula }: FormulaProps) {
  return <TexFormula>{toTex(formula)}</TexFormula>;
}

interface EntailResultProps {
  entailed: boolean;
  formula: string;
}

export function EntailResult({ formula, entailed }: EntailResultProps) {
  const K = "\\mathcal{K}";
  const symbol = entailed ? "\\vapprox" : "\\nvapprox";
  const result = K + " " + symbol + " " + formula;
  return <Formula formula={result} />;
}

interface KbProps {
  name?: string;
  formulas: string[];
  set?: boolean;
}
export function Kb({ name = "\\mathcal{K}", formulas, set = false }: KbProps) {
  return (
    <div className="line-clamp-1">
      {set && <Formula formula={`${name}=\\{\\;`} />}
      {formulas.map((formula, index, array) => (
        <span key={index}>
          <Formula formula={formula} />
          {index < array.length - 1 && <Formula formula=",\;" />}
        </span>
      ))}
      {set && <Formula formula="\;\}" />}
    </div>
  );
}

interface QueryFormulaProps {
  formula: string;
}

export function QueryFormula({ formula }: QueryFormulaProps) {
  return <Formula formula={"\\alpha = " + formula} />;
}

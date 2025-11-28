import { IBaseRankExplanation, ISequenceElement } from "../models";

export function buildExceptionalityCheckFormula(
  index: number,
  antecedentNegation: string,
  isExceptional: boolean
): string {
  const symbol = !isExceptional ? "\\vapprox\\top" : "\\vapprox\\bot";
  return `*_{${index}}${symbol}\\vsim{${antecedentNegation.trim()}}`;
}

export function buildExceptionalityCheckResult(
  formula: string,
  isExceptional: boolean
): { formula: string; text: string } {
  return {
    formula: `${formula.replace("(", "").replace(")", "").trim()}`,
    //formula: `therefore, ${formula}\\quad`,
    text: isExceptional ? "is <not exceptional> and hence remains in the current index" : "is <exceptional> and hence is moved to the next index",
  };
}

export function buildExceptionalityElementFormula(
  elementIndex: number,
  element: ISequenceElement
) {
  return `*_{${elementIndex}}${
    element.elementNumber === 0 ? "=\\mathcal{K}" : ""
  }=\\{${element.formulas}\\}`;
}

export function bulidRankFromExceptionalitySequence(
  ranks: IBaseRankExplanation["ranks"],
  sequence: IBaseRankExplanation["sequence"]
): string[] {
  const builtRanks: string[] = [];
  ranks = ranks.sort((a, b) => a.rankNumber - b.rankNumber);
  sequence = sequence.sort((a, b) => a.elementNumber - b.elementNumber);

  const n = ranks.length;

  const start = "\\begin{aligned}";
  const end = "\\end{aligned}";

  for (let i = 0; i < n; i++) {
    let formula = start + `\\mathcal{R}_{${ranks[i].rankNumber}}&=`;
    if (i === n - 1) {
      formula += `*_{${sequence[i].elementNumber}}\\\\&=\\{${sequence[i].formulas}\\}`;
    } else {
      formula += `*_{${sequence[i].elementNumber}}\\setminus *_{${
        sequence[i + 1].elementNumber
      }}\\\\`;
      formula += `&=\\{${sequence[i].formulas}\\}\\setminus \\{${
        sequence[i + 1].formulas
      }\\}\\\\`;
      formula += `&=\\{${ranks[i].formulas}\\}`;
    }
    formula += end;
    builtRanks.push(formula);
  }
  return builtRanks;
}

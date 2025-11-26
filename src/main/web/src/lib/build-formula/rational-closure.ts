import { IRank, IRationalClosureExplanation } from "../models";

export function unionRanksName(ranks: IRank[]): string {
  return ranks
    .map((rank) => `\\mathcal{\\overrightarrow{R}}_{${rank.rankNumber}}`)
    .join("\\cup ");
}

export function aligned(formula: string): string {
  return `\\begin{aligned}${formula}\\end{aligned}`;
}

export function buildRankUnion(ranks: IRank[]): string {
  const unionRanksKb = ranks
    .map((rank) =>
      rank.formulas.map((f) => f.replaceAll("~>", "=>")).join(", ")
    )
    .join(" \\cup ");

  const unionRanksKbName = unionRanksName(ranks);
  return `${unionRanksKbName} = \\{${unionRanksKb}\\}`;
}

export function buildRankCheck(
  check: IRationalClosureExplanation["checks"][number]
): string {
  return (
    unionRanksName(check.ranks) +
    (!check.isConsistent ? "" : "\\not") +
    "\\models " +
    check.antecedentNegation
  );
}

export function buildFinalEntailmentCheck(
  explanation: IRationalClosureExplanation
): string {
  const check = explanation.checks[explanation.checks.length - 1];
  return (
    unionRanksName(check.ranks) +
    (explanation.isEntailed ? "" : "\\not") +
    "\\models " +
    explanation.queryFormula.replaceAll("~>", "=>")
  );
}

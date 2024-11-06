/**
 * Convert formula from plain text to Tex formula.
 * @param formula formula to convert.
 * @returns Tex string
 */
function toTex(formula: string): string {
  if (formula == "+") {
    return "\\top"; // tautology
  }
  return formula
    .replaceAll("!", " \\neg ")
    .replaceAll("&&", " \\land ")
    .replaceAll("||", " \\lor ")
    .replaceAll("<=>", " \\leftrightarrow ")
    .replaceAll("=>", " \\to ")
    .replaceAll("~>", " \\vsim ");
}

/**
 * Gets the label for all ranks combined.
 * @param start The starting rank.
 * @param end The ending rank.
 * @returns Tex string representing union of all ranks.
 */
function allRanks(start: number, end: number) {
  const infiniteRank = "R_{\\infty}";
  const finiteRanks = `\\left( \\bigcup_{j=${start}}^{j < ${end}} R_j \\right)`;
  if (start >= end) {
    return infiniteRank;
  }
  return infiniteRank + " \\cup " + finiteRanks;
}

/**
 * Gets the label for the entailment of the ranks.
 * @param start The starting rank.
 * @param end The ending rank.
 * @param formula The formula to entail/not entail.
 * @param entailed Entailment.
 * @returns Tex string representing the entailment of the ranks.
 */
function allRanksEntail(
  start: number,
  end: number,
  formula: string,
  entailed: boolean = true
) {
  const ranks = allRanks(start, end);
  const symbol = entailed ? "\\models" : "\\not\\models";
  return ranks + " " + symbol + " " + toTex(formula);
}

export { allRanks, allRanksEntail, toTex };

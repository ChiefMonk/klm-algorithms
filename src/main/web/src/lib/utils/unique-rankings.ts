import { IBaseRankExplanation } from "@/lib/models";

function getUniqueRankings(sequence: IBaseRankExplanation["sequence"]) {
  return Array.from(
    new Map(
      sequence.map((element) => [
        element.elementNumber,
        {
          rankNumber: element.elementNumber,
          formulas: element.formulas,
        },
      ])
    ).values()
  );
}

export { getUniqueRankings };

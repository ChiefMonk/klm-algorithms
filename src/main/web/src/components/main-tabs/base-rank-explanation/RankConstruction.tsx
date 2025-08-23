import { bulidRankFromExceptionalitySequence } from "@/lib/build-formula/base-rank";
import { IBaseRankExplanation } from "@/lib/models";
import { Formula } from "../common/formulas";

interface RankConstructionProps {
  ranks: IBaseRankExplanation["ranks"];
  sequence: IBaseRankExplanation["sequence"];
}

export function RankConstruction({ ranks, sequence }: RankConstructionProps) {
  const rankExplanations: string[] = bulidRankFromExceptionalitySequence(
    ranks,
    sequence
  );
  return (
    <ul className="ml-8 space-y-6">
      {rankExplanations.map((explanation, index) => (
        <li key={index}>
          <Formula formula={explanation} />
        </li>
      ))}
    </ul>
  );
}

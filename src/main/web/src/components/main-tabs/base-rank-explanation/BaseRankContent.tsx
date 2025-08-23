import { getUniqueRankings } from "@/lib/utils/unique-rankings";
import { QueryInputContainer } from "../common/query-input";
import { RankingTable, SequenceTable } from "../tables/ranking-table";
import { PartitioningProcedure } from "./PartitioningProcedure";
import { IBaseRankExplanation } from "@/lib/models";
import { RankConstruction } from "./RankConstruction";

interface BaseRankContentProps {
  baseRankExplanation: IBaseRankExplanation;
}

function BaseRankContent({ baseRankExplanation }: BaseRankContentProps) {
  return (
    <div>
      <QueryInputContainer
        knowledgeBase={baseRankExplanation.knowledgeBase}
        queryFormula=""
        queryFormulaHidden
      />
      <div className="my-6 space-y-6">
        <PartitioningProcedure sequence={baseRankExplanation.sequence} />
        <p>The partitioning generates the following results:</p>
        <SequenceTable
          ranking={getUniqueRankings(baseRankExplanation.sequence)}
        />
        <p>The ranking is constructed as follows:</p>
        <RankConstruction
          ranks={baseRankExplanation.ranks}
          sequence={baseRankExplanation.sequence}
        />
        <p>This results in the following ranking:</p>
        <RankingTable ranking={baseRankExplanation.ranks} />
      </div>
    </div>
  );
}

export { BaseRankContent };

import { getUniqueRankings } from "@/lib/utils/unique-rankings";
import { QueryInputContainer } from "../common/query-input";
import { RankingTable, SequenceTable } from "../tables/ranking-table";
import { PartitioningProcedure } from "./PartitioningProcedure";
import { IBaseRankExplanation } from "@/lib/models";
import { RankConstruction } from "./RankConstruction";
import { Formula } from "../common/formulas";

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
        <p>From the procedure described above, we generate the following final exceptionality sequence for <Formula formula="\mathcal{K}" />:</p>
        <SequenceTable
          symbol="E"
          ranking={getUniqueRankings(baseRankExplanation.sequence)}
        />
        <p>From the above exceptionality sequence, the <i>BaseRank algorithm</i> generates the final ranking of statements in <Formula formula="\mathcal{K}" /> as follows:</p>
        <RankConstruction
          ranks={baseRankExplanation.ranks} 
          sequence={baseRankExplanation.sequence}
        />
        <p>The following is the base rank for <Formula formula="\mathcal{K}" /> produced by the <i>BaseRank algorithm</i> and is returned as the final result for use in any KLM-style defeasible entailment algorithm.</p>
        <RankingTable ranking={baseRankExplanation.ranks} />
      </div>
    </div>
  );
}

export { BaseRankContent };

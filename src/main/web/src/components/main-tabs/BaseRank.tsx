import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { ResultSkeleton } from "@/components/main-tabs/ResultSkeleton";
import { NoResults } from "./NoResults";
import { RankingTable, SequenceTable } from "./tables/ranking-table";
import { QueryInputContainer } from "./common/query-input";
import { BaseRankModel } from "@/lib/models";
import { Formula, Kb } from "./common/formulas";

interface BaseRankProps {
  isLoading: boolean;
  baseRank: BaseRankModel | null;
}

function BaseRank({ isLoading, baseRank }: BaseRankProps): JSX.Element {
  const classical = baseRank
    ? baseRank.ranking[baseRank.ranking.length - 1].formulas.filter(
        (formula) => !formula.includes("~>")
      )
    : [];
  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-lg font-bold">Base Rank</CardTitle>
        <CardDescription>
          Using base rank algorithm to create initial ranks.
        </CardDescription>
      </CardHeader>
      <CardContent>
        {!isLoading && baseRank && (
          <div>
            <QueryInputContainer
              knowledgeBase={baseRank.knowledgeBase}
              queryFormula={""}
              queryFormulaHidden
            />
            <div className="my-6 space-y-3">
              <p>
                Let <Formula formula="\mathcal{K}_C" /> be the knowledge base of
                all classical statements. Then,{" "}
              </p>
              <div className="text-center">
                <Kb name="\mathcal{K}_C" formulas={classical} set />
              </div>
              <div>
                <p>
                  The exceptionality sequence <Formula formula="*" /> for
                  knowledge base <Formula formula="\mathcal{K}" /> is given by:
                </p>
                <ul className="ml-8 list-disc ">
                  <li>
                    <Formula formula="*_0^\mathcal{K}=\{\alpha\vsim\beta\in\mathcal{K}\}" />{" "}
                    and
                  </li>
                  <li>
                    <Formula formula="*_{i+1}^\mathcal{K}=\{\alpha\vsim\beta \in *_{i}^\mathcal{K} \mid \mathcal{K}_C\cup *_{i}^\mathcal{K} \models\neg\alpha\}" />
                  </li>
                  <li>
                    for <Formula formula="0\leq i < n" />, where{" "}
                    <Formula formula="n" /> is the smallest integer such that{" "}
                    <Formula formula="*_n^\mathcal{K}=*_{n+1}^\mathcal{K}" />.
                  </li>
                  <li>
                    Final element <Formula formula="*_n^\mathcal{K}" /> is
                    usually denoted as{" "}
                    <Formula formula="*_\infty^\mathcal{K}" />. (It is unique.)
                  </li>
                </ul>
                <SequenceTable ranking={baseRank.sequence} />
              </div>
              <div>
                <p>
                  The initial ranks for knowledge base{" "}
                  <Formula formula="\mathcal{K}" /> are defined by:
                </p>
                <ul className="ml-8 list-disc ">
                  <li>
                    Finite rank:{" "}
                    <Formula formula="R_i = *_i^\mathcal{K}\setminus *_{i+1}^\mathcal{K}" />{" "}
                    <span className="ml-4">
                      (for <Formula formula="0\leq i < n" />)
                    </span>
                  </li>
                  <li>
                    Infinite rank:{" "}
                    <Formula formula="R_\infty = \mathcal{K}_C \cup *_\infty^\mathcal{K}" />
                  </li>
                </ul>
                <RankingTable ranking={baseRank.ranking} />
              </div>
            </div>
          </div>
        )}
        {isLoading && <ResultSkeleton />}
        {!isLoading && !baseRank && <NoResults />}
      </CardContent>
    </Card>
  );
}

export { BaseRank };

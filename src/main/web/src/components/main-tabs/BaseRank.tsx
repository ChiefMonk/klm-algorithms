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
import {  QueryInputContainerAgain } from "./common/query-input";
import { BaseRankModel, EntailmentType } from "@/lib/models";
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
        <CardTitle className="text-2xl font-bold">
          The Base Rank
        </CardTitle>
        <CardDescription className="text-base text-medium">
        With respect to a given defeasible knowledge base  <Formula formula="\mathcal{K}" />, the <b>base rank</b>, <Formula formula="br_{\mathcal{K}}(\alpha)" />, of a formula <Formula formula="\alpha \in \mathcal{L}" /> is the smallest index <i>r</i> such that <Formula formula="\alpha" />  is not exceptional in <Formula formula="\mathcal{E}^{\mathcal{K}}_{r}" />.
        The base rank of a defeasible implication with respect to <Formula formula="\mathcal{K}" />, <Formula formula="br_{\mathcal{K}}(\alpha ~> \beta)" />, is simply the base rank of its antecedent, <Formula formula="br_{\mathcal{K}}(\alpha)" />.  
        The intuition behind this ranking is that a lower rank indicates a higher degree of defeasibility.
        The <b>BaseRank</b> algorithm is used to determine the base ranking of statements in <Formula formula="\mathcal{K}" />. The procedure below describes how the algorithm determines the final base ranking of statements in <Formula formula="\mathcal{K}" /> and returns this ranking for use in KLM-style defeasible entailment, given a defeasible query formula.
        </CardDescription>      
      </CardHeader>
      <CardContent>
        {!isLoading && baseRank && (
          <div>
            <QueryInputContainerAgain
              type={EntailmentType.Unknown}
              knowledgeBase={baseRank.knowledgeBase}
              queryFormula={""}
              queryFormulaHidden
            />
            <div className="my-6 space-y-3">
              <p>
                If <Formula formula="\mathcal{K}_C" /> is the knowledge base of
                all classical statements in <Formula formula="\mathcal{K}" />,
                then:{" "}
              </p>
              <div className="text-left ml-12">
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

import {
  EntailmentModel,
  EntailmentType,
  LexicalEntailmentModel,
  Ranking,
} from "@/lib/models";
import { AllRanks, AllRanksEntail, EntailResult, EntailResultPrime, Formula } from "./formulas";
import { RankingTable } from "../tables/ranking-table";
import { RefinedRankingTable } from "../tables/refined-ranking";

interface RankCheckProps {
  value: Ranking;
  array: Ranking[];
  entailment: EntailmentModel;
}

function RankCheck({
  value: { rankNumber },
  array,
  entailment,
}: RankCheckProps) {
  const { baseRanking, type, negation, removedRanking, remainingRanks } =
    entailment;

  let showNotEntailed: boolean = false;

  if (type == EntailmentType.RationalClosure) {
    showNotEntailed = 
      rankNumber == removedRanking.length - 1 && remainingRanks.length > 1;
  }

  if (type == EntailmentType.LexicographicClosure) {
    const { weakenedRanking } = entailment as LexicalEntailmentModel;
    showNotEntailed =
      weakenedRanking.length > 0 &&
      rankNumber === weakenedRanking[weakenedRanking.length - 1].rankNumber &&
      remainingRanks.length > 1;
  }

  if (type == EntailmentType.BasicRelevantClosure) {
    showNotEntailed =
      rankNumber == removedRanking.length - 1 && remainingRanks.length > 1;
  }

  if (type == EntailmentType.MinimalRelevantClosure) {
    showNotEntailed =
      rankNumber == removedRanking.length - 1 && remainingRanks.length > 1;
  }

  const entireRankRemoved = !!removedRanking.find(
    (rank) => rank.rankNumber == rankNumber
  );

  return (
    <div className="space-y-4">
      {rankNumber === 1 && rankNumber < array.length - 1 && (
        <div>
          <Formula formula="\vdots" />
        </div>
      )}
      {(rankNumber == 0 || rankNumber === array.length - 1) && (
        <div className="my-4">
          <div>
            <AllRanksEntail
              start={rankNumber}
              end={baseRanking.length - 1}
              formula={negation}
            />
          </div>
          <div>
            <p>
              {type == EntailmentType.RationalClosure ? "Remove " : "Refine "}
              rank <Formula formula={`R_{${rankNumber}}`} />
            </p>
            <p>
              {type == EntailmentType.BasicRelevantClosure ? "Remove " : "Refine "}
              rank <Formula formula={`R_{${rankNumber}}`} />
            </p>
            <p>
              {type == EntailmentType.MinimalRelevantClosure ? "Remove " : "Refine "}
              rank <Formula formula={`R_{${rankNumber}}`} />
            </p>
            {type == EntailmentType.LexicographicClosure && (
              <div className="my-4">
                <div>
                  <RefinedRankingTable
                    ranks={(
                      entailment as LexicalEntailmentModel
                    ).weakenedRanking.filter(
                      (rank) => rank.rankNumber == rankNumber
                    )}
                  />
                </div>
                {entireRankRemoved && (
                  <>
                    <p>
                      <Formula formula={`\\forall R_{${rankNumber}},\\;`} />
                      <AllRanksEntail
                        start={rankNumber}
                        end={baseRanking.length - 1}
                        formula={negation}
                      />
                    </p>
                    <p>
                      Remove rank <Formula formula={`R_{${rankNumber}}`} />
                    </p>
                  </>
                )}
                {!entireRankRemoved && (
                  <>
                    <p>
                      <Formula formula={`\\exists R_{${rankNumber}},\\;`} />
                      <AllRanksEntail
                        start={rankNumber}
                        end={baseRanking.length - 1}
                        formula={negation}
                        entailed={false}
                      />
                    </p>
                  </>
                )}
              </div>
            )}
          </div>
          {showNotEntailed && (
            <div className="my-8">
              <AllRanksEntail
                start={remainingRanks[0].rankNumber}
                end={entailment.baseRanking.length - 1}
                formula={negation}
                entailed={false}
              />
            </div>
          )}
        </div>
      )}
    </div>
  );
}

interface EntailmentCheckProps {
  entailment: EntailmentModel;
}

function EntailmentCheck({
  entailment: { baseRanking, remainingRanks, removedRanking, queryFormula, entailed },
}: EntailmentCheckProps) {
  const start = baseRanking.length - remainingRanks.length;
  const end = baseRanking.length - 1;
  const classical = queryFormula.replaceAll("~>", "=>");
  const classicalRemaining = remainingRanks.flatMap(rank => rank.formulas).join(", ").replaceAll("~>", "=>");
  const classicalRemoved = removedRanking.flatMap(rank => rank.formulas).join(", ").replaceAll("~>", "=>");


  return (
    <div className="space-y-4">
      <p>
        We now check if the remaining ranked materialised statements <Formula formula={"\\mathcal{\\overrightarrow{K^\\prime}}"} /> {" "}={" "} <AllRanks start={start} end={end} />{" "} <Formula formula={"\\setminus"} /> <Formula formula="\{" /><Formula formula={classicalRemoved} /><Formula formula="\}" />
        {" "} entails the query <Formula formula={classical} />.
      </p>
      <p>
        Thats is, does <Formula formula={"\\mathcal{\\overrightarrow{K^\\prime}}"} /> {" "}={" "} <Formula formula="\{" /><Formula formula={classicalRemaining} /><Formula formula="\}" /> <Formula formula={"\\models"} />  <Formula formula={classical} />?
      </p>
      <p>
        If follows that {" "} <EntailResultPrime formula={queryFormula} entailed={entailed} />.
      </p>
      <p>
       Therefore, we conclude that the knowledge base <EntailResult formula={queryFormula} entailed={entailed} />. 
      </p>
    </div>
  );
}

type ExplanationProps = {
  entailment: EntailmentModel;
  className?: string;
};

export function Explanation({ entailment, className }: ExplanationProps) {
  const weakenedRanking =
    entailment.type === EntailmentType.LexicographicClosure
      ? (entailment as LexicalEntailmentModel).weakenedRanking
      : null;

  let refinedRanks: Ranking[] | null = null;
  if (weakenedRanking) {
    refinedRanks = [];
    for (let i = 0; i < weakenedRanking.length; i++) {
      const exists = refinedRanks.find(
        (rank) => rank.rankNumber == weakenedRanking[i].rankNumber
      );
      if (!exists) {
        refinedRanks.push(weakenedRanking[i]);
      }
    }
  }

  return (
    <div className={className}>

      <p>
        Check whether the ranks entail <Formula formula={entailment.negation} />
        . If they do,{" "}
        {entailment.type == EntailmentType.RationalClosure ? "remove" : "refine"}{" "}
        the lowest rank finite <Formula formula="R_i" />. If they don't we stop
        the process of{" "}
        {entailment.type == EntailmentType.RationalClosure
          ? "removing"
          : "refining"}{" "}
        ranks.
      </p>

      <div className="text-center">
        {entailment.type === EntailmentType.RationalClosure &&
          entailment.removedRanking.map((value, index, array) => (
            <RankCheck
              key={index}
              value={value}
              array={array}
              entailment={entailment}
            />
          ))}
        {refinedRanks &&
          refinedRanks.map((value, index, array) => (
            <RankCheck
              key={index}
              value={value}
              array={array}
              entailment={entailment}
            />
          ))}
        {entailment.baseRanking.length > 1 &&
          entailment.remainingRanks.length === 1 && (
            <p>All finite ranks are removed.</p>
          )}
        {entailment.baseRanking.length == 1 && (
          <p className="text-sm text-muted-foreground">
            No finite ranks to remove.
          </p>
        )}
      </div>
      <div>
        <p className="font-medium">The Deciding Knowledge Base <Formula formula={"\\mathcal{D} \\subseteq \\mathcal{K}"} /></p>
        <RankingTable ranking={entailment.remainingRanks} />
      </div>
      <EntailmentCheck entailment={entailment} />
    </div>
  );
}

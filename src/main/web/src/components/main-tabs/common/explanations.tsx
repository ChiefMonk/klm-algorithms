import {
  EntailmentModel,
  EntailmentType,
  LexicalEntailmentModel,
  Ranking,
} from "@/lib/models";
import { AllRanks, AllRanksEntail, EntailResult, Formula } from "./formulas";
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

  if (type == EntailmentType.RationlClosure) {
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
              {type == EntailmentType.RationlClosure ? "Remove " : "Refine "}
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
  entailment: { baseRanking, remainingRanks, queryFormula, entailed },
}: EntailmentCheckProps) {
  const start = baseRanking.length - remainingRanks.length;
  const end = baseRanking.length - 1;
  const classical = queryFormula.replaceAll("~>", "=>");

  return (
    <div className="space-y-4">
      <p>
        Now we check if the final ranks <AllRanks start={start} end={end} />{" "}
        entail the formula <Formula formula={classical} />.
      </p>
      <p>
        If follows that{" "}
        <AllRanksEntail
          start={start}
          end={end}
          formula={classical}
          entailed={entailed}
        />
        .
      </p>
      <p>
        Therefore <EntailResult formula={queryFormula} entailed={entailed} />.
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
        {entailment.type == EntailmentType.RationlClosure ? "remove" : "refine"}{" "}
        the lowest rank finite <Formula formula="R_i" />. If they don't we stop
        the process of{" "}
        {entailment.type == EntailmentType.RationlClosure
          ? "removing"
          : "refining"}{" "}
        ranks.
      </p>
      <div className="text-center">
        {entailment.type === EntailmentType.RationlClosure &&
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
        <p className="font-medium">Final ranks</p>
        <RankingTable ranking={entailment.remainingRanks} />
      </div>
      <EntailmentCheck entailment={entailment} />
    </div>
  );
}

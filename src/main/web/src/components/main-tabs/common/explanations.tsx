import {
  EntailmentModel,
  EntailmentType,
  IRationalClosureExplanation,
  LexicalEntailmentModel,
  Ranking,
} from "@/lib/models";
import { AllRanksEntail, EntailResult, Formula } from "./formulas";
import { RankingTable } from "../tables/ranking-table";
import { RefinedRankingTable } from "../tables/refined-ranking";
import { RankingTableWithout } from "../tables/ranking-table";
import { Kb } from "./formulas";
import {
  buildRankCheck,
  buildRankUnion,
} from "@/lib/build-formula/rational-closure";


interface RankCheckProps {
  value: Ranking;
  array: Ranking[];
  entailment: EntailmentModel;
}

const inferenceOperatorMap: Record<EntailmentType, string> = {
  [EntailmentType.Unknown]: "Unknown",
  [EntailmentType.RationalClosure]: "Rational Closure",
  [EntailmentType.LexicographicClosure]: "Lexicographic Closure",
  [EntailmentType.BasicRelevantClosure]: "Basic Relevant Closure",
  [EntailmentType.MinimalRelevantClosure]: "Minimal Relevant Closure",
};

const inferenceOperatorRelevant: Record<EntailmentType, boolean> = {
  [EntailmentType.Unknown]: false,
  [EntailmentType.RationalClosure]: false,
  [EntailmentType.LexicographicClosure]: false,
  [EntailmentType.BasicRelevantClosure]: true,
  [EntailmentType.MinimalRelevantClosure]: true,
};

type ExplanationProps = {
  entailment: EntailmentModel;
  className?: string;
};


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

function BaseRankingCheck({
  entailment: { type, baseRanking, queryFormula },
}: EntailmentCheckProps) {

  const inferenceOp = inferenceOperatorMap[type];
  return (
    <div className="space-y-4">
      <p className="mb-3">
        <ul className="list-disc list-inside">
          <li> The <i>{inferenceOp}</i> defeasible entailment algorithm starts with the base rankings of statements in <Formula formula="\mathcal{K}" />, constructed by the <i>BaseRank</i> algorithm.</li>
          <li>Note that the query formula <Formula formula={queryFormula} /> is not used in the <i>BaseRank</i> algorithm.</li>
          <li> The <i>BaseRank</i> algorithm takes the defeasible knowledge base <Formula formula="\mathcal{K}" /> as input and returns the following ranking of statements based on how exceptional they are relative to one another.</li>
        </ul>
      </p>

      <RankingTableWithout
        ranking={baseRanking}
      />
    </div>
  );
}

function RelevancePartitionCheck({
  entailment: { type, negation, relevantKnowledgeBase, relevantJustification, relevantRanking, irrelevantRanking, queryFormula },
}: EntailmentCheckProps) {

  console.log(type);

  return (
    <div className="space-y-4">
      <p className="mb-3">
        <ul className="list-disc list-inside">
          <li>Determination of the <i>relevance</i> with respect to <Formula formula="\mathcal{K}" /> and the query <Formula formula={`${queryFormula}`} /> involves locating the statements within <Formula formula="\mathcal{K}" /> responsible for the inconsistency with the query antecedent.</li>
          <li>The set <Formula formula="\mathcal{R}^{+}" /> of the <i>relevant partition</i> is minimal subset of <Formula formula="\mathcal{K}" /> <i>relevant</i> for concluding <Formula formula={`${negation}`} />. In literature and the dissertation, this is simply denoted as <Formula formula="\mathcal{R}" />.</li>
          <li>The set <Formula formula="\mathcal{R}^{-}" /> of the <i>relevant partition</i> is the subset of <Formula formula="\mathcal{K}" /> <i> not relevant</i> for concluding <Formula formula={`${negation}`} />. Therefore, <Formula formula="\mathcal{R}^{-}" /> is the complement of <Formula formula="\mathcal{R}^{+}" />.</li>
          <li>The process of determining <Formula formula="\mathcal{R}^{+}" /> starts by first finding the minimal sets of statements responsible for <Formula formula={`\\overrightarrow{\\mathcal{K}} \\models ${negation}`} />.</li>
          <li>The set responsible for <Formula formula={`\\overrightarrow{\\mathcal{K}} \\models ${negation}`} /> is <Formula formula="\{" /><Formula formula={relevantKnowledgeBase.join(", ")} /><Formula formula="\}" /> .</li>
          <li>The Justification sets for <Formula formula={`\\overrightarrow{\\mathcal{K}} \\models ${negation}`} /> from the entailment sub knowledge base are:</li>
        </ul>

        {relevantJustification.map((just, index) => (
          <div key={index} style={{ marginLeft: '3rem' }}>
            <Kb
              formulas={just}
              name={`\\mathcal{J}^{+}_{${index + 1}}`}
              set
            />
          </div>
        ))}
      </p>

      <ul className="list-disc list-inside">
      {type == EntailmentType.BasicRelevantClosure ? (
          <li> The <strong>relevant</strong> set is the union of statements in all justification sets above, minus those assigned to <Formula formula="\mathcal{R}_{\infty}" />.</li>
        ) : (

          <li> For each justification set above, we pick only the statements in the lowest rank, minus those assigned to <Formula formula="\mathcal{R}_{\infty}" />. The <strong>relevant</strong> set, <Formula formula="\mathcal{R}^{+}" />, is the union of these statements.</li>
        )}
        <li>The relevant set of statements, <Formula formula="\mathcal{R}^{+}" />, is shown below, grouped by rank.</li>
       
      </ul>
      <RankingTableWithout
        ranking={relevantRanking}
      />

      <ul className="list-disc list-inside">      
        <li> The <strong>irrelevant</strong> set is the set of all statements in <Formula formula="\mathcal{K}" />, including those assigned to <Formula formula="\mathcal{R}_{\infty}" />, minus those in <Formula formula="\mathcal{R}^{+}" />.</li>
        <li>The <strong>irrelevant</strong> set of statements, <Formula formula="\mathcal{R}^{-}" />, is shown below, grouped by rank.</li>
      </ul>
      <RankingTableWithout
        ranking={irrelevantRanking}
      />
    </div>
  );
}


function LexicographicPowersetCheck({
  entailment: { powersetRanking, negation, queryFormula, consistentRank },
}: EntailmentCheckProps) {

  return (
    <div className="space-y-4">
      <p className="mb-3">
        <ul className="list-disc list-inside">
          <li> Subsets, <Formula formula="\mathcal{S}_{i}" />, are ordered first by descending cardinality, with elements inside each subset arranged by decreasing rank. </li>
          <li> Subsets of equal size are then compared lexicographically by their rank sequences, yielding a precise and deterministic ordering. </li>
          <li> Note that since all the statements  assigned to <Formula formula="\mathcal{R}_{\infty}" /> are never discarded, they are included in all subsets <Formula formula="\mathcal{S}_{1}" /> to <Formula formula={`\\mathcal{S}_{${powersetRanking.length}}`} /> respectively. </li>
        </ul>
      </p>
      <RankingTableWithout
        symbol="S"
        ranking={powersetRanking}
        sortOrder="asc"
      />
      <p className="mb-3">
        <ul className="list-disc list-inside">
          <li>The negation of the query antecedent, <Formula formula={negation} />, is iteratively evaluated against each sub-knowledge-base, <Formula formula="\mathcal{S}_{1}" /> to <Formula formula={`\\mathcal{S}_{${powersetRanking.length}}`} />, for consistency in parallel. That is, we check if each subset <Formula formula={`\\overrightarrow{\\mathcal{S}_{i}} \\models ${negation}`} />.</li>
          <li>All the subsets consistent with the query antecedent (<Formula formula={`\\overrightarrow{\\mathcal{S}_{i}} \\not \\models ${negation}`} />) are added to the result set for the final entailment checking. We denote such subsets as  <Formula formula="\mathcal{S}_{c}" />.</li>
          <li>For each consistent sub-knowledge base, <Formula formula="\mathcal{S}_{c}" />, starting with those with lexicographically highest cardinality, we iteratively check if <Formula formula={`\\overrightarrow{\\mathcal{S}_{c}} \\models ${queryFormula.replaceAll("~>", "=>")}`} />?</li>
          {consistentRank > 0 ? (
            <p>
              <li>The knowledge base <Formula formula="\mathcal{K}" /> and defeasible query <Formula formula={queryFormula} /> yielded that the highest cardinality consistent sub-knowledge base is <Formula formula={`\\mathcal{S}_{${consistentRank}}`} />.</li>
              <li><Formula formula={`\\mathcal{S}_{${consistentRank}}`} />  {" "}={" "} <Formula formula="\{" /><Formula formula={powersetRanking
                .find(r => r.rankNumber === consistentRank)
                ?.formulas
                .join(", ")
                .replaceAll("~>", "=>")} /><Formula formula="\}" /> .</li>
              <li>Therefore, all the statements in <Formula formula="\mathcal{K}" /> but not in <Formula formula={`\\mathcal{S}_{${consistentRank}}`} /> are excluded from entailment checking and determination of <Formula formula={`\\overrightarrow{\\mathcal{K}} \\models ${queryFormula.replaceAll("~>", "=>")}`} />?</li>
            </p>
          ) : (
            <p>
              <li>The knowledge base <Formula formula="\mathcal{K}" /> and defeasible query <Formula formula={queryFormula} /> yielded no consistent sub-knowledge base, <Formula formula="\mathcal{S}_{c}" />.</li>
              <li>Therefore, only the statements in <Formula formula={`\\mathcal{R}_{\\infty}`} /> are considered for entailment checking and determining if <Formula formula={`\\overrightarrow{\\mathcal{K}_{\\infty}} \\models ${queryFormula.replaceAll("~>", "=>")}`} />?</li>
            </p>
          )}
        </ul>
      </p>
    </div>
  );
}


function DiscardedRankingCheck({
  entailment: { type, removedRanking, queryFormula },
}: EntailmentCheckProps) {

  const inferenceOp = inferenceOperatorMap[type];
  const isRelevant = inferenceOperatorRelevant[type];
  const relevantSet = isRelevant ? "\\mathcal{R}^{-}" : "\\mathcal{R}_{\\infty}";

  return (
    <div className="space-y-4">
      <p className="mb-3">
        {type == EntailmentType.RationalClosure ? (
          <ul className="list-disc list-inside">
            <li>These are ranks (if any) containing a stamement or statements causing the inconsistency with the query antecedent, <Formula formula={queryFormula.split("~>")[0].replaceAll("(", "").replaceAll(")", "")} />.</li>
            <li>If all successive union of finite ranks with <Formula formula={`\\mathcal{K}_{\\infty}`} /> are inconsistent with the query antecedent, then all finite rank statements would be discarded, except those assigned to <Formula formula={`\\mathcal{K}_{\\infty}`} />.</li>
            <li>The statements in these ranks are therefore discarded by the <i>{inferenceOp}</i> defeasible entailment algorithm and are excluded from entailment checking and determining if <Formula formula={`\\mathcal{K} \\vapprox ${queryFormula}`} />?</li>
            <li>For easy reference, we denoted this set as <Formula formula={`\\mathcal{K}_{d}`} />, and will be referred to as the <i>discarded rank staments</i>.</li>
          </ul>
        ) : (
          <ul className="list-disc list-inside">
            <li>These are the statements in <Formula formula="\mathcal{K}" /> (if any) causing the inconsistency with the query antecedent, <Formula formula={queryFormula.split("~>")[0].replaceAll("(", "").replaceAll(")", "")} />.</li>
            <li>If the whole lexicographic powerset (all the sub-knowledge bases above) is inconsistent with the query antecedent, then this is the set of all statements in  <Formula formula="\mathcal{K}" /> minus those assigned to <Formula formula={relevantSet} />.</li>
            <li>These are therefore discarded by the <i>{inferenceOp}</i> defeasible entailment algorithm and are excluded from entailment checking and determining if <Formula formula={`\\mathcal{K} \\vapprox ${queryFormula}`} />?</li>
            <li>For easy reference, we denoted this set as <Formula formula={`\\mathcal{K}_{d}`} />, and will be referred to as the <i>discarded sub knowledge base</i>.</li>
          </ul>
        )}
      </p>

      <RankingTableWithout
        ranking={removedRanking}
      />
    </div>
  );
}

function RemainingRankingCheck({
  entailment: { type, remainingRanking, queryFormula },
}: EntailmentCheckProps) {

  const inferenceOp = inferenceOperatorMap[type];
  return (
    <div className="space-y-4">
      <p className="mb-3">
        {type == EntailmentType.RationalClosure ? (
          <ul className="list-disc list-inside">
            <li>These are the remaining ranks (if any) with a union of statements consistency with the query antecedent, <Formula formula={queryFormula.split("~>")[0].replaceAll("(", "").replaceAll(")", "")} />.</li>
            <li>If all successive union of finite ranks with <Formula formula={`\\mathcal{K}_{\\infty}`} /> are inconsistent with the query antecedent, then only statements assigned to <Formula formula={`\\mathcal{R}_{\\infty}`} /> remain.</li>
            <li>These are therefore employed by the <i>{inferenceOp}</i> defeasible entailment algorithm for entailment checking and determining if <Formula formula={`\\mathcal{K} \\vapprox ${queryFormula}`} />?</li>
            <li>For easy reference, we denoted this set as <Formula formula={`\\mathcal{K}_{r}`} />, and will be referred to as the <i>remaining rank statements</i>.</li>
            <li>If <Formula formula={`\\mathcal{K}_{r}`} /> entails the query <Formula formula={queryFormula} />, then <Formula formula={`\\mathcal{K}_{r}`} /> is the <i>deciding knowledge base</i> <Formula formula="\mathcal{D}" />, to be used for justification-based explanation.</li>
          </ul>
        ) : (
          <ul className="list-disc list-inside">
            <li>These are the remaining statements in <Formula formula="\mathcal{K}" /> (if any) consistency with the query antecedent, <Formula formula={queryFormula.split("~>")[0].replaceAll("(", "").replaceAll(")", "")} />.</li>
            <li>If the whole lexicographic powerset (all the sub-knowledge bases above) is inconsistent with the query antecedent, then this set consists of only the statements assigned to <Formula formula={`\\mathcal{R}_{\\infty}`} />.</li>
            <li>They are therefore employed by the <i>{inferenceOp}</i> defeasible entailment algorithm for entailment checking and determining if <Formula formula={`\\mathcal{K} \\vapprox ${queryFormula}`} />?</li>
            <li>For easy reference, we denoted this set as <Formula formula={`\\mathcal{K}_{r}`} />, and will be referred to as the <i>remaining sub knowledge base</i>.</li>
            <li>If <Formula formula={`\\mathcal{K}_{r}`} /> entails the query <Formula formula={queryFormula} />, then <Formula formula={`\\mathcal{K}_{r}`} /> is the <i>deciding knowledge base</i> <Formula formula="\mathcal{D}" />, to be used for justification-based explanation.</li>
          </ul>
        )}
      </p>
      <RankingTableWithout
        ranking={remainingRanking}
      />
    </div>
  );
}


function EntailmentCheck({
  entailment: { type, baseRanking, remainingRanks, removedRanking, queryFormula, entailed },
}: EntailmentCheckProps) {
  const classical = queryFormula.replaceAll("~>", "=>");
  const classicalAll = baseRanking.flatMap(rank => rank.formulas).join(", ").replaceAll("~>", "=>");
  const classicalRemaining = remainingRanks.flatMap(rank => rank.formulas).join(", ").replaceAll("~>", "=>");
  const classicalRemoved = removedRanking.flatMap(rank => rank.formulas).join(", ").replaceAll("~>", "=>");
  const decidingKb = remainingRanks.flatMap(rank => rank.formulas).join(", ");
  const inferenceOp = inferenceOperatorMap[type];


  return (
    <div className="space-y-4">
      <p className="mb-3">
        <ul className="list-disc list-inside">
          <li>
            We now check if the query <Formula formula={classical} /> is entailed by <Formula formula={"\\overrightarrow{\\mathcal{K}_{r}}"} /> {" "}={" "} <Formula formula={"\\overrightarrow{\\mathcal{K}}"} />  <Formula formula={"\\setminus"} /> <Formula formula={"\\overrightarrow{\\mathcal{K}_{d}}"} />?
          </li>
          <li>
            Thats is, <Formula formula={"\\overrightarrow{\\mathcal{K}_{r}}"} /> {" "}={" "} <Formula formula="\{" /><Formula formula={classicalAll} /><Formula formula="\}" /> <Formula formula={"\\setminus"} />  <Formula formula="\{" /><Formula formula={classicalRemoved} /><Formula formula="\}" />.
          </li>
          <li>
            Therefore, <Formula formula={"\\overrightarrow{\\mathcal{K}_{r}}"} /> {" "}={" "} <Formula formula="\{" /><Formula formula={classicalRemaining} /><Formula formula="\}" />.
          </li>
          <li>
            Does <Formula formula={"\\overrightarrow{\\mathcal{K}_{r}}"} /> {" "}={" "} <Formula formula="\{" /><Formula formula={classicalRemaining} /><Formula formula="\}" /> <Formula formula={"\\models"} />  <Formula formula={classical} />?
          </li>
          {entailed ? (
            <p>
              <li>
                If follows that <Formula formula={"\\overrightarrow{\\mathcal{K}_{r}} \\subseteq \\overrightarrow{\\mathcal{K}}"} /> <Formula formula={"\\models"} />  <Formula formula={classical} />.
              </li>
              <li>
                The classical entailment logic concludes that the knowledge base <EntailResult formula={queryFormula} entailed={entailed} />.
              </li>
              <li>
                Therefore, the modified and optimised <i>{inferenceOp}</i> defeasible entailment algorithm for <i>justication</i> returns <strong>true</strong> and the defeasible entailment knowledge base <Formula formula={"\\mathcal{K}_{r}"} />.
              </li>
              <li>
                Since, <EntailResult formula={queryFormula} entailed={entailed} />, the <i>deciding knowledge base</i> <Formula formula="\mathcal{D}" /> = <Formula formula={"\\mathcal{K}_{r}"} /> = <Formula formula="\{" /><Formula formula={decidingKb} /><Formula formula="\}" />.
              </li>
            </p>
          ) : (
            <p>
              <li>
                If follows that <Formula formula={"\\overrightarrow{\\mathcal{K}_{r}} \\subseteq \\overrightarrow{\\mathcal{K}}"} /> <Formula formula={"\\not \\models"} />  <Formula formula={classical} />?
              </li>
              <li>
                The classical entailment logic concludes that the knowledge base <EntailResult formula={queryFormula} entailed={entailed} />.
              </li>
              <li>
                Therefore, the modified and optimised <i>{inferenceOp}</i> defeasible entailment algorithm for <i>justication</i> returns <strong>false</strong> and an empty defeasible entailment knowledge base.
              </li>
              <li>
                Since, <EntailResult formula={queryFormula} entailed={entailed} />, the <i>deciding knowledge base</i> <Formula formula="\mathcal{D}" /> = <Formula formula="\{\}" />.
              </li>
            </p>
          )}
        </ul>
      </p>
    </div>
  );
}


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
        Check whether the ranks entail <Formula formula={entailment.negation} />. If they do,{" "}
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

function RationalClosureDetermination({
  explanation,
}: {
  explanation: IRationalClosureExplanation;
}) {
  return (
    <div className="space-y-4">
      {explanation.checks.map((check, index) => (
        <div key={index}>
          <p className="mb-3">
            Iteration {index + 1}:
          </p>
          <div className="ml-4 space-y-2">
            <p>
              {index == 0 ? (
                <>
                  We start by checking and determining if the union of staments in all ranks entail the negation of the query antecedent,{" "} <Formula formula={check.antecedentNegation} />.
                </>
              ) : (
                <>
                  We again check and determine if the union of staments in the remaining ranks entail the negation of the query antecedent,{" "} <Formula formula={check.antecedentNegation} />.
                </>
              )}
            </p>
            <p>
              <Formula formula={buildRankUnion(check.ranks)} />
            </p>
            <p className="space-x-4">
              <span>
                We determine that <Formula formula={buildRankCheck(check)} />.
              </span>
              {!check.isConsistent && (
                <span>                 
                  <br />Becase the knowledge base consisting of staments in these ranks is still incconsistent with respect to the query antecedent, <Formula formula={check.antecedentNegation.replaceAll("!", "")} />, we discard all staments in the most typical and lowest rank,{" "}
                  <Formula formula={`\\mathcal{R}_{${check.removedRank.rankNumber}}`} />.
                  <br />We proceed to the next iteration with the remaining ranks.
                </span>
              )}
              {check.isConsistent && (
                <span>                 
                  <br />Now that the knowledge base consisting of staments in these ranks is now consistent with respect to the query antecedent, <Formula formula={check.antecedentNegation.replaceAll("!", "")} />, we stop processing and break out of the loop.
                  <br />We will now proceed to determine defeasible entailment by checking if the union of statements in the remaining ranks entail the query,{" "} <Formula formula={explanation.queryFormula} />.
                </span>
              )}
            </p>
          </div>
        </div>
      ))}
    </div>
  );
}

export function EntailmentExplanation({ entailment, className }: ExplanationProps) {
  return (
    <div className={className}>
      <EntailmentCheck entailment={entailment} />
    </div>
  );
}

export function DiscardedRankingExplanation({ entailment, className }: ExplanationProps) {
  return (
    <div className={className}>
      <DiscardedRankingCheck entailment={entailment} />
    </div>
  );
}

export function RemainingRankingExplanation({ entailment, className }: ExplanationProps) {
  return (
    <div className={className}>
      <RemainingRankingCheck entailment={entailment} />
    </div>
  );
}

export function LexicographicPowersetExplanation({ entailment, className }: ExplanationProps) {
  return (
    <div className={className}>
      <LexicographicPowersetCheck entailment={entailment} />
    </div>
  );
}

export function BaseRankingExplanation({ entailment, className }: ExplanationProps) {
  return (
    <div className={className}>
      <BaseRankingCheck entailment={entailment} />
    </div>
  );
}

export function RelevancePartitionExplanation({ entailment, className }: ExplanationProps) {
  return (
    <div className={className}>
      <RelevancePartitionCheck entailment={entailment} />
    </div>
  );
}

export { RationalClosureDetermination };
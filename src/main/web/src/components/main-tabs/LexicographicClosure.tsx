import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { ResultSkeleton } from "@/components/main-tabs/ResultSkeleton";
import { NoResults } from "./NoResults";
import { RankingTableWithout } from "./tables/ranking-table";
import { QueryInputContainer } from "./common/query-input";
import { LexicalEntailmentModel } from "@/lib/models";
import { Explanation } from "./common/explanations";
import { Justification } from "./justication";
import { useReasonerContext } from "@/state/reasoner.context";
import { Formula } from "./common/formulas";

interface LexicographicClosureProps {
  isLoading: boolean;
  lexicalEntailment: LexicalEntailmentModel | null;
}

function LexicographicClosure({
  isLoading,
  lexicalEntailment,
}: LexicographicClosureProps): JSX.Element {
  const { queryType } = useReasonerContext();
  return (
    <Card className="w-full h-full">
      <CardHeader>
        <CardTitle className="text-2xl font-bold">
          Lexicographic Closure
        </CardTitle>
        <CardDescription className="text-base text-medium">
          <b>Lexicographic Closure</b> is a KLM inference mechanism for non-monotonic reasoning introduced by <b>Lehmann</b>, and the entailment algorithms implemented here build on the version proposed by <b>Casini et al</b>. The algorithm operates in two sub-phases, <b><i>BaseRank</i></b> and <b><i>LexicographicClosure</i></b> algorithms. The algorithm functions by assigning a ranking of typicality to the statements in the knowledge base.
          Lexicographic Closure can be seen as a refinement of Rational Closure, where we remove single statements instead of entire ranks when inconsistencies arise during the reasoning process, starting with the most typical information. The <i>BaseRank</i> phase determines these rankings, where statements with lower ranks represent more typical information.
        </CardDescription>
      </CardHeader>
      <CardContent>
        {!isLoading && lexicalEntailment && (
          <div>
            <h1 className="text-lg font-bold mb-2">Entailment Determination</h1>
            <QueryInputContainer
              knowledgeBase={lexicalEntailment.knowledgeBase}
              queryFormula={lexicalEntailment.queryFormula}
            />
            <div className="my-6">

              <p className="font-medium">
                Base Rank of statements in <Formula formula="\mathcal{K}" />:
              </p>
              <p className="mb-3">
                Lexicographic Closure starts with the base rankings of statements in <Formula formula="\mathcal{K}" /> constructed by the <i>BaseRank</i> algorithm.
              </p>

              <RankingTableWithout
                ranking={lexicalEntailment.baseRanking}
              />

              <p className="font-medium">
                Lexicographic powerset of statements in <Formula formula="\mathcal{K}" />:
              </p>
              <p className="mb-3">
                Subsets, <Formula formula="\mathcal{S}_{i}" />, are ordered first by descending cardinality, with elements inside each subset arranged by decreasing rank. Subsets of equal size are then compared lexicographically by their rank sequences, yielding a precise and deterministic ordering.
                Note that since all the statements  assigned to <Formula formula="\mathcal{R}_{\infty}" /> are never discarded, they are included in all subsets <Formula formula="\mathcal{S}_{1}" /> to <Formula formula={`\\mathcal{S}_{${lexicalEntailment.powersetRanking.length}}`} /> respectively.
              </p>
              <RankingTableWithout
                symbol="S"
                ranking={lexicalEntailment.powersetRanking}
                sortOrder="asc"
              />
              <p className="mb-3">
                <ul className="list-disc list-inside">
                  <li>The negation of the query antecedent, <Formula formula={lexicalEntailment.negation} />, is iteratively evaluated against each sub-knowledge-base, <Formula formula="\mathcal{S}_{1}" /> to <Formula formula={`\\mathcal{S}_{${lexicalEntailment.powersetRanking.length}}`} />, for consistency in parallel. That is, we check if each subset <Formula formula={`\\overrightarrow{\\mathcal{S}_{i}} \\models ${lexicalEntailment.negation}`} />.</li>
                  <li>All the subsets consistent with the query antecedent (<Formula formula={`\\overrightarrow{\\mathcal{S}_{i}} \\not \\models ${lexicalEntailment.negation}`} />) are added to the result set for the final entailment checking. We denote such subsets as  <Formula formula="\mathcal{S}_{c}" />.</li>
                  <li>For each consistent sub-knowledge base, <Formula formula="\mathcal{S}_{c}" />, starting with those with lexicographically highest cardinality, we iteratively check if <Formula formula={`\\overrightarrow{\\mathcal{S}_{c}} \\models ${lexicalEntailment.queryFormula.replaceAll("~>", "=>")}`} />?</li>
                  {lexicalEntailment.consistentRank > 0 ? (
                    <p>
                      <li>The knowledge base <Formula formula="\mathcal{K}" /> and defeasible query <Formula formula={lexicalEntailment.queryFormula} /> yielded that the highest cardinality consistent sub-knowledge base is <Formula formula={`\\mathcal{S}_{${lexicalEntailment.consistentRank}}`} />.</li>
                      <li><Formula formula={`\\mathcal{S}_{${lexicalEntailment.consistentRank}}`} />  {" "}={" "} <Formula formula="\{" /><Formula formula={lexicalEntailment.powersetRanking
                        .find(r => r.rankNumber === lexicalEntailment.consistentRank)
                        ?.formulas
                        .join(", ")
                        .replaceAll("~>", "=>")} /><Formula formula="\}" /> .</li>
                      <li>Therefore, all the statements in <Formula formula="\mathcal{K}" /> but not in <Formula formula={`\\mathcal{S}_{${lexicalEntailment.consistentRank}}`} /> are excluded from entailment checking and determination of <Formula formula={`\\overrightarrow{\\mathcal{K}} \\models ${lexicalEntailment.queryFormula.replaceAll("~>", "=>")}`} />?</li>
                    </p>
                  ) : (
                    <p>
                      <li>The knowledge base <Formula formula="\mathcal{K}" /> and defeasible query <Formula formula={lexicalEntailment.queryFormula} /> yielded no consistent sub-knowledge base, <Formula formula="\mathcal{S}_{c}" />.</li>
                      <li>Therefore, only the statements in <Formula formula={`\\mathcal{R}_{\\infty}`} /> are considered for entailment checking and determining if <Formula formula={`\\overrightarrow{\\mathcal{K}_{\\infty}} \\models ${lexicalEntailment.queryFormula.replaceAll("~>", "=>")}`} />?</li>
                    </p>
                  )}
                </ul>
              </p>

              <p className="h-5" />
              <p className="font-medium">Discarded statements from <Formula formula="\mathcal{K}" /> shown per rank:</p>

              <p className="mb-3">
                <ul className="list-disc list-inside">
                  <li>These are the statements in <Formula formula="\mathcal{K}" /> (if any) causing the inconsistency with the query antecedent, <Formula formula={lexicalEntailment.queryFormula.split("~>")[0].replaceAll("(", "").replaceAll(")", "")} />.</li>
                  <li>If the whole lexicographic powerset (all the sub-knowledge bases above) is inconsistent with the query antecedent, then this is the set of all statements in  <Formula formula="\mathcal{K}" /> minus those assigned to <Formula formula={`\\mathcal{R}_{\\infty}`} />.</li>
                  <li>They are therefore discarded by the Lexicographic Closure algorithm and are excluded from entailment checking and determining if <Formula formula={`\\overrightarrow{\\mathcal{K}} \\vapprox ${lexicalEntailment.queryFormula}`} />?</li>
                </ul>
              </p>

              <RankingTableWithout
                ranking={lexicalEntailment.removedRanking}
              />

              <p className="font-medium">Remaining statements from <Formula formula="\mathcal{K}" /> shown per rank:</p>
              <p className="mb-3">
                <ul className="list-disc list-inside">
                  <li>These are the remaining statements in <Formula formula="\mathcal{K}" /> (if any) consistency with the query antecedent, <Formula formula={lexicalEntailment.queryFormula.split("~>")[0].replaceAll("(", "").replaceAll(")", "")} />.</li>
                  <li>If the whole lexicographic powerset (all the sub-knowledge bases above) is inconsistent with the query antecedent, then this set consists of only the statements assigned to <Formula formula={`\\mathcal{R}_{\\infty}`} />.</li>
                  <li>They are therefore employed by the Lexicographic Closure algorithm for entailment checking and determining if <Formula formula={`\\overrightarrow{\\mathcal{K}} \\vapprox ${lexicalEntailment.queryFormula}`} />?</li>
                </ul>
              </p>
              <RankingTableWithout
                ranking={lexicalEntailment.remainingRanking}
              />

            </div>
            <Explanation
              entailment={lexicalEntailment}
              className="mb-6 space-y-4"
            />

            <Justification
              queryType={queryType}
              entailment={lexicalEntailment}
              queryFormula={lexicalEntailment.queryFormula}
              entailed={lexicalEntailment.entailed}
            />
          </div>
        )}
        {isLoading && <ResultSkeleton />}
        {!isLoading && !lexicalEntailment && <NoResults />}
      </CardContent>
    </Card>
  );
}

export { LexicographicClosure };

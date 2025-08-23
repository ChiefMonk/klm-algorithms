package uct.cs.klm.algorithms.ranking;

import org.tweetyproject.logics.pl.syntax.PlFormula;

public record SequenceElementCheck(
        int elementNumber,
        PlFormula antecedentNegation,
        PlFormula formula,
        boolean isExceptional
) {
}

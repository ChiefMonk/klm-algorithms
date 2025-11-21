package uct.cs.klm.algorithms.ranking;

import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class represents a sequence element check for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public record SequenceElementCheck(
        int elementNumber,
        PlFormula antecedentNegation,
        PlFormula formula,
        boolean isExceptional
) {
}

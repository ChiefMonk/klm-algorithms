package uct.cs.klm.algorithms.models;

import org.tweetyproject.logics.pl.syntax.PlFormula;


/**
 * An interface representing a model formula ranked.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2025-01-26
 */
public record ModelFormulaRanked (int rank , PlFormula formula){}
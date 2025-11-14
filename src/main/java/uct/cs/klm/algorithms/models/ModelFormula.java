package uct.cs.klm.algorithms.models;

import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.enums.TautologyType;

/**
 * This class represents a model formula for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

public class ModelFormula {

    private PlFormula _formula;
    private boolean _isDefeasible;
    private TautologyType _tautologyType;

    public ModelFormula(PlFormula formula) {
        _formula = formula;
        _tautologyType = TautologyType.Unknown;
    }

    public ModelFormula(PlFormula formula, TautologyType tautologyType) {
        this(formula);
        _tautologyType = tautologyType;
    }

    /**
     * Get the formula.
     *
     * @return PlFormula.
     */
    public PlFormula getFormula() {
        return _formula;
    }
    
    /**
     * Get isDefeasible.
     *
     * @return isDefeasible.
     */
    public boolean isDefeasible() {
        return _isDefeasible;
    }
    
    /**
     * Get isTop.
     *
     * @return isTop.
     */
    public boolean isTop() {
        return _tautologyType == TautologyType.Top;
    }
    
    /**
     * Get isBottom.
     *
     * @return isBottom.
     */
    public boolean isBottom() {
        return _tautologyType == TautologyType.Bottom;
    }

}

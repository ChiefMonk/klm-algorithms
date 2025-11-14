package uct.cs.klm.algorithms.models;

/**
 * This class represents a model formula validation for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public class ModelFormulaValidation<T> {
 private final boolean _isValid;
    private final String  _message;
    private final T _data;
    
    public ModelFormulaValidation(boolean isValid, T data)
    {
        _isValid = isValid;
        _message = null;
        _data = data;
    }
    
    public ModelFormulaValidation(String message)
    {
        _isValid = false;
        _data = null;
        _message = message;
    }
    
    public boolean isValid() {
        return _isValid;
    }
    
    public String getMessage() {
        return _message;
    }
    
    public T getData() {
        return _data;
    }
}

package uct.cs.klm.algorithms.models;

/**
 * This class represents a model error response for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public class ModelErrorResponse {
  public final int code;
  public final String description;
  public final String message;

  public ModelErrorResponse(int code, String description, String message) {
    this.code = code;
    this.description = description;
    this.message = message;
  }
}

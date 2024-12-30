package uct.cs.klm.algorithms.models;

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

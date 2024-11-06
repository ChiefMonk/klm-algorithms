package uct.cs.klm.algorithms.services;

public class ReasonerFactory {
  public static ReasonerService createReasoner(String type) {
    return switch (type) {
      case "rational" -> new RationalReasonerImpl();
      case "lexical" -> new LexicalReasonerImpl();
      case "relevant" -> new RationalReasonerImpl();
      default -> throw new IllegalArgumentException("Unknown reasoner: " + type);
    };
  }
}

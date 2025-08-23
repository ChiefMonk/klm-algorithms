import { useLocation } from "react-router-dom";
import { QueryType } from "@/lib/models";

export function useRouteQueryType(): QueryType {
  const location = useLocation();
  const pathname = location.pathname;

  switch (pathname) {
    case "/":
    case "/entailment":
      return QueryType.Entailment;
    case "/justification":
      return QueryType.Justification;
    case "/evaluation":
      return QueryType.Evaluation;
    default:
      return QueryType.Entailment; // Default to Entailment for unknown routes
  }
}

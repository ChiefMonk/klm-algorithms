package uct.cs.klm.algorithms.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import uct.cs.klm.algorithms.models.EvaluationModel;
import uct.cs.klm.algorithms.models.EvaluationQuery;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public interface EvaluationService {
    EvaluationModel evaluate(EvaluationQuery query);
    SimpleEntry<String, String> exportEvaluation(EvaluationModel model) throws JsonProcessingException;
    EvaluationModel importEvaluation(InputStream inputStream) throws IOException;
}

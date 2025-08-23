package uct.cs.klm.algorithms.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.javalin.json.JavalinJackson;
import uct.cs.klm.algorithms.enums.Algorithm;
import uct.cs.klm.algorithms.enums.InferenceOperator;
import uct.cs.klm.algorithms.models.EvaluationData;
import uct.cs.klm.algorithms.models.EvaluationGroup;
import uct.cs.klm.algorithms.models.EvaluationModel;
import uct.cs.klm.algorithms.models.EvaluationQuery;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EvaluationServiceImpl implements EvaluationService {
    private final ObjectMapper mapper = JavalinJackson.defaultMapper();
    private final ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();

    @Override
    public EvaluationModel evaluate(EvaluationQuery query) {
        InferenceOperator operator = query.inferenceOperator();
        List<Algorithm> selectedAlgorithms = query.algorithms();

        EvaluationGroup group1 = new EvaluationGroup(
                "Evaluation 1",
                operator,
                generateData(selectedAlgorithms, operator, 1.0)
        );

        EvaluationGroup group2 = new EvaluationGroup(
                "Evaluation 2",
                operator,
                generateData(selectedAlgorithms, operator, 1.5)
        );

        EvaluationGroup group3 = new EvaluationGroup(
                "Evaluation 3",
                operator,
                generateData(selectedAlgorithms, operator, 2.0)
        );

        return new EvaluationModel(query, List.of(group1, group2, group3));
    }

    @Override
    public AbstractMap.SimpleEntry<String, String> exportEvaluation(EvaluationModel model) throws JsonProcessingException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = now.format(formatter);
        String filename = "klm_algorithms_" + timestamp + ".json";
        String json = writer.writeValueAsString(model);
        return new AbstractMap.SimpleEntry<>(filename, json);
    }

    @Override
    public EvaluationModel importEvaluation(InputStream inputStream) throws IOException {
        return mapper.readValue(inputStream, EvaluationModel.class);
    }

    // Private methods for generating dummy data
    private List<EvaluationData> generateData(List<Algorithm> algorithms, InferenceOperator op, double multiplier) {
        List<EvaluationData> dataList = new ArrayList<>();

        dataList.add(data("50 tqs R1", op, algorithms, multiplier * 100, multiplier * 300, multiplier * 200, multiplier * 10));
        dataList.add(data("50 tqs Rn", op, algorithms, multiplier * 200, multiplier * 250, multiplier * 180, multiplier * 12));
        dataList.add(data("50 tqs α_same", op, algorithms, multiplier * 300, multiplier * 400, multiplier * 270, multiplier * 15));
        dataList.add(data("50 tqs α_dist", op, algorithms, multiplier * 150, multiplier * 280, multiplier * 210, multiplier * 9));
        dataList.add(data("50 tqs α_half", op, algorithms, multiplier * 180, multiplier * 310, multiplier * 250, multiplier * 8));

        return dataList;
    }

    private EvaluationData data(String label, InferenceOperator op, List<Algorithm> algos, double naive, double binary, double ternary, double powerset) {
        EvaluationData d = new EvaluationData(label, op);
        if (algos.contains(Algorithm.Naive)) d.addResult(Algorithm.Naive, naive);
        if (algos.contains(Algorithm.Binary)) d.addResult(Algorithm.Binary, binary);
        if (algos.contains(Algorithm.Ternary)) d.addResult(Algorithm.Ternary, ternary);
        if (algos.contains(Algorithm.PowerSet)) d.addResult(Algorithm.PowerSet, powerset);
        return d;
    }

}

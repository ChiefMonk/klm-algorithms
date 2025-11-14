package uct.cs.klm.algorithms.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.javalin.json.JavalinJackson;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.benchmarks.JmhRunner;
import uct.cs.klm.algorithms.enums.Algorithm;
import uct.cs.klm.algorithms.enums.InferenceOperator;
import uct.cs.klm.algorithms.enums.ReasonerType;
import uct.cs.klm.algorithms.models.*;
import uct.cs.klm.algorithms.ranking.BaseRankService;
import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.utils.ReasonerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EvaluationServiceImpl implements EvaluationService {

    private final ObjectMapper mapper = JavalinJackson.defaultMapper();
    private final ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();

    private final FormulaServiceImpl formulaService;
    private final KnowledgeBaseServiceImpl knowledgeBaseService;
    private final BaseRankService baseRankService;

    public EvaluationServiceImpl() {
        formulaService = new FormulaServiceImpl();
        knowledgeBaseService = new KnowledgeBaseServiceImpl();
        baseRankService = new BaseRankService();
    }

    @Override
    public EvaluationModel evaluate(EvaluationQuery query) {
        EvaluationQueryParams params = query.parameters();

        String evaluation1 = "Category 1 Test Cases";
        String evaluation2 = "Category 2 Test Cases";
        String evaluation3 = "Category 3 Test Cases";
        String evaluation4 = "Category 4 Test Cases";
  

        // Example: Overriding query params for one of the evaluation
        EvaluationQueryParams params1 = new EvaluationQueryParams(
                100, // Overrides params.numberOfRanks()
                params.distribution(),
                params.numberOfDefeasibleImplications(),
                params.simpleDiOnly(),
                params.reuseConsequent(),
                params.antecedentComplexity(),
                params.consequentComplexity(),
                params.connective(),
                params.characterSet(),
                params.generator()
        );

        EvaluationGroup group1 = evaluationGroup(evaluation1, new EvaluationQuery(params1, query.inferenceOperator(), query.algorithms()));

        EvaluationQueryParams params2 = new EvaluationQueryParams(
                params.numberOfRanks(),
                params.distribution(),
                params.numberOfDefeasibleImplications(),
                false, // Overrides params.simpleDiOnly()
                params.reuseConsequent(),
                params.antecedentComplexity(),
                params.consequentComplexity(),
                params.connective(),
                params.characterSet(),
                params.generator()
        );

        EvaluationGroup group2 = evaluationGroup(evaluation2, new EvaluationQuery(params2, query.inferenceOperator(), query.algorithms()));

        EvaluationQueryParams params3 = new EvaluationQueryParams(
                params.numberOfRanks(),
                params.distribution(),
                params.numberOfDefeasibleImplications(),
                false, // Overrides params.simpleDiOnly()
                params.reuseConsequent(),
                params.antecedentComplexity(),
                params.consequentComplexity(),
                params.connective(),
                params.characterSet(),
                params.generator()
        );

        EvaluationGroup group3 = evaluationGroup(evaluation3, new EvaluationQuery(params3, query.inferenceOperator(), query.algorithms()));

        EvaluationQueryParams params4 = new EvaluationQueryParams(
                params.numberOfRanks(),
                params.distribution(),
                params.numberOfDefeasibleImplications(),
                false, // Overrides params.simpleDiOnly()
                params.reuseConsequent(),
                params.antecedentComplexity(),
                params.consequentComplexity(),
                params.connective(),
                params.characterSet(),
                params.generator()
        );

        EvaluationGroup group4 = evaluationGroup(evaluation4, new EvaluationQuery(params4, query.inferenceOperator(), query.algorithms()));

        return new EvaluationModel(query, List.of(group1, group2, group3, group4));
       
    }

    public EvaluationGroup evaluationGroup(String evaluationName, EvaluationQuery query) {
        ReasonerType reasonerType = switch (query.inferenceOperator()) {
            case RationalClosure ->
                ReasonerType.RationalClosure;
            case LexicographicClosure ->
                ReasonerType.LexicographicClosure;
            case BasicRelevantClosure ->
                ReasonerType.BasicRelevantClosure;
            case MinimalRelevantClosure ->
                ReasonerType.MinimalRelevantClosure;
        };

        IReasonerService reasoner = ReasonerFactory.createEntailment(reasonerType);

        InferenceOperator operator = query.inferenceOperator();
        List<Algorithm> selectedAlgorithms = query.algorithms();
        List<EvaluationData> dataList = new ArrayList<>();

        KnowledgeBase knowledgeBase = knowledgeBaseService.getKnowledgeBase();
        ModelBaseRank baseRank = baseRankService.construct(knowledgeBase);

        PlFormula formula = formulaService.getQueryFormula();

        for (String testQuery : List.of("First Rank", "Last Rank", "Random Rank", "Same Antecedents", "Different Antecedents", "Mixed Antecedents")) {
            EvaluationData data = new EvaluationData(testQuery, operator);

            for (Algorithm algo : selectedAlgorithms) {
                long start = System.nanoTime();
                reasoner.getEntailment(baseRank, formula);
                double timeMs = Math.round(((System.nanoTime() - start) / 1_000_000.0) * 100.0) / 100.0;
                data.addResult(algo, timeMs);

            }
            dataList.add(data);

        }

        return new EvaluationGroup(evaluationName, operator, dataList);
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
}

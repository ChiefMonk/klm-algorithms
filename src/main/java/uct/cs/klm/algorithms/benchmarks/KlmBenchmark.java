package uct.cs.klm.algorithms.benchmarks;

import org.openjdk.jmh.annotations.*;
import uct.cs.klm.algorithms.enums.Algorithm;
import uct.cs.klm.algorithms.enums.InferenceOperator;
import uct.cs.klm.algorithms.models.EvaluationData;
import uct.cs.klm.algorithms.services.EvaluationServiceImpl;
import uct.cs.klm.algorithms.services.IKnowledgeBaseService;
import uct.cs.klm.algorithms.services.KnowledgeBaseServiceImpl;

import java.util.concurrent.TimeUnit;

/**
 * This class represents a KLM benchmark for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 2, time = 1)
@Fork(value = 1)
@State(Scope.Thread)
public class KlmBenchmark {

    @Param({"Naive", "Binary", "Ternary", "PowerSet"})
    private String algorithmName;

    @Param({"RationalClosure", "LexicographicClosure", "RelevantClosure"})
    private String inferenceOpName;

    @Param({"QuerySet_1", "QuerySet_2", "QuerySet_3", "QuerySet_4", "QuerySet_5"})
    private String querySet;

    private InferenceOperator operator;
    private Algorithm algorithm;

    private IKnowledgeBaseService knowledgeBaseService;
    private EvaluationServiceImpl evaluationService;

    @Setup
    public void setup() {
        this.knowledgeBaseService = new KnowledgeBaseServiceImpl();
        this.evaluationService = new EvaluationServiceImpl();
        this.algorithm = Algorithm.valueOf(algorithmName);
        this.operator = InferenceOperator.valueOf(inferenceOpName);
    }

    @Benchmark
    public EvaluationData runBenchmark() {
        return null;
    }
}

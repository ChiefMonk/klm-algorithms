package uct.cs.klm.algorithms.benchmarks;

import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;

public class JmhRunner {

    public static Collection<RunResult> runBenchmark(String algorithm, String operator, String query) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*KlmBenchmark.*") // match correct class
                .param("algorithmName", algorithm)
                .param("inferenceOpName", operator)
                .param("querySet", query)
                .forks(2)
                .build();

        return new Runner(opt).run();
    }
}

package com.github.ferstl.jmhexperiments.getcaller;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import com.github.ferstl.jmhexperiments.ChartFucker;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

/**
 * Benchmarks for different ways to get the caller of a method.
 */
@BenchmarkMode(AverageTime)
@OutputTimeUnit(MICROSECONDS)
public class GetCallerBenchmark {

  public static void main(String[] args) throws RunnerException {
    String fileName = "getcaller-result.csv";
    Options options = new OptionsBuilder()
        .include(".*GetCallerBenchmark.*")
        .warmupIterations(3)
        .measurementIterations(5)
        .resultFormat(ResultFormatType.CSV)
        .result(fileName)
        .build();
    new Runner(options).run();

    ChartFucker.fuck(options.getResult().orElse(fileName));
  }

  @Benchmark
  public Class<?> getCallerClassByStackWalker() {
    return GetCaller.getCallerClassByStackWalker();
  }

  @Benchmark
  public String getCallerClassNameByStackWalker() {
    return GetCaller.getCallerClassNameByStackWalker();
  }

  @Benchmark
  public Class<?> getCallerClassByLookup() {
    return GetCaller.getCallerClassByLookup();
  }

  @Benchmark
  public String getCallerClassNameByException() {
    return GetCaller.getCallerClassNameByException();
  }

}

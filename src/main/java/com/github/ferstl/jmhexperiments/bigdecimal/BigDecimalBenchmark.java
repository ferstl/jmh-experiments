package com.github.ferstl.jmhexperiments.bigdecimal;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.github.ferstl.jmhexperiments.ChartFucker;

/**
 * <a href="http://stackoverflow.com/questions/30474381/java-bigdecimal-strange-performance-behaviour/">java-bigdecimal-strange-performance-behaviour</a>
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BigDecimalBenchmark {

  static int i = 1024;

  public static void main(String[] args) throws RunnerException {
    String fileName = "bigdecimal-construct-result.csv";
    Options options = new OptionsBuilder()
        .include(".*BigDecimalBenchmark.*")
        .warmupIterations(10)
        .measurementIterations(10)
        .resultFormat(ResultFormatType.CSV)
        .result(fileName)
        .build();
    new Runner(options).run();

    ChartFucker.fuck(options.getResult().orElse(fileName));
  }

  @Benchmark
  public BigDecimal constructor() {
    return new BigDecimal(foo());
  }

  @Benchmark
  public BigDecimal localVariable() {
    int hash = foo();
    return new BigDecimal(hash);
  }

  private static int foo() {
    return i;
  }

}

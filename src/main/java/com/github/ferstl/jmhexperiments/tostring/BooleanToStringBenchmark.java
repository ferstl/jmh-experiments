package com.github.ferstl.jmhexperiments.tostring;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.github.ferstl.jmhexperiments.ChartFucker;

/**
 * Show the differences between various toString methods for booleans.
 */
public class BooleanToStringBenchmark {

  public static void main(String[] args) throws RunnerException {
    String fileName = "boolean-to-string-result.csv";
    Options options = new OptionsBuilder()
      .include(".*BooleanToStringBenchmark.*")
      .warmupIterations(10)
      .measurementIterations(10)
      .resultFormat(ResultFormatType.CSV)
      .result(fileName)
      .build();
    new Runner(options).run();

    ChartFucker.fuck(options.getResult().orElse(fileName));
  }

  @Fork(1)
  @Benchmark
  public void instanceToString(Blackhole bh) {
    bh.consume(Boolean.TRUE.toString());
  }

  @Fork(1)
  @Benchmark
  public void staticToString(Blackhole bh) {
    bh.consume(Boolean.toString(true));
  }

}

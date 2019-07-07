package com.github.ferstl.jmhexperiments.typecheck;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class InstanceOfBenchmark {

  public static void main(String[] args) throws RunnerException {
    Options options = new OptionsBuilder()
        .include("com\\.github\\.ferstl\\.jmhexperiments\\.typecheck\\..*")
        .warmupIterations(5)
        .measurementIterations(5)
        .resultFormat(ResultFormatType.TEXT)
        .result("instanceof-result.txt")
        .build();
    new Runner(options).run();
  }

}

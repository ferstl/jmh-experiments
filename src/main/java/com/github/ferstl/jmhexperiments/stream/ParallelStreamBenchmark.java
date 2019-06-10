package com.github.ferstl.jmhexperiments.stream;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Shows the difference between serial and parallel streams.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ParallelStreamBenchmark {

  public static void main(String[] args) throws RunnerException {
    Options options = new OptionsBuilder()
        .include(".*ParallelStreamBenchmark.*")
        .warmupIterations(5)
        .measurementIterations(5)
        .resultFormat(ResultFormatType.TEXT)
        .result("parallel-stream-result.txt")
        .build();
    new Runner(options).run();
  }

  @Param({"1000", "10000", "100000", "1000000"})
  public int arraySize;

  private int[] array;

  @Setup
  public void setUp() {
    this.array = new int[this.arraySize];
    Random random = new Random();
    for (int i = 0; i < this.array.length; i++) {
      this.array[i] = random.nextInt();
    }
  }

  @Benchmark
  public int parallel() {
    return Arrays.stream(this.array).parallel().max().getAsInt();
  }

  @Benchmark
  public int serial() {
    return Arrays.stream(this.array).max().getAsInt();
  }

}

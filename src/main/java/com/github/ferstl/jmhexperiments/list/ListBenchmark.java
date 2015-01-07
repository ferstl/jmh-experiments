package com.github.ferstl.jmhexperiments.list;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.github.ferstl.jmhexperiments.ChartFucker;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ListBenchmark {
  private static final int NR_OF_ELEMENTS = 10_000;

  public static void main(String[] args) throws RunnerException {
    Options options = new OptionsBuilder()
      .include(".*ListBenchmark.*")
      .warmupIterations(10)
      .measurementIterations(10)
      .resultFormat(ResultFormatType.CSV)
      .result("list-result.csv")
//      .jvmArgs("-Xms4G", "-Xmx4G")
      .build();
    new Runner(options).run();

    ChartFucker.fuck(options.getResult().orElse("list-result.csv"));
  }


  @Fork(1)
  @Benchmark
  public Object addBaseline() {
    Integer[] array = new Integer[NR_OF_ELEMENTS];
    for(int i = 0; i < NR_OF_ELEMENTS; i++) {
      array[i] = i;
    }

    return array;
  }

  @Fork(1)
  @Benchmark
  public Object addArrayList() {

    ArrayList<Integer> list = new ArrayList<>();
    for(int i = 0; i < NR_OF_ELEMENTS; i++) {
      list.add(i);
    }

    return list;
  }

  @Fork(1)
  @Benchmark
  public Object addLinkedList() {

    ArrayList<Integer> list = new ArrayList<>();
    for(int i = 0; i < NR_OF_ELEMENTS; i++) {
      list.add(i);
    }

    return list;
  }

}

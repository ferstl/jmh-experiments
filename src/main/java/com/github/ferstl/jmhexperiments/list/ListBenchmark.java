package com.github.ferstl.jmhexperiments.list;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
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
      .jvmArgs("-Xmx4G")
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

    List<Integer> list = new ArrayList<>();
    for(int i = 0; i < NR_OF_ELEMENTS; i++) {
      list.add(i);
    }

    return list;
  }

  @Fork(1)
  @Benchmark
  public Object addLinkedList() {

    List<Integer> list = new LinkedList<>();
    for(int i = 0; i < NR_OF_ELEMENTS; i++) {
      list.add(i);
    }

    return list;
  }

  @Fork(1)
  @Benchmark
  public void iterateBaseLine(Data data, Blackhole bh) {
    Integer[] array = data.array;
    for(Integer i : array) {
      bh.consume(i);
    }
  }

  @Fork(1)
  @Benchmark
  public void iterateArrayList(Data data, Blackhole bh) {
    List<Integer> list = data.arrayList;
    for(Integer i : list) {
      bh.consume(i);
    }
  }

  @Fork(1)
  @Benchmark
  public void iterateLinkedList(Data data, Blackhole bh) {
    List<Integer> list = data.linkedList;
    for(Integer i : list) {
      bh.consume(i);
    }
  }

  @Fork(1)
  @Benchmark
  public Object insertAtBeginningArrayList() {
    List<Integer> list = new ArrayList<>();
    for(int i = 0; i < NR_OF_ELEMENTS; i++) {
      list.add(0, i);
    }

    return list;
  }

  @Fork(1)
  @Benchmark
  public Object insertAtBeginningLinkedList() {
    List<Integer> list = new LinkedList<>();
    for(int i = 0; i < NR_OF_ELEMENTS; i++) {
      list.add(0, i);
    }

    return list;
  }

  @State(Scope.Benchmark)
  public static class Data {

    private volatile Integer[] array;
    private volatile List<Integer> arrayList;
    private volatile List<Integer> linkedList;

    @Setup
    public void setup() {
      this.array = new Integer[NR_OF_ELEMENTS];
      this.arrayList = new ArrayList<>(NR_OF_ELEMENTS);
      this.linkedList = new LinkedList<>();

      for(int i = 0; i < NR_OF_ELEMENTS; i++) {
        this.array[i] = i;
        this.arrayList.add(i);
        this.linkedList.add(i);
      }
    }
  }
}

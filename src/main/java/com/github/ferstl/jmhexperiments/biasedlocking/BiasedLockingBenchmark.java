package com.github.ferstl.jmhexperiments.biasedlocking;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class BiasedLockingBenchmark {

  public static void main(String[] args) throws RunnerException {
    Options options = new OptionsBuilder()
      .include(".*BiasedLockingBenchmark.*")
      .jvmArgs("-XX:BiasedLockingStartupDelay=0")
      .warmupIterations(10)
      .measurementIterations(10)
      .resultFormat(ResultFormatType.CSV)
      .result("biased-locking-result.csv")
      .build();
    new Runner(options).run();

    ChartFucker.fuck(options.getResult().orElse("biased-locking-result.csv"));
  }

  @Fork(1)
  @Benchmark
  public void baseline(Blackhole bh) {
    for (int i = 0; i < 10000; i++) {
      bh.consume(i);
    }
  }

  @Fork(value = 1, jvmArgsAppend = {"-XX:+UseBiasedLocking", "-XX:BiasedLockingStartupDelay=0"})
  @Benchmark
  public void biased(Data data, Blackhole bh) {
    for (int i = 0; i < 10000; i++) {
      bh.consume(data.incrementSynchronized());
    }
  }

  @Fork(value = 1, jvmArgsAppend = {"-XX:-UseBiasedLocking"})
  @Benchmark
  public void nonBiased(Data data, Blackhole bh) {
    for (int i = 0; i < 10000; i++) {
      bh.consume(data.incrementSynchronized());
    }
  }

  @Fork(value = 1, jvmArgsAppend = {"-XX:+UseBiasedLocking", "-XX:BiasedLockingStartupDelay=0"})
  @Benchmark
  public void nonBiasedWithIdentityHashCode(DataWithIdentityHashCode data, Blackhole bh) {
    for (int i = 0; i < 10000; i++) {
      bh.consume(data.incrementSynchronized());
    }
  }

  @State(Scope.Benchmark)
  public static class Data {
    int counter = 0;
    Lock lock = new ReentrantLock();

    public synchronized int incrementSynchronized() {
      return ++this.counter;
    }

    public int incrementWithLock() {
      try {
        this.lock.lock();
        int i = ++this.counter;
        return i;
      } finally {
        this.lock.unlock();
      }
    }
  }

  @State(Scope.Benchmark)
  public static class DataWithIdentityHashCode extends Data {

    @Setup
    public void setup() {
      System.identityHashCode(this);
    }
  }
}

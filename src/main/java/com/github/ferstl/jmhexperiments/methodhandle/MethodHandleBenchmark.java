package com.github.ferstl.jmhexperiments.methodhandle;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.github.ferstl.jmhexperiments.ChartFucker;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MethodHandleBenchmark {

  public static void main(String[] args) throws RunnerException {
    Options options = new OptionsBuilder()
      .include(".*Benchmark.*")
      .warmupIterations(10)
      .measurementIterations(10)
      .resultFormat(ResultFormatType.CSV)
      .result("methodhandle-result.csv")
      .build();
    new Runner(options).run();

    ChartFucker.fuck(options.getOutput().orElse("jmh-result.csv"));
  }

  @Fork(1)
  @Benchmark
  public double baseline(TestObject state) {
    return testMethod(state);
  }

  @Fork(1)
  @Benchmark
  public double reflection(TestObject state) throws Exception {
    Method method = MethodHandleBenchmark.class.getMethod("testMethod", TestObject.class);
    return (double) method.invoke(null, state);
  }

  public static double testMethod(TestObject testObject) {
    return testObject.i + testObject.d;
  }


  @State(Scope.Thread)
  public static class TestObject {
    private volatile int i;
    private volatile double d;

    @Setup(Level.Iteration)
    public void setup() {
      Random random = new Random();

      this.i = random.nextInt();
      this.d = random.nextDouble();
    }
  }
}

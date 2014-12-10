package com.github.ferstl.jmhexperiments.methodhandle;

import java.lang.invoke.MethodHandle;
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

import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodType.methodType;

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

    ChartFucker.fuck(options.getResult().orElse("jmh-result.csv"));
  }

  @Fork(1)
  @Benchmark
  public double baseline(TestObject state) {
    return TestObject.testMethod(state);
  }

  @Fork(1)
  @Benchmark
  public double reflection(TestObject state) throws Exception {
    return (double) state.method.invoke(null, state);
  }

  @Fork(1)
  @Benchmark
  public double methodHandle(TestObject state) throws Throwable {
    return (double) state.methodHandle.invoke(state);
  }

  @Fork(1)
  @Benchmark
  public double boundMethodHandle(TestObject state) throws Throwable {
    return (double) state.boundMethodHandle.invoke();
  }


  @State(Scope.Thread)
  public static class TestObject {
    private volatile int i;
    private volatile double d;
    private volatile Method method;
    private volatile MethodHandle methodHandle;
    private volatile MethodHandle boundMethodHandle;

    @Setup(Level.Iteration)
    public void setup() throws Exception {
      Random random = new Random();
      this.i = random.nextInt();
      this.d = random.nextDouble();

      this.method = getClass().getMethod("testMethod", TestObject.class);
      this.methodHandle = lookup().findStatic(TestObject.class, "testMethod", methodType(double.class, TestObject.class));
      this.boundMethodHandle = lookup().findStatic(TestObject.class, "testMethod", methodType(double.class, TestObject.class)).bindTo(this);
    }

    public static double testMethod(TestObject testObject) {
      return testObject.i + testObject.d;
    }
  }
}

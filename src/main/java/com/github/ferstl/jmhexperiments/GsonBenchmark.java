package com.github.ferstl.jmhexperiments;

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
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Show the difference between creating new {@link Gson} instances for each object to write vs. using the same Gson
 * instance.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
public class GsonBenchmark {

  private static final Gson GSON = createGson();

  public static void main(String[] args) throws RunnerException {
    Options options = new OptionsBuilder()
      .include(".*Benchmark.*")
      .warmupIterations(10)
      .measurementIterations(10)
      .build();
    new Runner(options).run();
  }

  /**
   * Base line is assembling the JSON string with a string builder.
   */
  @Benchmark
  public String baseline(TestState state) {
    StringBuilder sb = new StringBuilder();
    TestObject testObject = state.testObject;

    sb.append("{")
      .append("\"time\":").append("\"").append(testObject.time).append("\"")
      .append(",")
      .append("\"text\":").append("\"").append(testObject.text).append("\"")
      .append("}");

    return sb.toString();
  }

  @Benchmark
  public String newGson(TestState state) {
    Gson gson = createGson();
    return gson.toJson(state.testObject);
  }

  @Benchmark
  public String sameGson(TestState state) {
    return GSON.toJson(state.testObject);
  }

  private static Gson createGson() {
    return new GsonBuilder()
      .create();
  }

  @State(Scope.Thread)
  public static class TestState {
    private TestObject testObject;

    @Setup(Level.Iteration)
    public void setup() {
      Random random = new Random();

      char[] chars = new char[1024];
      for (int i = 0; i < chars.length; i++) {
        chars[i] = (char) (random.nextInt(57) + 65);
      }

      this.testObject = new TestObject(System.currentTimeMillis(), new String(chars));
    }
  }

  /**
   * Similar to {@link TestState} but without JMH subclassing.
   */
  private static class TestObject {
    long time;
    String text;

    public TestObject(long time, String text) {
      this.time = time;
      this.text = text;
    }
  }
}

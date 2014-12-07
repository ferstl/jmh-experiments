package com.github.ferstl.jmhexperiments.gson;

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
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.github.ferstl.jmhexperiments.ChartFucker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Show the difference between creating new {@link Gson} instances for each object to write vs. using the same Gson
 * instance.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class GsonBenchmark {
  private static final String JFR_OPTS_TEMPLATE = "-XX:FlightRecorderOptions=defaultrecording=true,settings=profile.jfc,dumponexit=true,dumponexitpath=";
  private static final Gson GSON = createGson();

  public static void main(String[] args) throws RunnerException {
    Options options = new OptionsBuilder()
      .include(".*Benchmark.*")
      .warmupIterations(5)
      .measurementIterations(5)
      .resultFormat(ResultFormatType.CSV)
      .jvmArgsPrepend("-XX:+UnlockCommercialFeatures", "-XX:+FlightRecorder")
      .build();
    new Runner(options).run();

    ChartFucker.fuck(options.getOutput().orElse("jmh-result.csv"));
  }

  /**
   * Base line is assembling the JSON string with a string builder.
   */
  @Fork(value = 1, jvmArgs = JFR_OPTS_TEMPLATE + "baseline.jfr")
  @Benchmark
  public String baseline(TestState state) {
    return baseline(state.testObject);
  }

  @Fork(value = 1, jvmArgs = JFR_OPTS_TEMPLATE + "newGson.jfr")
  @Benchmark
  public String newGson(TestState state) {
    return newGson(state.testObject);
  }

  @Fork(value = 1, jvmArgs = JFR_OPTS_TEMPLATE + "sameGson.jfr")
  @Benchmark
  public String sameGson(TestState state) {
    return sameGson(state.testObject);
  }

  @Fork(value = 1, jvmArgs = JFR_OPTS_TEMPLATE + "concurrentBaseline.jfr")
  @Threads(8)
  @Benchmark
  public String concurrentBaseline(TestState state) {
    return baseline(state.testObject);
  }

  @Fork(value = 1, jvmArgs = JFR_OPTS_TEMPLATE + "concurrentNewGson.jfr")
  @Benchmark
  @Threads(8)
  public String concurrentNewGson(TestState state) {
    return newGson(state.testObject);
  }

  @Fork(value = 1, jvmArgs = JFR_OPTS_TEMPLATE + "concurrentSameGson.jfr")
  @Benchmark
  @Threads(8)
  public String concurrentSameGson(TestState state) {
    return sameGson(state.testObject);
  }

  private String baseline(TestObject testObject) {
    return new StringBuilder().append("{")
      .append("\"time\":").append("\"").append(testObject.time).append("\"")
      .append(",")
      .append("\"text\":").append("\"").append(testObject.text).append("\"")
      .append("}")
      .toString();
  }

  private String newGson(TestObject testObject) {
    Gson gson = createGson();
    return gson.toJson(testObject);
  }

  private String sameGson(TestObject testObject) {
    return GSON.toJson(testObject);
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

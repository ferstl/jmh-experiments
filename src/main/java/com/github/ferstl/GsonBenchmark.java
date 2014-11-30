package com.github.ferstl;

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
import com.google.gson.annotations.Expose;


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
      .build();
    new Runner(options).run();
  }

  /**
   * Base line is assembling the JSON string with a string builder.
   */
  @Benchmark
  public String baseline(TestObject obj) {
    StringBuilder sb = new StringBuilder();
    sb.append("{")
      .append("\"time\":").append("\"").append(obj.time).append("\"")
      .append(",")
      .append("\"text\":").append("\"").append(obj.text).append("\"")
      .append("}");

    return sb.toString();
  }

  @Benchmark
  public String newGson(TestObject obj) {
    Gson gson = createGson();
    return gson.toJson(obj);
  }

  @Benchmark
  public String sameGson(TestObject obj) {
    return GSON.toJson(obj);
  }

  private static Gson createGson() {
    return new GsonBuilder()
      // JMH creates a subclass of state object. The fields of subclass must not be serialized.
      .excludeFieldsWithoutExposeAnnotation()
      .create();
  }

  @State(Scope.Thread)
  public static class TestObject {
    @Expose
    long time;
    @Expose
    String text;

    @Setup(Level.Iteration)
    public void setup() {
      Random random = new Random();

      this.time = System.currentTimeMillis();

      char[] chars = new char[1024];
      for (int i = 0; i < chars.length; i++) {
        chars[i] = (char) (random.nextInt(75) + 47);
      }

      this.text = new String(chars);
    }
  }
}

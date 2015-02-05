package com.github.ferstl.jmhexperiments.equalshashcodebuilder;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class EqualsHashCodeBuilderBenchmark {

  public static void main(String[] args) throws RunnerException {
    Options options = new OptionsBuilder()
      .include(".*EqualsHashCodeBuilderBenchmark.*")
      .warmupIterations(10)
      .measurementIterations(10)
      .resultFormat(ResultFormatType.CSV)
      .result("equals-hashcode-builder.csv")
      .build();

    new Runner(options).run();

    ChartFucker.fuck(options.getResult().orElse("equals-hashcode-builder.csv"));
  }

  @Fork(value = 1, jvmArgs = {/*"-XX:+UnlockCommercialFeatures", "-XX:+FlightRecorder", "-XX:FlightRecorderOptions=defaultrecording=true,settings=profile.jfc,dumponexit=true,dumponexitpath=equals.jfr"*/})
  @Benchmark
  public boolean equalsBaseline(TestState state) {
    return state.testObject1.equalsPlain(state.testObject2);
  }

  @Fork(1)
  @Benchmark
  public boolean equalsBuilder(TestState state) {
    return state.testObject1.equalsWithBuilder(state.testObject2);
  }

  @Fork(1)
  @Benchmark
  public int hashCodeBaseline(TestState state) {
    return state.testObject1.hashCodePlain();
  }

  @Fork(1)
  @Benchmark
  public int hashCodeBuilder(TestState state) {
    return state.testObject1.hashCodeWithBuilder();
  }

  @Fork(value = 1, jvmArgs = {/*"-XX:+UnlockCommercialFeatures", "-XX:+FlightRecorder", "-XX:FlightRecorderOptions=defaultrecording=true,settings=profile.jfc,dumponexit=true,dumponexitpath=equals-contended.jfr"*/})
  @Threads(3)
  @Benchmark
  public boolean equalsBaselineContended(TestState state) {
    return state.testObject1.equalsPlain(state.testObject2);
  }

  @Fork(1)
  @Threads(3)
  @Benchmark
  public boolean equalsBuilderContended(TestState state) {
    return state.testObject1.equalsWithBuilder(state.testObject2);
  }

  @Fork(1)
  @Threads(3)
  @Benchmark
  public int hashCodeBaselineContended(TestState state) {
    return state.testObject1.hashCodePlain();
  }

  @Fork(1)
  @Threads(3)
  @Benchmark
  public int hashCodeBuilderContended(TestState state) {
    return state.testObject1.hashCodeWithBuilder();
  }

  // Just to verify that the implementations produce correct results
  public static void testImplementation() {
    TestState state = new TestState();
    state.setup();

    System.out.println(state.testObject1.equalsPlain(state.testObject2));
    System.out.println(state.testObject1.equalsWithBuilder(state.testObject2));
    System.out.println(state.testObject1.hashCodePlain());
    System.out.println(state.testObject1.hashCodeWithBuilder());
    System.out.println(state.testObject2.hashCodePlain());
    System.out.println(state.testObject2.hashCodeWithBuilder());
  }

  @State(Scope.Thread)
  public static class TestState {

    private TestObject testObject1;
    private TestObject testObject2;

    @Setup(Level.Trial)
    public void setup() {
      long l;
      double d;
      String s1;
      String[] array1;
      String s2;
      String[] array2;
      Random random = new Random();

      array1 = new String[20];
      array2 = new String[20];
      for (int i = 0; i < array1.length; i++) {
        array1[i] = createRandomString(random, 20);
        array2[i] = new String(array1[i]);
      }

      l = random.nextLong();
      d = random.nextDouble();
      s1 = createRandomString(random, 200);
      s2 = new String(s1);

      this.testObject1 = new TestObject(l, d, s1, array1);
      this.testObject2 = new TestObject(l, d, s2, array2);
    }

    private String createRandomString(Random random, int size) {
      char[] chars = new char[size];
      for (int i = 0; i < chars.length; i++) {
        chars[i] = (char) (random.nextInt(57) + 65);
      }

      return new String(chars);
    }
  }

  private static final class TestObject {
    private final long l;
    private final double d;
    private final String s;
    private final Object[] array;

    public TestObject(long l, double d, String s, Object[] array) {
      this.l = l;
      this.d = d;
      this.s = s;
      this.array = array;
    };

    public boolean equalsPlain(Object o) {
      if (o == this) { return true; }
      if (!(o instanceof TestObject)) { return false; }

      TestObject other = (TestObject) o;
      return this.l == other.l
          && this.d == other.d
          && Objects.equals(this.s, other.s)
          && Arrays.equals(this.array, other.array);
    }

    // generated by Eclipse
    public int hashCodePlain() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(this.array);
      long temp;
      temp = Double.doubleToLongBits(this.d);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + (int) (this.l ^ (this.l >>> 32));
      result = prime * result + ((this.s == null) ? 0 : this.s.hashCode());
      return result;
    }

    public boolean equalsWithBuilder(Object o) {
      if (o == this) { return true; }
      if (!(o instanceof TestObject)) { return false; }

      TestObject other = (TestObject) o;

      return new EqualsBuilder()
        .append(this.l, other.l)
        .append(this.d, other.d)
        .append(this.s, other.s)
        .append(this.array, other.array)
        .isEquals();
    }

    public int hashCodeWithBuilder() {
      return new HashCodeBuilder()
        .append(this.l)
        .append(this.d)
        .append(this.s)
        .append(this.array)
        .toHashCode();
    }
  }
}

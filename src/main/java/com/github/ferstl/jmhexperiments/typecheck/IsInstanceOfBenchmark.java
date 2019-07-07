package com.github.ferstl.jmhexperiments.typecheck;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class IsInstanceOfBenchmark {

  private BaseInterface instance;

  @Setup
  public void setUp() {
    this.instance = new BaseClass();
  }

  @Benchmark
  public ExtendedInterface instanceOf() {
    if (this.instance instanceof ExtendedInterface) {
      return (ExtendedInterface) this.instance;
    } else {
      return null;
    }
  }

  @Benchmark
  public ExtendedInterface cast() {
    try {
      return (ExtendedInterface) this.instance;
    } catch (Throwable t) {
      return null;
    }
  }

}

package com.github.ferstl.jmhexperiments.tostring;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
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

/**
 * Show the differences between various toString methods for Strings.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class IntegerToStringBenchmark {

    public static void main(String[] args) throws RunnerException {
        String fileName = "integer-to-string-result.csv";
        Options options = new OptionsBuilder()
                .include(".*IntegerToStringBenchmark.*")
                .warmupIterations(10)
                .measurementIterations(10)
                .resultFormat(ResultFormatType.CSV)
                .result(fileName)
                .build();
        new Runner(options).run();

        ChartFucker.fuck(options.getResult().orElse(fileName));
    }

    int small;
    int large;

    @Setup
    public void setup() {
      this.small = 1;
      this.large = Integer.MAX_VALUE;
    }

    @Benchmark
    public String quoteSmall() {
        return "" + this.small;
    }

    @Benchmark
    public String quoteLarge() {
        return "" + this.large;
    }

    @Benchmark
    public String toStringSmall() {
        return Integer.toString(this.small);
    }

    @Benchmark
    public String toStringLarge() {
        return Integer.toString(this.large);
    }


}

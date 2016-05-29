package com.github.ferstl.jmhexperiments.list;

import java.util.ArrayList;
import java.util.List;
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
 * Show the differences between various iteration methods over ArrayList.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class IterationBenchmark {

    public static void main(String[] args) throws RunnerException {
        String fileName = "list-iteraltion-result.csv";
        Options options = new OptionsBuilder()
                .include(".*IterationBenchmark.*")
                .warmupIterations(10)
                .measurementIterations(10)
                .resultFormat(ResultFormatType.CSV)
                .result(fileName)
                .build();
        new Runner(options).run();

        ChartFucker.fuck(options.getResult().orElse(fileName));
    }

    private List<Integer> list;

    @Setup
    public void setup() {
        this.list = new ArrayList<>(128);
        for (int i = 0; i < 128; i++) {
            this.list.add(i);
        }

        // mess up class hierarchy analysis
        this.list.forEach((i) -> { if (i > 128) System.out.println(i); });
        this.list.forEach((i) -> { if (i > 129) System.out.println(i); });
        this.list.forEach((i) -> { if (i > 130) System.out.println(i); });
        this.list.forEach((i) -> { if (i > 131) System.out.println(i); });
        this.list.forEach((i) -> { if (i > 132) System.out.println(i); });
    }

    @Benchmark
    public int forEachStatement() {
        Accumulator acc = new Accumulator();
        for (Integer i : this.list) {
            acc.add(i);
        }
        return acc.sum;
    }

    @Benchmark
    public int forEachMethod() {
        Accumulator acc = new Accumulator();
        this.list.forEach(acc::add);
        return acc.sum;
    }

    @Benchmark
    public int getWithCallToSize() {
        Accumulator acc = new Accumulator();
        for (int i = 0; i < this.list.size(); i++) {
            Integer element = this.list.get(i);
            acc.add(element);
        }
        return acc.sum;
    }

    @Benchmark
    public int getWithoutCallToSize() {
        Accumulator acc = new Accumulator();
        int size = this.list.size();
        for (int i = 0; i < size; i++) {
            Integer element = this.list.get(i);
            acc.add(element);
        }
        return acc.sum;
    }

    static final class Accumulator {

        int sum;

        void add(int i) {
            this.sum += i;
        }

    }

}

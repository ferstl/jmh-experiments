package com.github.ferstl.jmhexperiments.equals;

import java.security.MessageDigest;
import java.util.Arrays;
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
 * Checks if HotSpot optimizes array compares that are supposed to be constant time.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ArrayEqualsBenchmark {

    public static void main(String[] args) throws RunnerException {
      Options options = new OptionsBuilder()
        .include(".*ArrayEqualsBenchmark.*")
        .warmupIterations(10)
        .measurementIterations(10)
        .resultFormat(ResultFormatType.CSV)
        .result("equals-hashcode-builder.csv")
        .build();

      new Runner(options).run();

      ChartFucker.fuck(options.getResult().orElse("equals-array.csv"));
    }

    private static final int ARRAY_SIZE = 0x400;

    private byte[] firstArray;

    private byte[] secondArray;

    @Setup
    public void setup() {
        this.firstArray = new byte[ARRAY_SIZE];
        for (int i = 0; i < firstArray.length; i++) {
            firstArray[i] = (byte) i;
        }
        this.secondArray = new byte[this.firstArray.length];
        System.arraycopy(this.firstArray, 0, this.secondArray, 0, ARRAY_SIZE);
        // make sure difference is in the first byte
        this.secondArray[0] = (byte) (this.secondArray[0] + 1);
    }

    @Benchmark
    public boolean messageDigest() {
        return MessageDigest.isEqual(this.firstArray, this.secondArray);
    }

    @Benchmark
    public boolean customIsEqual() {
        return isEqual(this.firstArray, this.secondArray);
    }

    @Benchmark
    public boolean isEqualBoolean() {
        return isEqualBoolean(this.firstArray, this.secondArray);
    }

    @Benchmark
    public boolean arrays() {
        return Arrays.equals(this.firstArray, this.secondArray);
    }

    public static boolean isEqual(byte[] digesta, byte[] digestb) {
        if (digesta == digestb) return true;
        if (digesta == null || digestb == null) {
            return false;
        }
        if (digesta.length != digestb.length) {
            return false;
        }

        int result = 0;
        // time-constant comparison
        for (int i = 0; i < digesta.length; i++) {
            result |= digesta[i] ^ digestb[i];
        }
        return result == 0;
    }

    public static boolean isEqualBoolean(byte[] digesta, byte[] digestb) {
        if (digesta == digestb) return true;
        if (digesta == null || digestb == null) {
            return false;
        }
        if (digesta.length != digestb.length) {
            return false;
        }

        boolean result = true;
        // time-constant comparison
        for (int i = 0; i < digesta.length; i++) {
            if (digesta[i] != digestb[i]) {
                result = false;
            }
        }
        return result;
    }

}

package com.github.ferstl.jmhexperiments.regex;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.github.ferstl.jmhexperiments.ChartFucker;

/**
 * Show the differences between different ways to validate WildFly AS controller
 * path elements.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class RegexBenchmark {

  /**
   * A valid key contains alphanumerics and underscores, cannot start with a
   * number, and cannot start or end with {@code -}.
   */
  private static final Pattern VALID_KEY_PATTERN = Pattern.compile("\\*|[_a-zA-Z](?:[-_a-zA-Z0-9]*[_a-zA-Z0-9])?");
  private static final String WILDCARD_VALUE = "*";


  public static void main(String[] args) throws RunnerException {
    String fileName = "regex-validate-result.csv";
    Options options = new OptionsBuilder()
      .include(".*RegexBenchmark.*")
      .warmupIterations(10)
      .measurementIterations(10)
      .resultFormat(ResultFormatType.CSV)
      .result(fileName)
      .build();
    new Runner(options).run();

    ChartFucker.fuck(options.getResult().orElse(fileName));
  }

  @Benchmark
  public boolean benchmarkRegex() {
    return validateRegex("server-group");
  }

  @Benchmark
  public boolean benchmarkString() {
    return validateString("server-group");
  }


  private static boolean validateRegex(String s) {
    return VALID_KEY_PATTERN.matcher(s).matches();
  }

  private static boolean validateString(String s) {
    if (s == null) {
        return false;
    }
    if (s.equals(WILDCARD_VALUE)) {
        return true;
    }
    int lastIndex = s.length() - 1;
    if (!isValidStartCharacter(s.charAt(0))) {
      return false;
    }
    for (int i = 1; i < lastIndex; i++) {
      if (!isValidCharacter(s.charAt(i))) {
        return false;
      }
    }
    if (lastIndex > 0 && !isValidEndCharacter(s.charAt(lastIndex))) {
      return false;
    }
    return true;
  }

  private static boolean isValidStartCharacter(char c) {
    return c == '_'
        || c >= 'a' && c <= 'z'
        || c >= 'A' && c <= 'Z';
  }

  private static boolean isValidEndCharacter(char c) {
    return c == '_'
        || c >= '0' && c <= '9'
        || c >= 'a' && c <= 'z'
        || c >= 'A' && c <= 'Z';
  }

  private static boolean isValidCharacter(char c) {
    return c == '_' || c == '-'
        || c >= '0' && c <= '9'
        || c >= 'a' && c <= 'z'
        || c >= 'A' && c <= 'Z';
  }

}

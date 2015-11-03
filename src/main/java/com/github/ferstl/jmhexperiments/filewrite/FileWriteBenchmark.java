package com.github.ferstl.jmhexperiments.filewrite;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class FileWriteBenchmark {

  public static void main(String[] args) throws RunnerException {
    String fileName = "bigdecimal-construct-result.csv";
    Options options = new OptionsBuilder()
        .include(".*FileWriteBenchmark.*")
        .warmupIterations(0)
        .measurementIterations(5)
        .resultFormat(ResultFormatType.CSV)
        .result(fileName)
        .build();
    new Runner(options).run();

    ChartFucker.fuck(options.getResult().orElse(fileName));
  }

  @Benchmark
  public void bufferedWriterWithFlush() throws IOException {
    String text = createText();
    try(BufferedWriter writer = Files.newBufferedWriter(Paths.get("text"), StandardCharsets.UTF_8)) {

      for (int i = 0; i < 10_000; i++) {
        writer.write(text);
        writer.write("\n");
        writer.flush();
      }
    }
  }

  @Benchmark
  public void bufferedWriterWithSync() throws IOException {
    String text = createText();
    FileOutputStream fos = new FileOutputStream(Paths.get("text").toFile());

    try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))) {

      for (int i = 0; i < 10_000; i++) {
        writer.write(text);
        writer.write("\n");
        fos.getFD().sync();
      }
    }
  }

  private static final String createText() {
    Random rand = new Random();
    byte[] bytes = new byte[6144];
    rand.nextBytes(bytes);

    return Base64.getEncoder().encodeToString(bytes);
  }
}

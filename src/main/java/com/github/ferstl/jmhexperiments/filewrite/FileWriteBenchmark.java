package com.github.ferstl.jmhexperiments.filewrite;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
public class FileWriteBenchmark {

  private static final int NR_OF_LINES = 30_000;

  public static void main(String[] args) throws RunnerException {
    String fileName = "filewrite-result.csv";
    Options options = new OptionsBuilder()
        .include(".*FileWriteBenchmark.*")
        .warmupIterations(0)
        .measurementIterations(5)
        .forks(5)
        .resultFormat(ResultFormatType.CSV)
        .result(fileName)
        .build();
    new Runner(options).run();

    ChartFucker.fuck(options.getResult().orElse(fileName));
  }

  @Benchmark
  public void bufferedWriterWithFlush() throws IOException {
    String text = createText();

    try(BufferedWriter writer = newBufferedWriter(Paths.get("file1"), UTF_8, CREATE, TRUNCATE_EXISTING)) {
      for (int i = 0; i < NR_OF_LINES; i++) {
        writeLine(text, writer);
      }
    }
  }

  @Benchmark
  public void bufferedWriterWithSync() throws IOException {
    String text = createText();

    try(FileOutputStream fos = new FileOutputStream(Paths.get("file2").toFile(), false)) {
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, UTF_8));
      for (int i = 0; i < NR_OF_LINES; i++) {
        writeLine(text, writer);
        fos.getFD().sync();
      }
    }
  }

  @Benchmark
  public void bufferedWriterWithSyncAfter10Lines() throws IOException {
    String text = createText();

    try(FileOutputStream fos = new FileOutputStream(Paths.get("file10").toFile(), false)) {
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, UTF_8));
      for (int i = 0; i < NR_OF_LINES; i++) {
        writeLine(text, writer);
        if (i % 10 == 0) {
          fos.getFD().sync();
        }
      }
    }
  }

  @Benchmark
  public void bufferedWriterWithSyncAfter100Lines() throws IOException {
    String text = createText();

    try(FileOutputStream fos = new FileOutputStream(Paths.get("file100").toFile(), false)) {
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, UTF_8));
      for (int i = 0; i < NR_OF_LINES; i++) {
        writeLine(text, writer);
        if (i % 100 == 0) {
          fos.getFD().sync();
        }
      }
    }
  }

  @Benchmark
  public void bufferedWriterWithSyncAfter1000Lines() throws IOException {
    String text = createText();

    try(FileOutputStream fos = new FileOutputStream(Paths.get("file1000").toFile(), false)) {
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, UTF_8));
      for (int i = 0; i < NR_OF_LINES; i++) {
        writeLine(text, writer);
        if (i % 1000 == 0) {
          fos.getFD().sync();
        }
      }
    }
  }

  private void writeLine(String text, Writer writer) throws IOException {
    writer.write(text);
    writer.write("\n");
    writer.flush();
  }

  private static final String createText() {
    Random rand = new Random();
    byte[] bytes = new byte[6144];
    rand.nextBytes(bytes);

    return Base64.getEncoder().encodeToString(bytes);
  }
}

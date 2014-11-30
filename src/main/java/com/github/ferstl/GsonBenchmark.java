/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

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

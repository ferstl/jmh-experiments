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
  private static final String EXAMPLE_TEXT =
      "72bH`CZYn6Kk\u003dRUpD2L8bHxqCLBrCkLFOXcXkKt/E\u003eMrFfyjV\\?5JGfxI@XOq\u003eyjCClot]`4YwvKF:\u003d"
    + "dvKYkTO_lh_xFVFpT\u003ca6Q@W\u003eY1WCjJAUU5aNAAugfWs^SFF:gZ0A3ENw9\u003c?dvMNCaDWAjr?Orcp2Lb?ttDV[N"
    + "p5vVaVp@EpM;Oklaqq\u003e;s4@5?UoduJkOQYv_aK5tZ\u003eq\u003d`EU5O^P^MkY0UknGslO3M^\u003dPUKsV50SmC;Wo"
    + "FnD:WXow?XQT30\u003c4`DfOVKmO9v9Eo9\u003emTmUlkpjCx75\u003dJD\\@G1lICrBf78RhFm6MlcQTaQyI0w/9dv3CQp?7"
    + "R?Hy1[nIosZSIOre9vcS?[rvK?]fd;^kr[GqrV\u003c:hjcDy9wdDg\u003c^tFe\u003eNG\u003dfbfu^L\\b`EW:ep/75VRr"
    + "wQfnLJLlHh7vLsq]4\u003cCDRf\u003e`EhlSb0DPK3N\\8N]kZPRV`sfV]yrtY_pb/Nm?UBNRe2Z0@t\u003efFE6T^Vokdx?T"
    + "muI??Z6u_y9rdI2k[_\u003d::M/NY5`sOUk`U7F5IfOYkaBtA6Hcey]8hN^@pbHgN\\uBwDxR05VRAD`4xYZ`F\u003dXc\u003d"
    + "@\u003dft]DUMh@Wek?4Cso@yY_^cDbl@sFyg;@RB/lnOdEEcpE5oE\u003egh[j@\u003cSf/pMpL8trmQyvsV?NKv@ivgjQbS"
    + "qESP1ino6^\u003d\u003d^^UJXq\\D1\u003cZURaPh7Ax5Vt@Jscc\\Wqb?BGW0IO]ibjAgp?Nv^4@Tpe\u003c?eUhm8_g9p^R"
    + "jMAljX:]:Bh0eq7Y8qCBA;LC8\u003cCUZ9nr1`4I;irL\u003eGH\\4uQ`k\\OppRMx\\tl[5C6/uPn\u003dp^M\u003ddA;m6"
    + "pi5Q@\\\u003cHX0\u003eruHV4]k@8S9BxUXeS^k^10Z8:DCLk3/ZxG\\jD?f2HOYF/\u003c;o:oAOPGqAS[^wXmvA?kG2fEyw"
    + "jf1RT8bN@6[5tvJGmrnpu?ns5k13N9PdH:7VR1GY\\1C_581t8choRVr?ajl[;GhWF4J@^N;NvZE19UK2nCetMwqg_wZrlbgsS@V"
    + "__V2QMKv[`p01VVZAVI6kHI3ZC";

  public static void main(String[] args) throws RunnerException {
    Options options = new OptionsBuilder()
      .include(".*Benchmark.*")
      .build();
    new Runner(options).run();
  }

  @Benchmark
  public String baseline(TestObject obj) {
    StringBuilder sb = new StringBuilder();
    sb.append("{")
      .append("\"time\":")
      .append("\"text\":").append("\"").append(EXAMPLE_TEXT).append("\"")
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
    return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
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

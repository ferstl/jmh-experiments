package com.github.ferstl.jmhexperiments.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
 * Measures the overhead of JDK proxies.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class ProxyOverheadBenchmark {

  public static void main(String[] args) throws RunnerException {
    Options options = new OptionsBuilder()
        .include(".*ProxyOverheadBenchmark.*")
        .warmupIterations(10)
        .measurementIterations(10)
        .resultFormat(ResultFormatType.CSV)
        .result("proxy-overhead.csv")
        .build();

    new Runner(options).run();

    ChartFucker.fuck(options.getResult().orElse("proxy-overhead.csv"));
  }

  private NoArgumentProxyInterface noArgumentImplementation;

  private NoArgumentProxyInterface noArgumentProxy;

  private OneArgumentProxyInterface oneArgumentImplementation;

  private OneArgumentProxyInterface oneArgumentProxy;

  @Setup
  public void setup() {
    this.noArgumentImplementation = new NoArgumentImplementation();
    ClassLoader classLoader = ProxyOverheadBenchmark.class.getClassLoader();
    this.noArgumentProxy = (NoArgumentProxyInterface) Proxy.newProxyInstance(classLoader,
        new Class[]{NoArgumentProxyInterface.class},
        new NoArgumentProxyInterfaceHandler());
    this.oneArgumentImplementation = new OneArgumentImplementation();
    this.oneArgumentProxy = (OneArgumentProxyInterface) Proxy.newProxyInstance(classLoader,
        new Class[]{OneArgumentProxyInterface.class},
        new OneArgumentProxyInterfaceHandler());
  }

  @Benchmark
  public String noArgumentImplementation() {
    return this.noArgumentImplementation.interfaceMethod();
  }

  @Benchmark
  public String noArgumentProxy() {
    return this.noArgumentProxy.interfaceMethod();
  }

  @Benchmark
  public String oneArgumentImplementation() {
    return this.oneArgumentImplementation.interfaceMethod("arg");
  }

  @Benchmark
  public String oneArgumentProxy() {
    return this.oneArgumentProxy.interfaceMethod("arg");
  }

  interface NoArgumentProxyInterface {

    String interfaceMethod(); // intentionally use reference type

  }

  final class NoArgumentImplementation implements NoArgumentProxyInterface {

    @Override
    public String interfaceMethod() {
      return "implementation";
    }

  }

  final class NoArgumentProxyInterfaceHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      return "proxy";
    }

  }

  interface OneArgumentProxyInterface {

    String interfaceMethod(String s); // intentionally use reference type

  }

  final class OneArgumentImplementation implements OneArgumentProxyInterface {

    @Override
    public String interfaceMethod(String s) {
      return s;
    }

  }

  final class OneArgumentProxyInterfaceHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      return args[0];
    }

  }

}

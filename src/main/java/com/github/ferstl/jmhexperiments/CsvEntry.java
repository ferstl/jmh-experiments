package com.github.ferstl.jmhexperiments;


public class CsvEntry {
  private String benchmark;
  private String mode;
  private int threads;
  private int samples;
  private double score;
  private double error;
  private String unit;

  public String getBenchmark() {
    return this.benchmark;
  }

  public void setBenchmark(String benchmark) {
    this.benchmark = benchmark;
  }

  public String getMode() {
    return this.mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public int getThreads() {
    return this.threads;
  }

  public void setThreads(int threads) {
    this.threads = threads;
  }

  public int getSamples() {
    return this.samples;
  }

  public void setSamples(int samples) {
    this.samples = samples;
  }

  public double getScore() {
    return this.score;
  }

  public void setScore(double score) {
    this.score = score;
  }

  public double getError() {
    return this.error;
  }

  public void setError(double error) {
    this.error = error;
  }

  public String getUnit() {
    return this.unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

}

package com.github.ferstl.jmhexperiments;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;


public class ChartFucker {

  private static final String[] CSV_COLUMNS = { "benchmark", "mode", "threads", "samples", "score", "error", "unit" };
  private static final CellProcessor[] CSV_CELL_PROCESSORS = {null, null, new ParseInt(), new ParseInt(), new ParseDouble(), new ParseDouble(), null };

  public static void fuck(String fileName) {
    try(CsvBeanReader csvReader = new CsvBeanReader(Files.newBufferedReader(Paths.get(fileName)), CsvPreference.STANDARD_PREFERENCE)) {
      // skip the header
      csvReader.getHeader(true);

      DefaultStatisticalCategoryDataset ds = new DefaultStatisticalCategoryDataset();
      CsvEntry entry;
      while((entry = csvReader.read(CsvEntry.class, CSV_COLUMNS, CSV_CELL_PROCESSORS)) != null) {
        addToDataSet(ds, entry);
      }

      CategoryAxis xAxis = new CategoryAxis("Benchmark");
      NumberAxis yAxis = new NumberAxis("performance");

      StatisticalBarRenderer renderer = new StatisticalBarRenderer();
      CategoryPlot plot = new CategoryPlot(ds, xAxis, yAxis, renderer);
      plot.setOrientation(PlotOrientation.HORIZONTAL);

      JFreeChart chart = new JFreeChart(plot);
      chart.removeLegend();
      ChartUtilities.saveChartAsPNG(getChartFile(fileName), chart, 600, 300);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  private static void addToDataSet(DefaultStatisticalCategoryDataset ds, CsvEntry entry) {
    int methodStart = entry.getBenchmark().lastIndexOf('.');
    String method = entry.getBenchmark().substring(methodStart + 1);

    ds.add(entry.getScore(), entry.getError(), "", method);
  }

  private static File getChartFile(String csvFileName) {
    int extStart = csvFileName.lastIndexOf('.');
    String chartFileName = csvFileName.substring(0, extStart) + ".png";
    return Paths.get(chartFileName).toFile();
  }
}

package io.github.mecruty.analyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import java.awt.Color;
import java.awt.Font;

// Class for creating different kinds of charts
public class ChartVisualizer {
    private static final int CHART_WIDTH = 800;
    private static final int CHART_HEIGHT = 600;
    private static final int RENDER_SCALE_FACTOR = 3;

    private String username;

    public ChartVisualizer(String username) {
        this.username = username;
    }

    public void displayChart(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame("Chart Displayer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

    // Saves chart as png
    public void saveChart(String name, JFreeChart chart, String folder) {
        try {
            // Puts file in specific folder within visualization
            File dest = new File("./data/" + username + "/visualization/" + folder + "/" + name + ".png");

            // Creates all directories for data
            File dir = dest.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }

            // Renders at 2x resolution for much clearer image
            ChartUtils.writeScaledChartAsPNG(
                new FileOutputStream(dest), 
                chart, CHART_WIDTH, CHART_HEIGHT, 
                RENDER_SCALE_FACTOR, RENDER_SCALE_FACTOR);

        } catch (IOException e) {
            throw new RuntimeException("Saving chart failed");
        }
    }

    // Creates bar chart given map of values
    public JFreeChart createBarChart(String title, Map<String, Integer> values, String rowName) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Sort columns by descreasing order of count
        List<Entry<String, Integer>> list = new ArrayList<>(values.entrySet());
        list.sort(Entry.<String, Integer>comparingByValue().reversed());

        for (Entry<String, Integer> value : list) {
            dataset.addValue(value.getValue(), "Result", value.getKey());
        }

        // Creating the chart
        JFreeChart chart = formatBarChart(title, dataset, rowName);

        return chart;
    }

    // Creates histogram
    public JFreeChart createHistogram(String title, Map<String, Integer> values, String rowName) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<Entry<String, Integer>> list = new ArrayList<>(values.entrySet());
        for (Entry<String, Integer> value : list) {
            dataset.addValue(value.getValue(), "Result", value.getKey());
        }

        JFreeChart chart = formatBarChart(title, dataset, rowName, true);

        return chart;
    }

    // defaults to not histogram
    private JFreeChart formatBarChart(String title, DefaultCategoryDataset dataset, String rowName) {
        return formatBarChart(title, dataset, rowName, false);
    }

    // Creates a bar chart given proper dataset
    private JFreeChart formatBarChart(String title, DefaultCategoryDataset dataset, String rowName, boolean isHistogram) {
        JFreeChart chart = ChartFactory.createBarChart(
                title,
                null,
                rowName,
                dataset,
                isHistogram ? PlotOrientation.VERTICAL : PlotOrientation.HORIZONTAL,
                false,
                true,
                false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        
        // gridlines light grey
        plot.setRangeGridlinePaint(new Color(200, 200, 200)); 
        plot.setRangeGridlinesVisible(true);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        // random cyan I liked
        renderer.setSeriesPaint(0, new Color(79, 189, 189));
        // max bar width 10%
        renderer.setMaximumBarWidth(0.1);

        // displays count of each value at end of bar
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setSeriesItemLabelFont(0, new Font("Calibri", Font.PLAIN, 12));

        renderer.setIncludeBaseInRange((!isHistogram));
        formatBarAxes(plot, dataset, (!isHistogram));

        return chart;
    }

    // Formats margins and bounds of bar chart axes
    private void formatBarAxes(CategoryPlot plot, DefaultCategoryDataset dataset, boolean includeZero) {
        // Making bars closer together when theres less, currently max bar width is 10% of graph
        // So max space given is count * 10%
        CategoryAxis domainAxis = plot.getDomainAxis();

        int colCount = dataset.getColumnCount();
        domainAxis.setLowerMargin(Math.max(0.03, 0.5 - 0.05 * colCount));
        domainAxis.setUpperMargin(Math.max(0.03, 0.5 - 0.05 * colCount));
        domainAxis.setCategoryMargin(0.05);

        // If boundaries become inconsistent, pass through list
        // For now, default behaviour seems to be mostly stable

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(includeZero);
    }
}

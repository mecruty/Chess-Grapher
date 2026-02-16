package io.github.mecruty.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

// Class for creating different kinds of charts
public class ChartVisualizer {
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

    public void saveChart(String name, JFreeChart chart, String folder) {
        try {
            // Puts file in specific folder within visualization
            File dest = new File("./data/" + username + "/visualization/" + folder + "/" + name + ".png");

            // Creates all directories for data
            File dir = dest.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdir();
            }

            ChartUtils.saveChartAsPNG(dest, chart, 640, 480);
        } catch (IOException e) {
            throw new RuntimeException("Saving chart failed");
        }
    }

    public JFreeChart createPieChart(String title, Map<String, Integer> values) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        // Find total sum of values to calculate percentage
        double total = 0;
        for (int value : values.values()) {
            total += value;
        }

        for (Entry<String, Integer> value : values.entrySet()) {
            dataset.setValue(value.getKey(), value.getValue() / total);
        }

        // Creating the chart
        JFreeChart chart = ChartFactory.createPieChart(
                title,
                dataset,
                true,
                true,
                false);

        // // Removes the labels (look weird)
        // PiePlot plot = (PiePlot) chart.getPlot();
        // plot.setLabelGenerator(null);

        return chart;
    }
}

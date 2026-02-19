package io.github.mecruty.analyzer;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.jfree.chart.JFreeChart;

import io.github.mecruty.analyzer.frequencyAnalyzers.ComplexFrequencyAnalyzer;
import io.github.mecruty.analyzer.frequencyAnalyzers.SimpleFrequencyAnalyzer;

public class GameAnalyzer {
    private String username;
    private List<List<String>> csv;
    private ChartVisualizer vis;

    public GameAnalyzer(String username, List<List<String>> csv) {
        this.csv = csv;
        this.username = username;
        vis = new ChartVisualizer(this.username);

        // Creates the directory for the general visualization
        File dir = new File("./data/" + username + "/visualization");
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
    }

    public void analyzeAll() {
        analyzeAllSimpleFrequency();
        analyzeComplexFrequency("time_class", "bullet", "result");

        System.out.println("Data analyzed!");
    }

    public void analyzeAllSimpleFrequency() {
        SimpleFrequencyAnalyzer sfa = new SimpleFrequencyAnalyzer(csv);
        Map<String, Map<String, Integer>> result = sfa.analyzeAll();

        createFrequencyCharts(result, "simple frequency");
    }

    public void analyzeComplexFrequency(String filterKey, String filterValue, String dataKey) {
        ComplexFrequencyAnalyzer cfa = new ComplexFrequencyAnalyzer(csv);
        Map<String, Map<String, Integer>> result = cfa.analyze(filterKey, filterValue, dataKey);

        createFrequencyCharts(result, "complex frequency");
    }

    // Creates, saves, and displays a frequency chart
    private void createFrequencyCharts(Map<String, Map<String, Integer>> result, String folder) {
        for (String key : result.keySet()) {
            JFreeChart chart = vis.createBarChart(key, result.get(key));
            vis.saveChart(key, chart, folder);
            vis.displayChart(chart);
        }
    }
}

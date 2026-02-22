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
        // example for now
        analyzeComplexFrequency("time_class", "bullet");

        System.out.println("Data analyzed!");
    }

    public void analyzeAllSimpleFrequency() {
        SimpleFrequencyAnalyzer sfa = new SimpleFrequencyAnalyzer(csv);
        Map<String, Map<String, Integer>> result = sfa.analyzeAll();

        createFrequencyCharts(result, "simple frequency");
    }

    public void analyzeComplexFrequency(String filterKey, String filterValue) {
        ComplexFrequencyAnalyzer cfa = new ComplexFrequencyAnalyzer(csv);
        Map<String, Map<String, Integer>> result = cfa.analyze(filterKey, filterValue);

        createFrequencyCharts(result, "complex frequency");
    }

    // Creates, saves, and displays a frequency chart
    private void createFrequencyCharts(Map<String, Map<String, Integer>> result, String folder) {
        for (String key : result.keySet()) {
            String name = fixChartName(key);

            JFreeChart chart = vis.createBarChart(name, result.get(key));
            vis.saveChart(name, chart, folder);
            vis.displayChart(chart);
        }
    }

    // Fixes names of all charts
    private String fixChartName(String key) {
        String[] split = key.split("-");
        String name = "";

        if (split[1].equals("Frequency")) {
            // simple frequency
            name = "Frequency of ";
            name += fixColumnName(split[0]);
        } else if (split[5].equals("ComplexFrequency")) {
            // complex frequency
            name = "Frequency of ";
            name += fixColumnName(split[0]);
            name += " when ";
            name += fixColumnName(split[2]);
            name += " is ";
            name += split[4];
        }

        return name;
    }

    // Replaces column with readable name
    private String fixColumnName(String col) {
        String name = "";

        switch (col) {
            case "rules":
                name = "Ruleset";
                break;
            case "result":
                name = "Game Result";
                break;
            case "result_detailed":
                name = "Detailed Game Result";
                break;
            case "eco":
                name = "Eco";
                break;
            case "colour":
                name = "Colour";
                break;
            case "time_control":
                name = "Time Control";
                break;
            case "time_class":
                name = "Time Class";
                break;
            default:
                throw new RuntimeException("Unexpected chart name:" + name);
        }

        return name;
    }
}

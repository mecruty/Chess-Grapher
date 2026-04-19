package io.github.mecruty.analyzer;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.JFreeChart;

import io.github.mecruty.analyzer.frequencyAnalyzers.ComplexFrequencyAnalyzer;
import io.github.mecruty.analyzer.frequencyAnalyzers.SimpleFrequencyAnalyzer;
import io.github.mecruty.analyzer.regressionAnalyzers.CorrelationAnalyzer;
import io.github.mecruty.analyzer.regressionAnalyzers.LogisticRegressionAnalyzer;

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

        analyzeRegressionWeights();

        analyzeDiffCorrelation();

        System.out.println("Data analyzed!");
    }

    public void analyzeDiffCorrelation() {
        CorrelationAnalyzer ca = new CorrelationAnalyzer(csv);
        double result = ca.analyze();

        // Essentially Pearson's R
        System.out.println("Point Biserial: " + result);
    }

    public void compareCorrelations(List<List<String>> otherCsv) {
        CorrelationAnalyzer ca = new CorrelationAnalyzer(csv);
        double[] result = ca.compare(otherCsv);

        System.out.println(result[0] + " " + result[1]);
    }

    private void analyzeRegressionWeights() {
        LogisticRegressionAnalyzer lga = new LogisticRegressionAnalyzer(csv);
        Map<String, Double> result = lga.analyze();

        createRegressionChart(result, "regression");
        System.out.println("Regression Training Score: " + lga.score());
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
            makeBarChart(key, result.get(key), folder, "Count");
        }
    }

    // Creates, saves, and displays weights from regression
    // 2 decimals of accuracy
    private void createRegressionChart(Map<String, Double> result, String folder) {
        Map<String, Integer> resultScaled = new HashMap<>();

        for (String key : result.keySet()) {
            resultScaled.put(key, (int) (result.get(key) * 100));
        }

        makeBarChart("Regression", resultScaled, folder, "Weight");
    }

    // Creates, saves, and displays a general bar graph
    private void makeBarChart(String rawName, Map<String, Integer> map, String folder, String rowName) {
        String name = fixChartName(rawName);

        JFreeChart chart = vis.createBarChart(name, map, rowName);
        vis.saveChart(name, chart, folder);
        vis.displayChart(chart);
    }

    // Fixes names of all charts
    private String fixChartName(String key) {
        String[] split = key.split("-");
        String name = "";

        if (split[0].equals("Regression")) {
            // regression
            name = "Regression Weights";
        } else if (split[1].equals("Frequency")) {
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

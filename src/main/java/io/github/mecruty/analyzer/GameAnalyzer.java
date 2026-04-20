package io.github.mecruty.analyzer;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    File dir;

    public GameAnalyzer(String username, List<List<String>> csv) {
        this.csv = csv;
        this.username = username;
        vis = new ChartVisualizer(this.username);

        dir = new File("./data/" + username + "/visualization");

        // Creates the directory for the general visualization
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
    }

    public void analyzeAll() {
        analyzeAllSimpleFrequency();
        // example for now TODO
        analyzeComplexFrequency("time_class", "bullet");

        analyzeRegressionWeights();

        analyzeDiffCorrelation();

        System.out.println("Data analyzed!");
    }

    // NOTE: use carefully, data recollection required after
    // deletes all data from a user
    public void deleteAllData() {
        File userDir = new File("./data/" + username);
        if (userDir.exists()) {
            deleteFolder(userDir);
        }
    }

    // deletes everything in the visualization folder
    public void deleteAnalyses() {
        if (dir.exists()) {
            deleteFolder(dir);
        }
    }

    // recursively deletes everything in folder
    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    // default to 10 bins
    public void analyzeDiffCorrelation() {
        analyzeDiffCorrelation(10);
    }

    // creates histogram and calculates point biserial
    public void analyzeDiffCorrelation(int numBins) {
        CorrelationAnalyzer ca = new CorrelationAnalyzer(csv);
        // runs point biserial
        double result = ca.analyze();

        // creates the histogram
        createCorrelationChart(ca.histogram(numBins), "correlation");

        // Essentially Pearson's R
        System.out.println("Point Biserial: " + result);
        // TODO logging actually goes here
    }

    public void compareCorrelations(List<List<String>> otherCsv) {
        CorrelationAnalyzer ca = new CorrelationAnalyzer(csv);
        double[] result = ca.compare(otherCsv);

        System.out.println(result[0] + " " + result[1]);
    }

    public void analyzeRegressionWeights() {
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
    // 3 decimals of accuracy
    private void createRegressionChart(Map<String, Double> result, String folder) {
        Map<String, Integer> resultScaled = new HashMap<>();

        for (String key : result.keySet()) {
            resultScaled.put(key, (int) (result.get(key) * 1000));
        }

        makeBarChart("Regression", resultScaled, folder, "Weight");
    }

    // Creates, saves, and displays percentage winrates compared to rating diff
    private void createCorrelationChart(Map<String, Double> result, String folder) {
        Map<String, Integer> resultScaled = new LinkedHashMap<>();

        int count = 0;
        for (String key : result.keySet()) {
            resultScaled.put(key, (int) (result.get(key) * 100));
            count++;
        }

        makeHistogram("Correlation-of-ratingDiff-and-winrate-" + count + "-bins", resultScaled, folder, "Winrate (%)");
    }

    // Creates, saves, and displays a histogram
    private void makeHistogram(String rawName, Map<String, Integer> map, String folder, String rowName) {
        String name = fixChartName(rawName);

        JFreeChart chart = vis.createHistogram(name, map, rowName);
        vis.saveChart(name, chart, folder);
        vis.displayChart(chart);
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
        } else if (split[0].equals("Correlation")) {
            name = "(Own. rating - Opp. rating) vs. Winrate  (" + split[5] + " bins)";
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

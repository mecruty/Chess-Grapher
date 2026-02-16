package io.github.mecruty.analyzer.frequencyAnalyzers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrequencyAnalyzer {
    protected List<List<String>> csv;

    public FrequencyAnalyzer(List<List<String>> csv) {
        this.csv = csv;
    }

    protected List<String> getColumn(String name) {
        // Collect values
        int index = csv.get(0).indexOf(name);
        List<String> values = new ArrayList<>();
        csv.forEach((game) -> values.add(game.get(index)));

        // Remove the first row (column names)
        values.remove(0);
        return values;
    }

    protected Map<String, Integer> countFrequency(List<String> values) {
        Map<String, Integer> analysis = new HashMap<String, Integer>();
        for (String value : values) {
            analysis.putIfAbsent(value, 0);
            analysis.compute(value, (k, v) -> v += 1);
        }

        return analysis;
    }
}

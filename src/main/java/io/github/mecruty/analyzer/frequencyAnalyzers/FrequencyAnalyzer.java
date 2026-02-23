package io.github.mecruty.analyzer.frequencyAnalyzers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.mecruty.analyzer.Analyzer;

public class FrequencyAnalyzer extends Analyzer {
    public FrequencyAnalyzer(List<List<String>> csv) {
        super(csv);
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

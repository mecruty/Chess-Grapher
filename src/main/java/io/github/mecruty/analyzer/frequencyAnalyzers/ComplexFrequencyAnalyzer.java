package io.github.mecruty.analyzer.frequencyAnalyzers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Assuming a filter by this, what is the distribution of something else (pie chart)
public class ComplexFrequencyAnalyzer extends FrequencyAnalyzer {
    public ComplexFrequencyAnalyzer(List<List<String>> csv) {
        super(csv);
    }

    public Map<String, Map<String, Integer>> analyze(String filterKey, String filterValue) {
        List<List<String>> filteredData = new ArrayList<>();

        if (filterKey.equals("results_detailed")); // TODO currently will fail

        // Find column of key to be filtered
        int index = getRow(0).indexOf(filterKey);
        if (index == -1) throw new RuntimeException("Filter variable does not exist");
        
        // Add column names
        filteredData.add(getRow(0));

        for (int i = 1; i < csv.size(); i++) {
            if (getRow(i).get(index).equals(filterValue)) {
                filteredData.add(getRow(i));
            }
        }

        if (filteredData.size() == 1) throw new RuntimeException("Filter value never appears");

        // with new data
        SimpleFrequencyAnalyzer sfa = new SimpleFrequencyAnalyzer(filteredData);

        return renameComplexFrequencies(filterKey, filterValue, sfa.analyzeAll());
    }

    // renames xxx-Frequency into xxx-FilteredByWhen-filterKey-Is-filterValue-ComplexFrequency
    private Map<String, Map<String, Integer>> renameComplexFrequencies(String filterKey, String filterValue, Map<String, Map<String, Integer>> sf) {
        Map<String, Map<String, Integer>> renamed = new HashMap<>();

        for (Map.Entry<String, Map<String, Integer>> entry : sf.entrySet()) {
            String key = entry.getKey();
            key = key.substring(0, key.indexOf("-"));

            key += "-FilteredByWhen-" + filterKey + "-Is-" + filterValue + "-ComplexFrequency";

            renamed.put(key, entry.getValue());
        }

        return renamed;
    }
}

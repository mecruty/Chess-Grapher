package io.github.mecruty.analyzer.frequencyAnalyzers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Assuming a filter by this, what is the distribution of something else (pie chart)
public class ComplexFrequencyAnalyzer extends FrequencyAnalyzer {
    public ComplexFrequencyAnalyzer(List<List<String>> csv) {
        super(csv);
    }

    public Map<String, Map<String, Integer>> analyze(String filterKey, String filterValue) {
        List<String> filterData;
        List<String> data;

        // switch if needed in future
        switch (filterKey) {
            default:
                filterData = getColumn(filterKey);
                break;
        }

        List<String> filteredData = new ArrayList<>();
        for (int i = 0; i < filterData.size(); i++) {
            if (filterData.get(i).equals(filterValue)) {
                filteredData.add(data.get(i));
            }
        }

        return Map.of("COLUMN" + "-FilteredByWhen-" + filterKey + "-Is-" + filterValue + "-ComplexFrequency", countFrequency(filteredData));
    }

    private List<String> collectWinrate() {
        List<String> data = getColumn("result");
        
        for (String value : data) {
            switch (value) {
                case "checkmate", "resigned":
                    
                    break;
            }
        }

        return null;
        //TODO
    }

    private List<String> collectResultDetailed() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'collectResultDetailed'");
    }
}

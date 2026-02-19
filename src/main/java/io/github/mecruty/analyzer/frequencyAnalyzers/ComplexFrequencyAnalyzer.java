package io.github.mecruty.analyzer.frequencyAnalyzers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Assuming a filter by this, what is the distribution of something else (pie chart)
public class ComplexFrequencyAnalyzer extends FrequencyAnalyzer {
    public ComplexFrequencyAnalyzer(List<List<String>> csv) {
        super(csv);
    }

    public Map<String, Map<String, Integer>> analyze(String filterKey, String filterValue, String dataKey) {
        List<String> filterData;
        List<String> data;

        // switch if needed in future
        switch (filterKey) {
            default:
                filterData = getColumn(filterKey);
                break;
        }

        // extra cases
        switch (dataKey) {
            case "winrate":
                data = collectWinrate();
                break;
            case "result_detailed":
                data = collectResultDetailed();
                break;
            default:
                data = getColumn(dataKey);
                break;
        }

        List<String> filteredData = new ArrayList<>();
        for (int i = 0; i < filterData.size(); i++) {
            if (filterData.get(i).equals(filterValue)) {
                filteredData.add(data.get(i));
            }
        }

        return Map.of(dataKey + "-FilteredBy-" + filterValue + "-For-" + filterKey, countFrequency(filteredData));
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

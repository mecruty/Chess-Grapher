package io.github.mecruty.analyzer.frequencyAnalyzers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Essentially the things that could be measured with bar charts
public class SimpleFrequencyAnalyzer extends FrequencyAnalyzer {
    public SimpleFrequencyAnalyzer(List<List<String>> csv) {
        super(csv);
    }

    public Map<String, Map<String, Integer>> analyzeAll() {
        Map<String, Map<String, Integer>> analysis = new HashMap<>();

        analysis.put("rules-Frequency", analyzeRules());
        analysis.put("result-Frequency", analyzeResult());
        analysis.put("resultDetailed-Frequency", analyzeResultDetailed());
        //analysis.put("eco-Frequency", analyzeEco());
        analysis.put("colour-Frequency", analyzeColour());
        analysis.put("timeControl-Frequency", analyzeTimeControl());
        analysis.put("timeClass-Frequency", analyzeTimeClass());

        return analysis;
    }

    private Map<String, Integer> analyzeRules() {
        List<String> values = getColumn("rules");

        return countFrequency(values);
    }

    // captures only player results
    private Map<String, Integer> analyzeResult() {
        List<String> values = getColumn("result");

        return countFrequency(values);
    }

    // captures results from both player and opponent
    private Map<String, Integer> analyzeResultDetailed() {
        List<String> values = getColumn("result");

        // Values for the opponent
        List<String> opponentValues = getColumn("opponent_result");

        // Adds the manner in which the opponent lost (opponent_result) if result is win
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);

            if (value.equals("win")) {
                values.set(i, value + "(" + opponentValues.get(i) + ")");
            }
        }
        
        return countFrequency(values);
    }

    // too many categories
    @SuppressWarnings("unused")
    // eco is opening
    private Map<String, Integer> analyzeEco() {
        List<String> values = getColumn("eco");

        // Removes the "https://www.chess.com/openings/" from each opening
        // Unless there was no opening
        int indexToRemove = "https://www.chess.com/openings/".length();
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);

            if (!value.equals("none")) {
                values.set(i, value.substring(indexToRemove));
            }
        }
        
        return countFrequency(values);
    }

    private Map<String, Integer> analyzeColour() {
        List<String> values = getColumn("colour");
        
        return countFrequency(values);
    }

    private Map<String, Integer> analyzeTimeControl() {
        List<String> values = getColumn("time_control");
        
        return countFrequency(values);
    }

    private Map<String, Integer> analyzeTimeClass() {
        List<String> values = getColumn("time_class");
        
        return countFrequency(values);
    }
}

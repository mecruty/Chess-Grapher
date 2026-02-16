package io.github.mecruty.analyzer.frequencyAnalyzers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Essentially the things that could be measured with pie charts
public class SimpleFrequencyAnalyzer extends FrequencyAnalyzer {
    public SimpleFrequencyAnalyzer(List<List<String>> csv) {
        super(csv);
    }

    public Map<String, Map<String, Integer>> analyzeAll() {
        Map<String, Map<String, Integer>> analysis = new HashMap<>();

        analysis.put("rulesFrequency", analyzeRules());
        analysis.put("resultFrequency", analyzeResult());
        analysis.put("resultDetailedFrequency", analyzeResultDetailed());
        //analysis.put("ecoFrequency", analyzeEco());
        analysis.put("colourFrequency", analyzeColour());
        analysis.put("timeControlFrequency", analyzeTimeControl());
        analysis.put("timeClassFrequency", analyzeTimeClass());

        return analysis;
    }

    public Map<String, Integer> analyzeRules() {
        List<String> values = getColumn("rules");

        return countFrequency(values);
    }

    public Map<String, Integer> analyzeResult() {
        List<String> values = getColumn("result");

        return countFrequency(values);
    }

    public Map<String, Integer> analyzeResultDetailed() {
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

    public Map<String, Integer> analyzeEco() {
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

    public Map<String, Integer> analyzeColour() {
        List<String> values = getColumn("colour");
        
        return countFrequency(values);
    }

    public Map<String, Integer> analyzeTimeControl() {
        List<String> values = getColumn("time_control");
        
        return countFrequency(values);
    }

    public Map<String, Integer> analyzeTimeClass() {
        List<String> values = getColumn("time_class");
        
        return countFrequency(values);
    }
}

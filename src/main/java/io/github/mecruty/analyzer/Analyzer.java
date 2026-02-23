package io.github.mecruty.analyzer;

import java.util.ArrayList;
import java.util.List;

public class Analyzer {
    protected List<List<String>> csv;

    public Analyzer(List<List<String>> csv) {
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

    // Mainly for clarity
    // Note: 0th row is column names
    protected List<String> getRow(int index) {
        return csv.get(index);
    }
}

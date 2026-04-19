package io.github.mecruty.analyzer.regressionAnalyzers;

import java.util.List;

import io.github.mecruty.analyzer.Analyzer;

import smile.math.MathEx;

// Calculations for finding the importance of rating difference
public class CorrelationAnalyzer extends Analyzer {

    public CorrelationAnalyzer(List<List<String>> csv) {
        super(csv);
    }

    public double analyze() {
        return findCorrelation(csv);
    }

    // returns self correlation first
    public double[] compare(List<List<String>> otherCsv) {
        double[] correlations = {findCorrelation(csv), findCorrelation(otherCsv)};

        return correlations;
    }

    // Calculates pearsons r (or in this case, point biserial, as win/loss is binary)
    private double findCorrelation(List<List<String>> data) {
        int rowCount = data.size() - 1;
        if (rowCount <= 1) return 0.0;

        double[] ratingDiff = new double[rowCount];
        double[] results = new double[rowCount];

        int[] rating = getColumn("rating").stream().mapToInt(Integer::parseInt).toArray();
        int[] oppRating = getColumn("opponent_rating").stream().mapToInt(Integer::parseInt).toArray();
        String[] result = getColumn("result").toArray(new String[0]);

        for (int i = 0; i < rowCount; i++) {
            ratingDiff[i] = rating[i] - oppRating[i];
            results[i] = result[i].equals("win") ? 1 : 0;
        }

        return MathEx.cor(ratingDiff, results);
    }
}

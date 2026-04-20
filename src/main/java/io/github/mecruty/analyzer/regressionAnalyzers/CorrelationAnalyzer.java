package io.github.mecruty.analyzer.regressionAnalyzers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import io.github.mecruty.analyzer.Analyzer;

import smile.math.MathEx;

// Calculations for finding the importance of rating difference
// Draws count as non-wins
public class CorrelationAnalyzer extends Analyzer {

    public CorrelationAnalyzer(List<List<String>> csv) {
        super(csv);
    }

    public double analyze() {
        return findCorrelation();
    }

    // returns self correlation first
    public double[] compare(List<List<String>> otherCsv) {
        double[] correlations = {findCorrelation(), findCorrelation(otherCsv)};

        return correlations;
    }

    // defaults to current csv
    private double findCorrelation() {
        return findCorrelation(csv);
    }

    // Calculates pearsons r (or in this case, point biserial, as win/loss is binary)
    private double findCorrelation(List<List<String>> data) {
        // could be simplified
        // however, this benefits off of superclass
        List<List<String>> currCsv = csv;
        csv = data;

        double[] ratingDiff = new double[csv.size() - 1];
        double[] results = new double[csv.size() - 1];
        formatColumns(ratingDiff, results);

        csv = currCsv;
        return MathEx.cor(ratingDiff, results);
    }

    // Returns bins with equal quantity games and winrates for each bin
    // TODO complex
    public Map<String, Double> histogram(int numBins) {
        int rowCount = csv.size() - 1;
        
        double[] ratingDiff = new double[rowCount];
        double[] results = new double[rowCount];
        formatColumns(ratingDiff, results);

        // workaround for sorting the two arrays together
        Integer[] indices = IntStream.range(0, rowCount).boxed().toArray(Integer[]::new);
        Arrays.sort(indices, Comparator.comparingDouble(i -> ratingDiff[i]));

        double[] sortedDiff = new double[rowCount];
        double[] sortedRes = new double[rowCount];

        for (int i = 0; i < rowCount; i++) {
            sortedDiff[i] = ratingDiff[indices[i]];
            sortedRes[i] = results[indices[i]];
        }

        
        // Creating bins
        Map<String, Double> bins = new LinkedHashMap<>();
        int gamesPerBin = rowCount / numBins;

        for (int i = 0; i < numBins; i++) {
            int countedGames = i * gamesPerBin;
            int countedGamesEnd = countedGames + gamesPerBin;

            int startDiff = (int) sortedDiff[countedGames];
            int endDiff = (int) sortedDiff[countedGamesEnd - 1];
            double winrate = 0;
            for (int j = countedGames; j < countedGamesEnd; j++) {
                winrate += sortedRes[j];
            }

            int divideBy = gamesPerBin;

            // on last loop
            if (i == numBins - 1) {
                int extra = rowCount - (countedGamesEnd);
                for (int j = countedGamesEnd; j < countedGamesEnd + extra; j++) {
                    winrate += sortedRes[j];
                }
                divideBy += extra;
            }

            winrate /= divideBy;

            bins.put(startDiff + " to " + endDiff, winrate);
        }

        return bins;
    }

    // Formats the ratingDiff and results arrays
    private void formatColumns(double[] ratingDiff, double[] results) {
        int rowCount = csv.size() - 1;

        int[] rating = getColumn("rating").stream().mapToInt(Integer::parseInt).toArray();
        int[] oppRating = getColumn("opponent_rating").stream().mapToInt(Integer::parseInt).toArray();
        String[] result = getColumn("result").toArray(new String[0]);

        for (int i = 0; i < rowCount; i++) {
            ratingDiff[i] = rating[i] - oppRating[i];
            results[i] = result[i].equals("win") ? 1 :
             (result[i].equals("agreed") ||
              result[i].equals("repetition") ||
              result[i].equals("insufficient") ||
              result[i].equals("timevsinsufficient") ||
              result[i].equals("stalemate")) ? 0.5 : 0;
        }
    }
}

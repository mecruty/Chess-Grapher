package io.github.mecruty.analyzer.regressionAnalyzers;

import java.util.*;
import java.util.stream.Collectors;

import io.github.mecruty.analyzer.Analyzer;

import smile.classification.LogisticRegression;
import smile.data.DataFrame;
import smile.data.transform.InvertibleColumnTransform;
import smile.feature.transform.Standardizer;

public class LogisticRegressionAnalyzer extends Analyzer {
    private LogisticRegression.Binomial model;
    private InvertibleColumnTransform scaler;
    // keeping order cleaner
    private final Map<String, List<String>> categoriesMap = new LinkedHashMap<>();
    private String[] featureNames;
    private double[][] x;
    private int[] y;

    public LogisticRegressionAnalyzer(List<List<String>> csv) {
        super(csv);
    }

    public Map<String, Double> analyze() {
        prepareData();
        
        DataFrame featuresDf = DataFrame.of(x, featureNames);
        this.scaler = Standardizer.fit(featuresDf);
        DataFrame scaledFeaturesDf = scaler.apply(featuresDf);

        double[][] scaledX = scaledFeaturesDf.toArray();

        this.model = (LogisticRegression.Binomial) LogisticRegression.fit(scaledX, y);

        Map<String, Double> coefficientsMap = new HashMap<>();
        double[] modelCoefficients = model.coefficients();

        for (int i = 0; i < featureNames.length; i++) {
            coefficientsMap.put(featureNames[i], modelCoefficients[i]);
            System.out.println(featureNames[i] + " " + modelCoefficients[i]);
        }
        
        if (modelCoefficients.length > featureNames.length) {
            coefficientsMap.put("Intercept", modelCoefficients[modelCoefficients.length - 1]);
            System.out.println("Intercept " + modelCoefficients[modelCoefficients.length - 1]);
        }

        return coefficientsMap;
    }
    
    // Collects useful rows from data and one hot encodes them
    private void prepareData() {
        int rowCount = csv.size() - 1;
        
        String[] rules = getColumn("rules").toArray(new String[0]);
        String[] setup = getColumn("initial_setup").toArray(new String[0]);
        String[] tc = getColumn("time_control").toArray(new String[0]);
        String[] tclass = getColumn("time_class").toArray(new String[0]);

        if (categoriesMap.isEmpty()) {
            categoriesMap.put("Ruleset", Arrays.stream(rules).distinct().sorted().collect(Collectors.toList()));
            categoriesMap.put("Setup", Arrays.stream(setup).distinct().sorted().collect(Collectors.toList()));
            categoriesMap.put("Time Control", Arrays.stream(tc).distinct().sorted().collect(Collectors.toList()));
            categoriesMap.put("Time Class", Arrays.stream(tclass).distinct().sorted().collect(Collectors.toList()));
        }

        List<String> namesList = new ArrayList<>(Arrays.asList("Rating", "Rating Difference", "isRated", "isBlack"));
        for (String key : categoriesMap.keySet()) {
            for (String cat : categoriesMap.get(key)) {
                namesList.add(key + "_" + cat);
            }
        }
        this.featureNames = namesList.toArray(new String[0]);

        this.x = new double[rowCount][featureNames.length];
        this.y = new int[rowCount];

        int[] rating = getColumn("rating").stream().mapToInt(Integer::parseInt).toArray();
        int[] oppRating = getColumn("opponent_rating").stream().mapToInt(Integer::parseInt).toArray();
        int[] rated = getColumn("rated").stream().mapToInt(i -> i.equals("TRUE") ? 1 : 0).toArray();
        int[] black = getColumn("colour").stream().mapToInt(i -> i.equals("black") ? 1 : 0).toArray();
        String[] resStr = getColumn("result").toArray(new String[0]);

        for (int i = 0; i < rowCount; i++) {
            x[i][0] = rating[i];
            x[i][1] = rating[i] - oppRating[i]; // rating difference
            x[i][2] = rated[i];
            x[i][3] = black[i];
            
            int col = 4;
            col = fillOHE(x[i], col, "Ruleset", rules[i]);
            col = fillOHE(x[i], col, "Setup", setup[i]);
            col = fillOHE(x[i], col, "Time Control", tc[i]);
            col = fillOHE(x[i], col, "Time Class", tclass[i]);
            
            y[i] = resStr[i].equals("win") ? 1 : 0;
        }
    }

    // Implementing own one hot encoding
    // Smile a bit unstable
    private int fillOHE(double[] row, int startCol, String categoryName, String value) {
        List<String> cats = categoriesMap.get(categoryName);
        for (int i = 0; i < cats.size(); i++) {
            if (cats.get(i).equals(value)) {
                row[startCol + i] = 1.0;
            } else {
                row[startCol + i] = 0.0;
            }
        }
        return startCol + cats.size();
    }

    // Tests model accuracy on the current csv
    public double score() {
        if (model == null) throw new IllegalStateException("Model must be trained before scoring.");

        prepareData();
        double[][] scaledX = scaler.apply(DataFrame.of(x, featureNames)).toArray();
        int correct = 0;
        for (int i = 0; i < scaledX.length; i++) {
            if (model.predict(scaledX[i]) == y[i]) correct++;
        }
        return (double) correct / y.length;
    }

    public int[] predict() {
        if (model == null) throw new IllegalStateException("Model must be trained before making predictions.");

        prepareData();
        double[][] scaledX = scaler.apply(DataFrame.of(x, featureNames)).toArray();
        return model.predict(scaledX);
    }
    
    public void setData(List<List<String>> newCsv) {
        this.csv = newCsv;
    }
}

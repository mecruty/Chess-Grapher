package io.github.mecruty.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import io.github.mecruty.analyzer.GameAnalyzer;
import io.github.mecruty.collection.CSVParser;
import io.github.mecruty.collection.GamesCollector;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "ChessAnalyzer", mixinStandardHelpOptions = true, version = "1.0.0")
public class CLI {
    static Scanner sc = new Scanner(System.in);

    @Parameters(index = "0", description = "Chess.com username of selected player")
    static private String username;

    @Command(name = "collect", description = "Collects and updates any missing game data from player")
    void collect(
        @Option(names = {"-a", "--all"}, description = "Collects ALL game data from player (May take several minutes)")
        boolean all
    ) {
        // TODO implement usecase for all tag
        GamesCollector gc = new GamesCollector(username);
        CSVParser csvp = new CSVParser(username);
        csvp.saveJSONToCSV(gc.collectAll());
        System.out.println("Sucessfully collected and updated" + username + "games.");
    }
    
    @Command(name = "analyze", description = "Analyzes game data from player, data must be already collected")
    void analyze(
        @Option(names = {"-s", "--simple"}, description = "Runs simple frequency graphs")
        boolean simple,

        @Option(names = {"-c", "--complex"}, description = "Runs complex frequency graphs, needs further input")
        boolean complex,

        @Option(names = {"-r", "--regression"}, description = "Runs linear regression to find weights for each variable")
        boolean regression,

        @Option(names = {"-a", "--all"}, description = "Runs all analyses")
        boolean all

    ) {
        CSVParser csvp = new CSVParser(username);
        GameAnalyzer ga;
        try {
            ga = new GameAnalyzer(username, csvp.loadCSV());
            
            if (!(all || simple || complex || regression)) {
                System.out.println("Defaulting to simple frequency analysis...");
                ga.analyzeAllSimpleFrequency();
            } else if (all) {
                ga.analyzeAll();
            } else {
                if (simple) ga.analyzeAllSimpleFrequency();
                if (complex) runComplexFrequencyAnalysis(ga);
                if (regression); // TODO do lol
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: This player's data does not exist");
            System.out.println("Try collecting player data with \"collect\" first");
        } catch (IOException e) {
            System.out.println("Error: Data could not be analyzed");
            System.out.println("Try recollecting player data");
        }
    }

    private void runComplexFrequencyAnalysis(GameAnalyzer ga) {
        System.out.println("Choose a variable to filter by:");
        String filterKey = sc.nextLine();
        System.out.println("Choose a specific outcome to filter by:");
        String filterValue = sc.nextLine();
        ga.analyzeComplexFrequency(filterKey, filterValue);
    }
}
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

    @Command(name = "collect", description = "Collects and updates any missing game data from player")
    void collect(
        @Parameters(index = "0", description = "Chess.com username of selected player")
        String username,

        @Option(names = {"-a", "--all"}, description = "Collects ALL game data from player (May take several minutes)")
        boolean all
    ) {
        // TODO implement usecase for all tag
        GamesCollector gc = new GamesCollector(username);
        CSVParser csvp = new CSVParser(username);
        csvp.saveJSONToCSV(gc.collectAll());
        System.out.println("Sucessfully collected and updated " + username + "\'s games."); 
    }

    @Command(name = "analyze", description = "Analyzes game data from player, data must be already collected")
    void analyze(
        @Parameters(index = "0", description = "Chess.com username of selected player")
        String username,

        @Option(names = {"-s", "--simple"}, description = "Runs simple frequency graphs")
        boolean simple,

        @Option(names = {"-c", "--complex"}, description = "Runs complex frequency graphs, needs further input")
        boolean complex,

        @Option(names = {"-a", "--all"}, description = "Runs all analyses")
        boolean all

    ) {
        CSVParser csvp = new CSVParser(username);
        GameAnalyzer ga;
        try {
            ga = new GameAnalyzer(username, csvp.loadCSV());
            
            if (!(all || simple || complex)) {
                System.out.println("Defaulting to simple frequency analysis...");
                ga.analyzeAllSimpleFrequency();
            } else if (all) {
                ga.analyzeAllFrequency();
            } else {
                if (simple) ga.analyzeAllSimpleFrequency();
                if (complex) runComplexFrequencyAnalysis(ga);
            }
        } catch (FileNotFoundException e) {
            FileNotFoundErrorMessage();
        } catch (IOException e) {
            IOExceptionMessage();
        }

        System.out.println("Data analyzed!");
    }

    private void runComplexFrequencyAnalysis(GameAnalyzer ga) {
        System.out.println("Choose a variable to filter by:");
        String filterKey = sc.nextLine();
        System.out.println("Choose a specific outcome to filter by:");
        String filterValue = sc.nextLine();
        ga.analyzeComplexFrequency(filterKey, filterValue);
    }

    @Command(name = "regression", description = "Builds and can predict ")
    void regression(
        @Parameters(index = "0", description = "Chess.com username of selected player")
        String username
    ) {
        CSVParser csvp = new CSVParser(username);
        GameAnalyzer ga;
        try {
            ga = new GameAnalyzer(username, csvp.loadCSV());
            ga.analyzeRegressionWeights();
        } catch (FileNotFoundException e) {
            FileNotFoundErrorMessage();
        } catch (IOException e) {
            IOExceptionMessage();
        }

        System.out.println("Data analyzed!");
    }

    @Command(name = "correlate", description = "Finds and graphs correlation between rating difference and win/loss. >0.3 is considered very well correlated")
    void correlate(
        @Parameters(index = "0", description = "Chess.com username of selected player")
        String username,

        @Parameters(index = "1", arity = "0..1", description = "Second username (required for comparison)")
        String username2,

        @Parameters(index = "1", arity = "0..1", description = "Number of bins to be used in histogram (default: 10)")
        String binCount,

        @Option(names = {"-c", "--compare"}, description = "Compares correlation value between two users, not graphed")
        boolean compare
    ) {
        CSVParser csvp = new CSVParser(username);
        GameAnalyzer ga;
        try {
            ga = new GameAnalyzer(username, csvp.loadCSV());

            if (!compare) {
                if (binCount == null) {
                    System.out.println("Defaulting to 10 bins for histogram...");
                    ga.analyzeDiffCorrelation();
                } else {
                    int count;
                    try {
                        count = Integer.parseInt(binCount);
                        if (count <= 0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("The number of bins must be an integer larger than 0");
                        return;
                    }
                    ga.analyzeDiffCorrelation(count);
                }
            } else {
                if (username2 == null) {
                    System.out.println("Error: Second username needed");
                    return;
                }

                CSVParser csvp2 = new CSVParser(username2);
                ga.compareCorrelations(csvp2.loadCSV());
            }
        } catch (FileNotFoundException e) {
            FileNotFoundErrorMessage();
        } catch (IOException e) {
            IOExceptionMessage();
        }

        System.out.println("Data analyzed!");
    }

    @Command(name = "delete", description = "Deletes all charts and analyzed info of player")
    void delete(
        @Parameters(index = "0", description = "Chess.com username of selected player")
        String username,

        @Option(names = {"-a", "--all"}, description = "Deletes ALL player game data")
        boolean all
    ) {
        System.out.println("IMPORTANT: this will delete player/analysis data");
        System.out.println("Would you like to continue? (y/[n])");
        String next = sc.nextLine();
        if (!next.trim().toLowerCase().equals("y")) {
            System.out.println("Delete operation cancelled");
            return;
        }
        System.out.println("Confirmed, deleting...");

        // csv not needed
        GameAnalyzer ga = new GameAnalyzer(username, null);
        if (!all) {
            ga.deleteAnalyses();
        } else {
            ga.deleteAllData();
        }

        System.out.println("Successfully deleted!");
    }

    private void FileNotFoundErrorMessage() {
        System.out.println("Error: This player's data does not exist");
        System.out.println("Try collecting player data with \"collect\" first");
    }

    private void IOExceptionMessage() {
        System.out.println("Error: Data could not be analyzed");
        System.out.println("Try recollecting player data");
    }
}
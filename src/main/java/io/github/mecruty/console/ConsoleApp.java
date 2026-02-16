package io.github.mecruty.console;

import java.util.Scanner;

import io.github.mecruty.analyzer.GameAnalyzer;
import io.github.mecruty.collection.CSVParser;
import io.github.mecruty.collection.GamesCollector;

public class ConsoleApp {
    private String username;
    private GamesCollector gc;
    private CSVParser csvp;
    private GameAnalyzer ga;

    static Scanner sc = new Scanner(System.in);

    public ConsoleApp() {
        run();
    }

    private void run() {
        System.out.println("Enter username:");
        username = sc.nextLine();
        gc = new GamesCollector(username);
        csvp = new CSVParser(username);

        System.out.println("Would you like to collect data?");
        if (sc.nextLine().equals("y")) {
            csvp.saveJSONToCSV(gc.collectAll());
            System.out.println("Done");
        }

        System.out.println("Would you like to load data?");
        if (sc.nextLine().equals("y")) {
            // TODO temporary
            ga = new GameAnalyzer(username, csvp.loadCSV());
            ga.analyzeAll();
        }
    }
}

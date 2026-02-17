package io.github.mecruty.ui;

import io.github.mecruty.analyzer.GameAnalyzer;
import io.github.mecruty.collection.CSVParser;
import io.github.mecruty.collection.GamesCollector;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "ChessAnalyzer", mixinStandardHelpOptions = true, version = "1.0.0")
public class CLI {
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
        // TODO implement choice for which analysis (probably implement analysis first)
    ) {
        CSVParser csvp = new CSVParser(username);
        GameAnalyzer ga = new GameAnalyzer(username, csvp.loadCSV());
        ga.analyzeAll();
    }
}
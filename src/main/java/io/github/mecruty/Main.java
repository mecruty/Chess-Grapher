package io.github.mecruty;

import io.github.mecruty.ui.CLI;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        //int exitCode = 
        new CommandLine(new CLI()).execute(args);
        //System.exit(exitCode);
    }
}
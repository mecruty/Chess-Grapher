package io.github.mecruty.collection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;

public class CSVParser {
    private final String username;
    private final String destination;

    public CSVParser(String username) {
        this.username = username;
        destination = "./data/" + username + "/" + username + ".csv";
    }

    public void saveJSONToCSV(JSONObject json) {
        JSONArray docs = json.getJSONArray("games");

        File file = new File(destination);

        // Creates the parent directory if nonexistent (folder for the user)
        // Make sure there should be a parent directory
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        String csv = CDL.toString(docs);

        try {
            PrintWriter writer = new PrintWriter(file);
            writer.print(csv);
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Writing to csv failed");
        }
    }

    public List<List<String>> loadCSV() {
        List<List<String>> csv = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(destination))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                csv.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new RuntimeException("Loading csv failed");
        }

        return csv;
    }

    public String getUsername() {
        return username;
    }
}

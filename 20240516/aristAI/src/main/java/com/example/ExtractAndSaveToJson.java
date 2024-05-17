package com.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ExtractAndSaveToJson {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/youtube_data";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "12345";
    private static final String OUTPUT_JSON_FILE = "all_transcriptionsV2.json";

    public static void main(String[] args) {
        // Load the MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Extract data and save to JSON
        JsonArray transcriptionsArray = new JsonArray();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT timestampId, sentence FROM transcriptions")) {

            while (resultSet.next()) {
                String timestampId = resultSet.getString("timestampId");
                String sentence = resultSet.getString("sentence");

                JsonObject transcriptionObject = new JsonObject();
                // transcriptionObject.addProperty("timestampId", timestampId);
                // transcriptionObject.addProperty("sentence", sentence);
                transcriptionObject.addProperty(timestampId, sentence);

                transcriptionsArray.add(transcriptionObject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Write to JSON file
        try (FileWriter fileWriter = new FileWriter(OUTPUT_JSON_FILE)) {
            new Gson().toJson(transcriptionsArray, fileWriter);
            System.out.println("Transcriptions saved to " + OUTPUT_JSON_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

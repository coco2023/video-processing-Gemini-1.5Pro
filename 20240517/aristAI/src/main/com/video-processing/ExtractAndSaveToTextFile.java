package com.example;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ExtractAndSaveToTextFile {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/youtube_data";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "12345";
    private static final String OUTPUT_TEXT_FILE = "train/transcriptions.txt";

    public static void main(String[] args) {
        // Load the MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Extract data and save to text file
        try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT timestampId, sentence FROM transcriptions");
             FileWriter fileWriter = new FileWriter(OUTPUT_TEXT_FILE)) {

            while (resultSet.next()) {
                String timestampId = resultSet.getString("timestampId");
                String sentence = resultSet.getString("sentence");

                // Write to text file in the desired format
                fileWriter.write(timestampId + "|" + sentence + System.lineSeparator());
            }

            System.out.println("Transcriptions saved to " + OUTPUT_TEXT_FILE);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}

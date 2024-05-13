package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class YouTubeVideoDownloader {

    private static final String DOWNLOAD_DIRECTORY = "outputs/videos/";

    public static void downloadVideo(String videoUrl, String videoId) {

        // Define the filename using the videoKey and the current timestamp
        String filename = videoId + ".%(ext)s";
        String outputPath = DOWNLOAD_DIRECTORY + filename;
        
        ProcessBuilder processBuilder = new ProcessBuilder("yt-dlp", videoUrl, "-o", outputPath);

        try {
            Process process = processBuilder.start();

            // Read the output from the command
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait to ensure the process is finished
            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);

        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred during video download: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        String jsonFilePath = "outputs/20240512225916_Speeches_videoLinks.json";

        try {

            // Ensure the download directory exists
            new File(DOWNLOAD_DIRECTORY).mkdirs();

            // Parse JSON file
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> videoUrls = mapper.readValue(new File(jsonFilePath), Map.class);
         
            // Iterate through the map and download each video
            for (Map.Entry<String, String> entry : videoUrls.entrySet()) {
                String videoId = entry.getKey();
                String videoUrl = entry.getValue();
                downloadVideo(videoUrl, videoId);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

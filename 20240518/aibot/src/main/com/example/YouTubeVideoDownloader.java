package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class YouTubeVideoDownloader {

    private static final String DOWNLOAD_DIRECTORY = "outputs/videos/";

    public static void main(String[] args) {
        String jsonFilePath = "outputs/videoLinks.json";

        ensureDirectoryExists(DOWNLOAD_DIRECTORY);

        Map<String, String> videoUrls = parseJsonFile(jsonFilePath);
        if (videoUrls != null) {
            downloadAllVideos(videoUrls);
        }
    }

    private static void ensureDirectoryExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private static Map<String, String> parseJsonFile(String jsonFilePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(jsonFilePath), Map.class);
        } catch (IOException e) {
            System.err.println("Failed to parse JSON file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static void downloadAllVideos(Map<String, String> videoUrls) {
        for (Map.Entry<String, String> entry : videoUrls.entrySet()) {
            String videoId = entry.getKey();
            String videoUrl = entry.getValue();
            System.out.println("Downloading video: " + videoId + " from URL: " + videoUrl);
            downloadVideo(videoUrl, videoId);
        }
    }

    private static void downloadVideo(String videoUrl, String videoId) {
        String filename = videoId + ".%(ext)s";
        String outputPath = DOWNLOAD_DIRECTORY + filename;
        ProcessBuilder processBuilder = new ProcessBuilder("yt-dlp", "--no-playlist", videoUrl, "-o", outputPath);

        try {
            Process process = processBuilder.start();
            logProcessOutput(process);
            int exitCode = process.waitFor();
            System.out.println("Exited with error code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred during video download: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void logProcessOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
        }
    }
}

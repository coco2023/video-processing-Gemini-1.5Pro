package com.example;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.protobuf.Duration;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TranscribeWavToDB {

    private static final String JSON_PATH = "outputs/woven-sequence-422021-4dd531c27e17.json";
    private static final String BUCKET_NAME = "video1-20240502";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/youtube_data";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "12345";

    public static void main(String[] args) {
        try (SpeechClient speechClient = initializeSpeechClient();
             Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {

            List<String> segmentFiles = listSegmentFiles();
            for (String segmentFile : segmentFiles) {
                String gcsUri = "gs://" + BUCKET_NAME + "/" + segmentFile;
                transcribeAndSaveToDB(speechClient, gcsUri, segmentFile, connection);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static SpeechClient initializeSpeechClient() throws IOException {
        GoogleCredentials credentials;
        try (FileInputStream serviceAccountStream = new FileInputStream(JSON_PATH)) {
            credentials = GoogleCredentials.fromStream(serviceAccountStream);
        }

        SpeechSettings settings = SpeechSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        return SpeechClient.create(settings);
    }

    private static List<String> listSegmentFiles() throws IOException {
        GoogleCredentials credentials;
        try (FileInputStream serviceAccountStream = new FileInputStream(JSON_PATH)) {
            credentials = GoogleCredentials.fromStream(serviceAccountStream);
        }

        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        Bucket bucket = storage.get(BUCKET_NAME);

        List<String> segmentFiles = new ArrayList<>();
        for (Blob blob : bucket.list().iterateAll()) {
            String name = blob.getName();
            if (name.endsWith(".wav")) {
                segmentFiles.add(name);
            }
        }
        return segmentFiles;
    }

    private static void transcribeAndSaveToDB(SpeechClient speechClient, String gcsUri, String fileName, Connection connection) throws IOException, SQLException {
        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(AudioEncoding.LINEAR16)
                .setSampleRateHertz(44100)
                .setLanguageCode("en-US")
                .setEnableWordTimeOffsets(true)
                .build();
        RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(gcsUri).build();

        RecognizeResponse response = speechClient.recognize(config, audio);
        List<SpeechRecognitionResult> results = response.getResultsList();

        saveTranscriptionsToDB(results, fileName, connection);
    }

    private static void saveTranscriptionsToDB(List<SpeechRecognitionResult> results, String fileName, Connection connection) throws SQLException {
        String insertSQL = "INSERT INTO transcriptions (start_time, end_time, timestampId, sentence, file_name) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            for (SpeechRecognitionResult result : results) {
                for (SpeechRecognitionAlternative alternative : result.getAlternativesList()) {
                    List<WordInfo> words = alternative.getWordsList();

                    StringBuilder sentence = new StringBuilder();
                    Duration startTime = null;
                    Duration endTime = null;

                    for (int i = 0; i < words.size(); i++) {
                        WordInfo wordInfo = words.get(i);
                        if (startTime == null) {
                            startTime = wordInfo.getStartTime();
                        }
                        endTime = wordInfo.getEndTime();
                        sentence.append(wordInfo.getWord()).append(" ");

                        if (wordInfo.getWord().endsWith(".") || i == words.size() - 1) {
                            String sentenceString = sentence.toString().trim();
                            String startTimestamp = formatTimestamp(startTime);
                            String endTimestamp = formatTimestamp(endTime);
                            String timestampId = fileName.replace(".wav", "") + "#" + startTimestamp + "#" + endTimestamp;

                            preparedStatement.setString(1, startTimestamp);
                            preparedStatement.setString(2, endTimestamp);
                            preparedStatement.setString(3, timestampId);
                            preparedStatement.setString(4, sentenceString);
                            preparedStatement.setString(5, fileName);
                            preparedStatement.executeUpdate();

                            sentence.setLength(0);
                            startTime = null;
                            endTime = null;
                        }
                    }
                }
            }
        }
    }

    private static String formatTimestamp(Duration duration) {
        long seconds = duration.getSeconds();
        int nanos = duration.getNanos();
        return String.format("%02d:%02d:%02d.%03d",
                seconds / 3600,
                (seconds % 3600) / 60,
                seconds % 60,
                nanos / 1000000);
    }
}

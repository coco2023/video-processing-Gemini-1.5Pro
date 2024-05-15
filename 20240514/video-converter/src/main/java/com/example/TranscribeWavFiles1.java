package com.example;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

public class TranscribeWavFiles1 {
    public static void main(String[] args) throws IOException {
        String folderPath = "outputs/videos/wav/";
        File folder = new File(folderPath);
        Map<String, List<String>> transcriptions = new HashMap<>();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".wav"));
            if (files != null) {
                for (File file : files) {
                    List<String> transcription = transcribeAudio(file.getPath());
                    transcriptions.put(file.getName().replace(".wav", ""), transcription);
                }
                saveTranscriptionsToJson(transcriptions, "transcriptions.json");
            } else {
                System.out.println("No .wav files found in the directory.");
            }
        } else {
            System.out.println("Directory does not exist.");
        }
    }

    public static List<String> transcribeAudio(String filePath) throws IOException {
        List<String> transcriptions = new ArrayList<>();

        final GoogleCredentials credentials;
        try {
            credentials = GoogleCredentials.getApplicationDefault();
        } catch (IOException e) {
            // Fallback to explicitly loading the credentials from the file
            try (FileInputStream credentialsStream = new FileInputStream(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"))) {
                credentials = GoogleCredentials.fromStream(credentialsStream);
            }
        }

        // Instantiates a client with credentials
        SpeechSettings speechSettings = SpeechSettings.newBuilder().setCredentialsProvider(() -> credentials).build();
        try (SpeechClient speechClient = SpeechClient.create(speechSettings)) {
            // Reads the audio file into memory
            Path path = Paths.get(filePath);
            byte[] data = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(data);

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-US")
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Performs speech recognition on the audio file
            RecognizeResponse response = speechClient.recognize(config, audio);
            for (SpeechRecognitionResult result : response.getResultsList()) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                transcriptions.add(alternative.getTranscript());
            }
        }
        return transcriptions;
    }
    
    public static void saveTranscriptionsToJson(Map<String, List<String>> transcriptions, String outputPath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(outputPath)) {
            gson.toJson(transcriptions, writer);
        } catch (IOException e) {
            System.err.println("Failed to save transcriptions to JSON file: " + e.getMessage());
        }
    }

}

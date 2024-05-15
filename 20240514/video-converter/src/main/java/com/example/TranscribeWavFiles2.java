package com.example;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognizeRequest;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.protobuf.ByteString;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.io.File;

public class TranscribeWavFiles2 {
    public static void main(String[] args) {
        String folderPath = "outputs/videos/wav/";
        String jsonPath = "outputs/woven-sequence-422021-4dd531c27e17.json";

        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    try {
                        transcribeAudio(file.getAbsolutePath(), jsonPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void transcribeAudio(String filePath, String jsonPath) throws Exception {
        // Load the service account key JSON file
        GoogleCredentials credentials;
        try (FileInputStream serviceAccountStream = new FileInputStream(jsonPath)) {
            credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
        }

        SpeechSettings settings = SpeechSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        try (SpeechClient speechClient = SpeechClient.create(settings)) {
            // Reads the audio file into memory
            ByteString audioBytes = ByteString.readFrom(new FileInputStream(filePath));

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-US")
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

            // Performs speech recognition on the audio file
            RecognizeRequest request = RecognizeRequest.newBuilder().setConfig(config).setAudio(audio).build();
            RecognizeResponse response = speechClient.recognize(request);

            // Print the results
            for (SpeechRecognitionResult result : response.getResultsList()) {
                for (SpeechRecognitionAlternative alternative : result.getAlternativesList()) {
                    System.out.printf("Transcript: %s%n", alternative.getTranscript());
                }
            }
        }
    }
}

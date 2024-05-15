package com.example;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TranscribeWavFiles4 {

    /** Demonstrates using the Speech API to transcribe an audio file. */
    public static void main(String... args) throws Exception {
        String jsonPath = "outputs/woven-sequence-422021-4dd531c27e17.json";
        String gcsUri = "gs://video1-20240502/1ESOfxO78B8#9#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f.wav";

        try (SpeechClient speechClient = initializeSpeechClient(jsonPath)) {
            transcribeAudio(speechClient, gcsUri);
        }
    }

    public static SpeechClient initializeSpeechClient(String jsonPath) throws IOException {
        GoogleCredentials credentials;
        try (FileInputStream serviceAccountStream = new FileInputStream(jsonPath)) {
            credentials = GoogleCredentials.fromStream(serviceAccountStream);
        }

        SpeechSettings settings = SpeechSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        return SpeechClient.create(settings);
    }

    public static void transcribeAudio(SpeechClient speechClient, String gcsUri) throws IOException {
        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(AudioEncoding.LINEAR16)
                .setSampleRateHertz(44100)  // Updated sample rate to match the WAV file
                .setLanguageCode("en-US")
                .build();
        RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(gcsUri).build();

        RecognizeResponse response = speechClient.recognize(config, audio);
        List<SpeechRecognitionResult> results = response.getResultsList();

        JsonArray transcriptsArray = new JsonArray();

        for (SpeechRecognitionResult result : results) {
            for (SpeechRecognitionAlternative alternative : result.getAlternativesList()) {
                System.out.printf("Transcription: %s%n", alternative.getTranscript());

                JsonObject transcriptObject = new JsonObject();
                transcriptObject.addProperty("transcript", alternative.getTranscript());
                transcriptsArray.add(transcriptObject);
            }
        }

        // Write transcripts to a JSON file
        writeTranscriptsToJsonFile("transcription.json", transcriptsArray);
    }

    private static void writeTranscriptsToJsonFile(String fileName, JsonArray transcriptsArray) {
        Gson gson = new Gson();
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            gson.toJson(transcriptsArray, fileWriter);
            System.out.println("Transcripts saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

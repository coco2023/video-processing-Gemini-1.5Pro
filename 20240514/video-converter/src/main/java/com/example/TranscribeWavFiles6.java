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

public class TranscribeWavFiles6 {

    public static void main(String[] args) throws Exception {
          String jsonPath = "outputs/woven-sequence-422021-4dd531c27e17.json";
          String gcsUriPrefix = "gs://video1-20240502/";

        // Replace with the list of your segment file names
        String[] segmentFiles = {
            "1ESOfxO78B8#9#PL_oohi_O51Z_lORk8SCG_4x1smii5ky7f#segment#0.wav",
            // Add more segments as needed
        };

        try (SpeechClient speechClient = initializeSpeechClient(jsonPath)) {
            for (String segmentFile : segmentFiles) {
                String gcsUri = gcsUriPrefix + segmentFile;
                String outputJsonPath = "transcribe/" + "transcribes2#" + segmentFile.replace(".wav", ".json");
                transcribeAudio(speechClient, gcsUri, outputJsonPath);
            }
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

    public static void transcribeAudio(SpeechClient speechClient, String gcsUri, String outputJsonPath) throws IOException {
        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(AudioEncoding.LINEAR16)
                .setSampleRateHertz(44100)  // Ensure this matches the sample rate of the audio file
                .setLanguageCode("en-US")
                .build();
        RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(gcsUri).build();

        RecognizeResponse response = speechClient.recognize(config, audio);
        List<SpeechRecognitionResult> results = response.getResultsList();

        JsonArray transcriptsArray = new JsonArray();

        for (SpeechRecognitionResult result : results) {
            for (SpeechRecognitionAlternative alternative : result.getAlternativesList()) {
                String[] sentences = alternative.getTranscript().split("\\. ");
                for (String sentence : sentences) {
                    JsonObject transcriptObject = new JsonObject();
                    transcriptObject.addProperty("transcript", sentence.trim() + ".");
                    transcriptsArray.add(transcriptObject);
                }
            }
        }

        // Write transcripts to a JSON file
        writeTranscriptsToJsonFile(outputJsonPath, transcriptsArray);
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

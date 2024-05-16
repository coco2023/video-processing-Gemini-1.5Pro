package com.example;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.protobuf.Duration;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TranscribeWavFiles7 {

    public static void main(String[] args) throws Exception {
          String jsonPath = "outputs/woven-sequence-422021-4dd531c27e17.json";
          String gcsUriPrefix = "gs://video1-20240502/";
          String bucketName = "video1-20240502";

        try (SpeechClient speechClient = initializeSpeechClient(jsonPath)) {
          List<String> segmentFiles = listSegmentFiles(bucketName, jsonPath);
            for (String segmentFile : segmentFiles) {
                    String gcsUri = "gs://" + bucketName + "/" + segmentFile;
                String outputJsonPath = "transcribe/" + "transcribes#" + segmentFile.replace(".wav", ".json");
                transcribeAudio(speechClient, gcsUri, outputJsonPath);
            }
        }
    }

    public static List<String> listSegmentFiles(String bucketName, String jsonPath) throws IOException {
          GoogleCredentials credentials;
          try (FileInputStream serviceAccountStream = new FileInputStream(jsonPath)) {
              credentials = GoogleCredentials.fromStream(serviceAccountStream);
          }
  
          Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
          Bucket bucket = storage.get(bucketName);
  
          List<String> segmentFiles = new ArrayList<>();
          for (Blob blob : bucket.list().iterateAll()) {
              String name = blob.getName();
              if (name.endsWith(".wav")) {
                  segmentFiles.add(name);
              }
          }
          return segmentFiles;
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
          .setEnableWordTimeOffsets(true)  // Enable word time offsets
          .build();
        RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(gcsUri).build();

        RecognizeResponse response = speechClient.recognize(config, audio);
        List<SpeechRecognitionResult> results = response.getResultsList();

        JsonArray transcriptsArray = new JsonArray();

        for (SpeechRecognitionResult result : results) {
            for (SpeechRecognitionAlternative alternative : result.getAlternativesList()) {
                List<WordInfo> words = alternative.getWordsList();

                // Group words into sentences based on timestamps
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
                    // Check if the word ends with a period or if it's the last word
                    if (wordInfo.getWord().endsWith(".") || i == words.size() - 1) {
                        JsonObject transcriptObject = new JsonObject();
                        String sentenceTimestamp = formatTimestamp(startTime) + " - " + formatTimestamp(endTime);
                        String sentenceString = sentence.toString().trim();
                    //     transcriptObject.addProperty("timestamp", formatTimestamp(startTime) + " - " + formatTimestamp(endTime));
                    //     transcriptObject.addProperty("sentence", sentence.toString().trim());
                    transcriptObject.addProperty(sentenceTimestamp, sentenceString);
                        transcriptsArray.add(transcriptObject);

                        // Reset for the next sentence
                        sentence.setLength(0);
                        startTime = null;
                        endTime = null;
                    }
                }
            }
        }

        // Write transcripts to a JSON file
        writeTranscriptsToJsonFile(outputJsonPath, transcriptsArray);
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

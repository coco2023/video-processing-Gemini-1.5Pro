// package com.example;

// import com.google.auth.oauth2.GoogleCredentials;
// import com.google.cloud.speech.v1.*;
// import com.google.cloud.speech.v1.SpeechClient;
// import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
// import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
// import com.google.cloud.speech.v1.RecognitionAudio;
// import com.google.cloud.speech.v1.RecognitionConfig;
// import com.google.cloud.speech.v1.SpeechClient;
// import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
// import com.google.cloud.speech.v1.SpeechRecognitionResult;
// import com.google.cloud.speech.v1.SpeechSettings;
// import com.google.longrunning.Operation;
// import com.google.protobuf.ByteString;
// import com.google.protobuf.Duration;

// import java.io.FileInputStream;
// import java.io.IOException;
// import java.util.List;
// import java.io.File;

// public class TranscribeWavFiles3 {
//     public static void main(String[] args) {
//         String folderPath = "gs://video1-20240502/";
//         String jsonPath = "outputs/woven-sequence-422021-4dd531c27e17.json";
//         try {
//                     SpeechClient speechClient = initializeSpeechClient(jsonPath);
//                     // You can now use the speechClient to transcribe audio files
//                     System.out.println("Speech client initialized successfully.");
//                     speechClient.close();
//           } catch (IOException e) {
//                     e.printStackTrace();
//           }

//         File folder = new File(folderPath);
//         File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));

//         if (listOfFiles != null) {
//             for (File file : listOfFiles) {
//                 if (file.isFile()) {
//                     try {
//                         transcribeAudio(file.getName(), jsonPath);
//                     } catch (Exception e) {
//                         e.printStackTrace();
//                     }
//                 }
//             }
//         }
//     }

//     public static void transcribeAudio(String fileName, String jsonPath) throws Exception {
//         GoogleCredentials credentials;

//         try (FileInputStream serviceAccountStream = new FileInputStream(jsonPath)) {
//           credentials = GoogleCredentials.fromStream(serviceAccountStream);
//       }

//         SpeechSettings settings = SpeechSettings.newBuilder()
//                 .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
//                 .build();

//         try (SpeechClient speechClient = SpeechClient.create(settings)) {
//             String gcsUri = "gs://video1-20240502/" + fileName;
//             RecognitionConfig config = RecognitionConfig.newBuilder()
//                     .setEncoding(AudioEncoding.LINEAR16)
//                     .setSampleRateHertz(16000)
//                     .setLanguageCode("en-US")
//                     .build();
//             RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(gcsUri).build();

//             LongRunningRecognizeRequest request = LongRunningRecognizeRequest.newBuilder()
//                     .setConfig(config)
//                     .setAudio(audio)
//                     .build();

//             Operation operation = speechClient.longRunningRecognizeAsync(request).get();
//             LongRunningRecognizeResponse response = operation.getResponse().unpack(LongRunningRecognizeResponse.class);

//             for (SpeechRecognitionResult result : response.getResultsList()) {
//                 for (SpeechRecognitionAlternative alternative : result.getAlternativesList()) {
//                     System.out.printf("Transcript: %s%n", alternative.getTranscript());
//                 }
//             }
//         }
//     }
// }
